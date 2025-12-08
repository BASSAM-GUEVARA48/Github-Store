package zed.rainxch.githubstore.feature.auth.presentation

sealed interface AuthenticationEvents {
    data object OnNavigateToMain : AuthenticationEvents
}