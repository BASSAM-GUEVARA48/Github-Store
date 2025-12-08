package zed.rainxch.githubstore.core.presentation.utils

import java.awt.Desktop
import java.net.URI

class JvmBrowserHelper : BrowserHelper {
    override fun openUrl(
        url: String,
        onFailure: (error: String) -> Unit
    ) {
        val os = System.getProperty("os.name").lowercase()

        try {
            when {
                os.contains("linux") -> {
                    val processBuilder = ProcessBuilder("xdg-open", url)
                    processBuilder.redirectErrorStream(true)
                    processBuilder.start()
                }

                Desktop.isDesktopSupported() && Desktop.getDesktop()
                    .isSupported(Desktop.Action.BROWSE) -> {
                    Desktop.getDesktop().browse(URI(url))
                }

                else -> {
                    onFailure("Cannot open browser automatically. Please visit: $url")
                }
            }
        } catch (e: Exception) {
            onFailure("Failed to open browser: ${e.message}. Please visit: $url")
        }
    }

}
