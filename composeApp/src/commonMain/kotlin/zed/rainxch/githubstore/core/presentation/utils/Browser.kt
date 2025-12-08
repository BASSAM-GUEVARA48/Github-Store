package zed.rainxch.githubstore.core.presentation.utils

interface BrowserHelper {
    fun openUrl(
        url: String,
        onFailure: (error: String) -> Unit = { },
    )
}

