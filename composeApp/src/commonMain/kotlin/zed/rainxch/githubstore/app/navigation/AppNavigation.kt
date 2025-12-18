package zed.rainxch.githubstore.app.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.toInt
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import zed.rainxch.githubstore.feature.apps.presentation.AppsRoot
import zed.rainxch.githubstore.feature.auth.presentation.AuthenticationRoot
import zed.rainxch.githubstore.feature.details.presentation.DetailsRoot
import zed.rainxch.githubstore.feature.home.presentation.HomeRoot
import zed.rainxch.githubstore.feature.search.presentation.SearchRoot
import zed.rainxch.githubstore.feature.settings.presentation.SettingsRoot

@Composable
fun AppNavigation(
    navBackStack: SnapshotStateList<GithubStoreGraph>
) {

    NavDisplay(
        backStack = navBackStack,
        onBack = {
            navBackStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<GithubStoreGraph.HomeScreen> {
                HomeRoot(
                    onNavigateToSearch = {
                        navBackStack.add(GithubStoreGraph.SearchScreen)
                    },
                    onNavigateToSettings = {
                        navBackStack.add(GithubStoreGraph.SettingsScreen)
                    },
                    onNavigateToApps = {
                        navBackStack.add(GithubStoreGraph.AppsScreen)
                    },
                    onNavigateToDetails = { repo ->
                        navBackStack.add(
                            GithubStoreGraph.DetailsScreen(
                                repositoryId = repo.id.toInt()
                            )
                        )
                    }
                )
            }

            entry<GithubStoreGraph.SearchScreen> {
                SearchRoot(
                    onNavigateBack = {
                        navBackStack.removeLastOrNull()
                    },
                    onNavigateToDetails = { repo ->
                        navBackStack.add(
                            GithubStoreGraph.DetailsScreen(
                                repositoryId = repo.id.toInt()
                            )
                        )
                    }
                )
            }

            entry<GithubStoreGraph.DetailsScreen> { args ->
                DetailsRoot(
                    onNavigateBack = {
                        navBackStack.removeLastOrNull()
                    },
                    onOpenRepositoryInApp = { repoId ->
                        navBackStack.add(
                            GithubStoreGraph.DetailsScreen(
                                repositoryId = repoId
                            )
                        )
                    },
                    viewModel = koinViewModel {
                        parametersOf(args.repositoryId)
                    }
                )
            }

            entry<GithubStoreGraph.AuthenticationScreen> {
                AuthenticationRoot(
                    onNavigateToHome = {
                        navBackStack.clear()
                        navBackStack.add(GithubStoreGraph.HomeScreen)
                    }
                )
            }

            entry<GithubStoreGraph.SettingsScreen> {
                SettingsRoot(
                    onNavigateBack = {
                        navBackStack.removeLastOrNull()
                    }
                )
            }

            entry<GithubStoreGraph.AppsScreen> {
                AppsRoot(
                    onNavigateBack = {
                        navBackStack.removeLastOrNull()
                    },
                    onNavigateToRepo = { repoId ->
                        navBackStack.add(
                            GithubStoreGraph.DetailsScreen(
                                repositoryId = repoId.toInt()
                            )
                        )
                    }
                )
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(Spring.DampingRatioLowBouncy)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = spring(Spring.DampingRatioLowBouncy)
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(Spring.DampingRatioLowBouncy)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(Spring.DampingRatioLowBouncy)
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(Spring.DampingRatioLowBouncy)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(Spring.DampingRatioLowBouncy)
            )
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
