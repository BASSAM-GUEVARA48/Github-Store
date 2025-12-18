package zed.rainxch.githubstore.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface GithubStoreGraph: NavKey {
    @Serializable
    data object HomeScreen : GithubStoreGraph

    @Serializable
    data object SearchScreen : GithubStoreGraph

    @Serializable
    data object AuthenticationScreen : GithubStoreGraph

    @Serializable
    data class DetailsScreen(
        val repositoryId: Int
    ) : GithubStoreGraph

    @Serializable
    data object SettingsScreen : GithubStoreGraph

    @Serializable
    data object AppsScreen : GithubStoreGraph
}