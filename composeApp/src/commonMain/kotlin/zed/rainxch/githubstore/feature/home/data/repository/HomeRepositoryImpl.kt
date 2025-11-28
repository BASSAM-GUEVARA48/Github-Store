package zed.rainxch.githubstore.feature.home.data.repository

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.core.data.mappers.toSummary
import zed.rainxch.githubstore.core.data.model.GithubRepoNetworkModel
import zed.rainxch.githubstore.core.data.model.GithubRepoSearchResponse
import zed.rainxch.githubstore.core.domain.Platform
import zed.rainxch.githubstore.core.domain.model.PlatformType
import zed.rainxch.githubstore.feature.home.domain.repository.HomeRepository
import zed.rainxch.githubstore.feature.home.domain.repository.PaginatedRepos
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class HomeRepositoryImpl(
    private val githubNetworkClient: HttpClient,
    private val platform: Platform
) : HomeRepository {

    override fun getTrendingRepositories(page: Int): Flow<PaginatedRepos> =
        searchReposWithInstallersFlow(
            baseQuery = "stars:>500 archived:false",
            sort = "stars",
            order = "desc",
            startPage = page
        )

    override fun getLatestUpdated(page: Int): Flow<PaginatedRepos> =
        searchReposWithInstallersFlow(
            baseQuery = "stars:>50 archived:false",
            sort = "updated",
            order = "desc",
            startPage = page
        )

    @OptIn(ExperimentalTime::class)
    override fun getNew(page: Int): Flow<PaginatedRepos> {
        val sixMonthsAgo = Clock.System.now()
            .minus(30.days)
            .toLocalDateTime(TimeZone.UTC)
            .date

        return searchReposWithInstallersFlow(
            baseQuery = "stars:>5 archived:false created:>=$sixMonthsAgo",
            sort = "created",
            order = "desc",
            startPage = page
        )
    }

    private fun searchReposWithInstallersFlow(
        baseQuery: String,
        sort: String,
        order: String,
        startPage: Int,
        desiredCount: Int = 10
    ): Flow<PaginatedRepos> = flow {
        val results = mutableListOf<GithubRepoSummary>()
        var currentApiPage = startPage
        val perPage = 100
        val semaphore = Semaphore(25)
        val maxPagesToFetch = 5
        var pagesFetchedCount = 0
        var lastEmittedCount = 0

        val query = buildSimplifiedQuery(baseQuery)
        Logger.d { "Query: $query | Sort: $sort | Page: $startPage" }

        while (results.size < desiredCount && pagesFetchedCount < maxPagesToFetch) {
            currentCoroutineContext().ensureActive()

            try {
                val response: GithubRepoSearchResponse =
                    githubNetworkClient.get("/search/repositories") {
                        parameter("q", query)
                        parameter("sort", sort)
                        parameter("order", order)
                        parameter("per_page", perPage)
                        parameter("page", currentApiPage)
                    }.body()

                Logger.d { "API Page $currentApiPage: Got ${response.items.size} repos" }

                if (response.items.isEmpty()) {
                    Logger.d { "No more items from API, breaking" }
                    break
                }

                val candidates = response.items
                    .map { repo -> repo to calculatePlatformScore(repo) }
                    .filter { it.second > 0 }
                    .take(50)
                    .map { it.first }

                Logger.d { "Checking ${candidates.size} candidates for installers" }

                coroutineScope {
                    val deferredResults = candidates.map { repo ->
                        async {
                            semaphore.withPermit {
                                withTimeoutOrNull(5000) {
                                    checkRepoHasInstallers(repo)
                                }
                            }
                        }
                    }

                    for (deferred in deferredResults) {
                        currentCoroutineContext().ensureActive()

                        val result = deferred.await()
                        if (result != null) {
                            results.add(result)
                            Logger.d { "Found installer repo: ${result.fullName} (${results.size}/$desiredCount)" }

                            if (results.size % 3 == 0 || results.size >= desiredCount) {
                                val newItems = results.subList(lastEmittedCount, results.size)

                                if (newItems.isNotEmpty()) {
                                    emit(
                                        PaginatedRepos(
                                            repos = newItems.toList(),
                                            hasMore = true,
                                            nextPageIndex = currentApiPage + 1
                                        )
                                    )
                                    Logger.d { "Emitted ${newItems.size} repos (total: ${results.size})" }
                                    lastEmittedCount = results.size
                                }
                            }

                            if (results.size >= desiredCount) {
                                Logger.d { "Reached desired count, breaking" }
                                break
                            }
                        }
                    }
                }

                if (results.size >= desiredCount || response.items.size < perPage) {
                    Logger.d { "Breaking: results=${results.size}, response size=${response.items.size}" }
                    break
                }

                currentApiPage++
                pagesFetchedCount++

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Logger.e { "Search failed: ${e.message}" }
                e.printStackTrace()
                break
            }
        }

        if (results.size > lastEmittedCount) {
            val finalBatch = results.subList(lastEmittedCount, results.size)
            val finalHasMore = pagesFetchedCount < maxPagesToFetch && results.size >= desiredCount
            emit(
                PaginatedRepos(
                    repos = finalBatch.toList(),
                    hasMore = finalHasMore,
                    nextPageIndex = if (finalHasMore) currentApiPage + 1 else currentApiPage
                )
            )
            Logger.d { "Final emit: ${finalBatch.size} repos (total: ${results.size})" }
        } else if (results.isEmpty()) {
            emit(
                PaginatedRepos(
                    repos = emptyList(),
                    hasMore = false,
                    nextPageIndex = currentApiPage
                )
            )
            Logger.d { "No results found" }
        }
    }.flowOn(Dispatchers.IO)

    private fun buildSimplifiedQuery(baseQuery: String): String {
        val topic = when (platform.type) {
            PlatformType.ANDROID -> "android"
            PlatformType.WINDOWS -> "desktop"
            PlatformType.MACOS -> "macos"
            PlatformType.LINUX -> "linux"
        }

        return "$baseQuery topic:$topic"
    }

    private fun calculatePlatformScore(repo: GithubRepoNetworkModel): Int {
        var score = 5
        val topics = repo.topics.orEmpty().map { it.lowercase() }
        val language = repo.language?.lowercase()
        val desc = repo.description?.lowercase() ?: ""

        when (platform.type) {
            PlatformType.ANDROID -> {
                if (topics.contains("android")) score += 10
                if (topics.contains("mobile")) score += 5
                if (language == "kotlin" || language == "java") score += 5
                if (desc.contains("android") || desc.contains("apk")) score += 3
            }

            PlatformType.WINDOWS, PlatformType.MACOS, PlatformType.LINUX -> {
                if (topics.any { it in setOf("desktop", "electron", "app", "gui", "compose-desktop") }) score += 10
                if (topics.contains("cross-platform") || topics.contains("multiplatform")) score += 8
                if (language in setOf("kotlin", "c++", "rust", "c#", "swift", "dart")) score += 5
                if (desc.contains("desktop") || desc.contains("application")) score += 3
            }
        }

        return score
    }

    private suspend fun checkRepoHasInstallers(repo: GithubRepoNetworkModel): GithubRepoSummary? {
        return try {
            // Get recent releases to find the latest stable one
            val allReleases: List<GithubReleaseNetworkModel> = githubNetworkClient
                .get("/repos/${repo.owner.login}/${repo.name}/releases") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("per_page", 10) // Check up to 10 recent releases
                }
                .body()

            // Find the latest STABLE release (not draft, not prerelease)
            val stableRelease = allReleases.firstOrNull {
                it.draft != true && it.prerelease != true
            }

            // If no stable release exists, reject this repo
            if (stableRelease == null || stableRelease.assets.isEmpty()) {
                return null
            }

            // Check if the stable release has the installers we need
            val relevantAssets = stableRelease.assets.filter { asset ->
                val name = asset.name.lowercase()
                when (platform.type) {
                    PlatformType.ANDROID -> name.endsWith(".apk")
                    PlatformType.WINDOWS -> name.endsWith(".msi") || name.endsWith(".exe") || name.contains(".exe")
                    PlatformType.MACOS -> name.endsWith(".dmg") || name.endsWith(".pkg")
                    PlatformType.LINUX -> name.endsWith(".appimage") || name.endsWith(".deb") || name.endsWith(".rpm")
                }
            }

            if (relevantAssets.isNotEmpty()) {
                repo.toSummary()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @Serializable
    private data class GithubReleaseNetworkModel(
        val assets: List<AssetNetworkModel>,
        val draft: Boolean? = null,
        val prerelease: Boolean? = null,
        @SerialName("published_at") val publishedAt: String? = null
    )

    @Serializable
    private data class AssetNetworkModel(
        val name: String
    )
}