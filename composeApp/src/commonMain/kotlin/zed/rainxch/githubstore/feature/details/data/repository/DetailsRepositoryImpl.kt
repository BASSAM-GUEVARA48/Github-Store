package zed.rainxch.githubstore.feature.details.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zed.rainxch.githubstore.core.domain.model.GithubAsset
import zed.rainxch.githubstore.core.domain.model.GithubRelease
import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.core.domain.model.GithubUser
import zed.rainxch.githubstore.feature.details.domain.model.RepoStats
import zed.rainxch.githubstore.feature.details.domain.repository.DetailsRepository

class DetailsRepositoryImpl(
    private val github: HttpClient
) : DetailsRepository {

    override suspend fun getRepositoryById(id: Long): GithubRepoSummary {
        val repo: RepoByIdNetwork = github.get("/repositories/$id") {
            header(HttpHeaders.Accept, "application/vnd.github+json")
        }.body()

        return GithubRepoSummary(
            id = repo.id,
            name = repo.name,
            fullName = repo.fullName,
            owner = GithubUser(
                login = repo.owner.login,
                avatarUrl = repo.owner.avatarUrl,
                htmlUrl = repo.owner.htmlUrl
            ),
            description = repo.description,
            htmlUrl = repo.htmlUrl,
            stargazersCount = repo.stars,
            forksCount = repo.forks,
            language = repo.language,
            topics = repo.topics,
            releasesUrl = "https://api.github.com/repos/${repo.owner.login}/${repo.name}/releases{/id}",
            updatedAt = repo.updatedAt
        )
    }

    override suspend fun getLatestPublishedRelease(owner: String, repo: String): GithubRelease? {
        val releases: List<ReleaseNetwork> = github.get("/repos/$owner/$repo/releases") {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            parameter("per_page", 10)
        }.body()

        val latest = releases
            .asSequence()
            .filter { (it.draft != true) && (it.prerelease != true) }
            .sortedByDescending { it.publishedAt ?: it.createdAt ?: "" }
            .firstOrNull()
            ?: return null

        return latest.toDomain()
    }

    override suspend fun getReadme(owner: String, repo: String): String? {
        return try {
            github.get("https://raw.githubusercontent.com/$owner/$repo/master/README.md").body()
        } catch (t: Throwable) {
            try {
                github.get("https://raw.githubusercontent.com/$owner/$repo/main/README.md").body()
            } catch (e: Throwable) {
                null
            }
        }
    }

    override suspend fun getRepoStats(owner: String, repo: String): RepoStats {
        val info: RepoInfoNetwork = github.get("/repos/$owner/$repo") {
            header(HttpHeaders.Accept, "application/vnd.github+json")
        }.body()

        // Infer contributors count using Link header trick with per_page=1
        val contributorsCount = try {
            val response = github.get("/repos/$owner/$repo/contributors") {
                header(HttpHeaders.Accept, "application/vnd.github+json")
                parameter("per_page", 1)
                parameter("anon", 1)
            }
            val link = response.headers["Link"]
            if (link != null && link.contains("rel=\"last\"")) {
                // Example: <https://api.github.com/...&page=34>; rel="last"
                val lastPart = link.split(',').firstOrNull { it.contains("rel=\"last\"") }
                val pageParam = lastPart?.substringAfter("page=")?.substringBefore('>')
                pageParam?.toIntOrNull() ?: 0
            } else {
                // If no Link header, either 0 or 1 contributors
                1
            }
        } catch (_: Throwable) { 0 }

        return RepoStats(
            stars = info.stars,
            forks = info.forks,
            openIssues = info.openIssues,
            contributors = contributorsCount
        )
    }

    // Network models
    @Serializable
    private data class RepoByIdNetwork(
        val id: Long,
        val name: String,
        @SerialName("full_name") val fullName: String,
        val owner: OwnerNetwork,
        val description: String? = null,
        @SerialName("html_url") val htmlUrl: String,
        @SerialName("stargazers_count") val stars: Int,
        @SerialName("forks_count") val forks: Int,
        val language: String? = null,
        val topics: List<String>? = null,
        @SerialName("updated_at") val updatedAt: String,
    )

    @Serializable
    private data class OwnerNetwork(
        val login: String,
        @SerialName("avatar_url") val avatarUrl: String,
        @SerialName("html_url") val htmlUrl: String
    )

    @Serializable
    private data class RepoInfoNetwork(
        @SerialName("stargazers_count") val stars: Int,
        @SerialName("forks_count") val forks: Int,
        @SerialName("open_issues_count") val openIssues: Int,
    )

    @Serializable
    private data class ReleaseNetwork(
        val id: Long,
        @SerialName("tag_name") val tagName: String,
        val name: String? = null,
        val draft: Boolean? = null,
        val prerelease: Boolean? = null,
        val author: OwnerNetwork,
        @SerialName("published_at") val publishedAt: String? = null,
        @SerialName("created_at") val createdAt: String? = null,
        val body: String? = null,
        @SerialName("tarball_url") val tarballUrl: String,
        @SerialName("zipball_url") val zipballUrl: String,
        @SerialName("html_url") val htmlUrl: String,
        val assets: List<AssetNetwork>
    )

    @Serializable
    private data class AssetNetwork(
        val id: Long,
        val name: String,
        @SerialName("content_type") val contentType: String,
        val size: Long,
        @SerialName("browser_download_url") val downloadUrl: String,
        val uploader: OwnerNetwork
    )

    private fun ReleaseNetwork.toDomain(): GithubRelease = GithubRelease(
        id = id,
        tagName = tagName,
        name = name,
        author = GithubUser(
            login = author.login,
            avatarUrl = author.avatarUrl,
            htmlUrl = author.htmlUrl
        ),
        publishedAt = publishedAt ?: createdAt ?: "",
        description = body
            // 1. Replace the <details> and <summary> tags
            ?.replace("<details>", "")
            ?.replace("</details>", "")
            ?.replace("<summary>", "")
            ?.replace("</summary>", "")
            // 2. Remove the trailing \r\n (Carriage Return + Line Feed) which can sometimes cause issues
            ?.replace("\r\n", "\n"),
        assets = assets.map { it.toDomain() },
        tarballUrl = tarballUrl,
        zipballUrl = zipballUrl,
        htmlUrl = htmlUrl
    )

    private fun AssetNetwork.toDomain(): GithubAsset = GithubAsset(
        id = id,
        name = name,
        contentType = contentType,
        size = size,
        downloadUrl = downloadUrl,
        uploader = GithubUser(
            login = uploader.login,
            avatarUrl = uploader.avatarUrl,
            htmlUrl = uploader.htmlUrl
        )
    )
}