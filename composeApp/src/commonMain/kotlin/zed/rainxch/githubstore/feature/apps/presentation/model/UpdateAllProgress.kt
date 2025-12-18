package zed.rainxch.githubstore.feature.apps.presentation.model

data class UpdateAllProgress(
    val current: Int,
    val total: Int,
    val currentAppName: String
)