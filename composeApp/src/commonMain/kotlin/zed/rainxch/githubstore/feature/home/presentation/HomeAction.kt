package zed.rainxch.githubstore.feature.home.presentation

import zed.rainxch.githubstore.core.domain.model.GithubRepoSummary
import zed.rainxch.githubstore.feature.home.presentation.model.HomeCategory

sealed interface HomeAction {
    data object Refresh : HomeAction
    data object Retry : HomeAction
    data object LoadMore : HomeAction
    data object OnSearchClick : HomeAction
    data object OnSettingsClick : HomeAction
    data object OnAppsClick : HomeAction
    data class SwitchCategory(val category: HomeCategory) : HomeAction
    data class OnRepositoryClick(val repo: GithubRepoSummary) : HomeAction
}