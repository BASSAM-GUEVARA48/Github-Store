package zed.rainxch.githubstore.feature.details.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import zed.rainxch.githubstore.core.domain.model.PlatformType
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException

class DesktopInstaller(
    private val platform: PlatformType
) : Installer {

    override suspend fun isSupported(extOrMime: String): Boolean {
        val ext = extOrMime.lowercase().removePrefix(".")
        return when (platform) {
            PlatformType.WINDOWS -> ext in listOf("msi", "exe")
            PlatformType.MACOS -> ext in listOf("dmg", "pkg")
            PlatformType.LINUX -> ext in listOf("appimage", "deb", "rpm")
            else -> false
        }
    }

    override suspend fun ensurePermissionsOrThrow(extOrMime: String) = withContext(Dispatchers.IO) {
        val ext = extOrMime.lowercase().removePrefix(".")

        if (platform == PlatformType.LINUX && ext == "appimage") {
            try {
                val tempFile = File.createTempFile("appimage_perm_test", ".tmp")
                try {
                    val canSetExecutable = tempFile.setExecutable(true)
                    if (!canSetExecutable) {
                        throw IllegalStateException(
                            "Unable to set executable permissions. AppImage installation requires " +
                                    "the ability to make files executable."
                        )
                    }
                } finally {
                    tempFile.delete()
                }
            } catch (e: IOException) {
                throw IllegalStateException(
                    "Failed to verify permission capabilities for AppImage installation: ${e.message}",
                    e
                )
            } catch (e: SecurityException) {
                throw IllegalStateException(
                    "Security restrictions prevent setting executable permissions for AppImage files.",
                    e
                )
            }
        }
    }

    override suspend fun install(filePath: String, extOrMime: String) =
        withContext(Dispatchers.IO) {
            val file = File(filePath)
            if (!file.exists()) {
                throw IllegalStateException("File not found: $filePath")
            }

            val ext = extOrMime.lowercase().removePrefix(".")

            when (platform) {
                PlatformType.WINDOWS -> installWindows(file, ext)
                PlatformType.MACOS -> installMacOS(file, ext)
                PlatformType.LINUX -> installLinux(file, ext)
                else -> throw UnsupportedOperationException("Installation not supported on $platform")
            }
        }

    private fun installWindows(file: File, ext: String) {
        when (ext) {
            "msi" -> {
                val pb = ProcessBuilder("msiexec", "/i", file.absolutePath)
                pb.start()
            }

            "exe" -> {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file)
                } else {
                    val pb = ProcessBuilder(file.absolutePath)
                    pb.start()
                }
            }

            else -> throw IllegalArgumentException("Unsupported Windows installer: .$ext")
        }
    }

    private fun installMacOS(file: File, ext: String) {
        when (ext) {
            "dmg" -> {
                val pb = ProcessBuilder("open", file.absolutePath)
                pb.start()
            }

            "pkg" -> {
                val pb = ProcessBuilder("open", file.absolutePath)
                pb.start()
            }

            else -> throw IllegalArgumentException("Unsupported macOS installer: .$ext")
        }
    }

    private fun installLinux(file: File, ext: String) {
        when (ext) {
            "appimage" -> {
                installAppImage(file)
            }

            "deb" -> {
                installDebPackage(file)
            }

            "rpm" -> {
                installRpmPackage(file)
            }

            else -> throw IllegalArgumentException("Unsupported Linux installer: .$ext")
        }
    }

    private fun installDebPackage(file: File) {
        Logger.d { "Installing DEB package: ${file.absolutePath}" }

        val installMethods = listOf(
            listOf("pkexec", "apt", "install", "-y", file.absolutePath),

            listOf("pkexec", "sh", "-c", "dpkg -i '${file.absolutePath}' || apt-get install -f -y"),

            listOf("gdebi-gtk", file.absolutePath),

            null
        )

        for (method in installMethods) {
            if (method == null) {
                openTerminalForDebInstall(file.absolutePath)
                return
            }

            try {
                Logger.d { "Trying installation method: ${method.joinToString(" ")}" }
                val process = ProcessBuilder(method).start()
                val exitCode = process.waitFor()

                if (exitCode == 0) {
                    Logger.d { "DEB package installed successfully" }
                    return
                } else {
                    Logger.w { "Installation method failed with exit code: $exitCode" }
                }
            } catch (e: IOException) {
                Logger.w { "Installation method not available: ${e.message}" }
            }
        }

        throw IOException("Could not install DEB package. Please install it manually.")
    }

    private fun installRpmPackage(file: File) {
        Logger.d { "Installing RPM package: ${file.absolutePath}" }

        val installMethods = listOf(
            listOf("pkexec", "dnf", "install", "-y", file.absolutePath),

            listOf("pkexec", "yum", "install", "-y", file.absolutePath),

            listOf("pkexec", "zypper", "install", "-y", file.absolutePath),

            listOf("pkexec", "rpm", "-i", file.absolutePath),

            null
        )

        for (method in installMethods) {
            if (method == null) {
                openTerminalForRpmInstall(file.absolutePath)
                return
            }

            try {
                Logger.d { "Trying installation method: ${method.joinToString(" ")}" }
                val process = ProcessBuilder(method).start()
                val exitCode = process.waitFor()

                if (exitCode == 0) {
                    Logger.d { "RPM package installed successfully" }
                    return
                } else {
                    Logger.w { "Installation method failed with exit code: $exitCode" }
                }
            } catch (e: IOException) {
                Logger.w { "Installation method not available: ${e.message}" }
            }
        }

        throw IOException("Could not install RPM package. Please install it manually.")
    }

    private fun openTerminalForDebInstall(filePath: String) {
        Logger.d { "Opening terminal for DEB installation" }

        // First, try to detect available terminals
        val availableTerminals = detectAvailableTerminals()

        if (availableTerminals.isEmpty()) {
            // No terminal found - try graphical notification or fallback
            Logger.e { "No terminal emulator found on system" }

            // Try to show a notification with instructions
            tryShowNotification(
                "Installation Required",
                "Please install manually: sudo dpkg -i '$filePath' && sudo apt-get install -f -y"
            )

            // Try to copy command to clipboard
            tryCopyToClipboard("sudo dpkg -i '$filePath' && sudo apt-get install -f -y")

            throw IOException(
                "No terminal emulator found. Please install manually:\n" +
                        "sudo dpkg -i '$filePath' && sudo apt-get install -f -y"
            )
        }

        val command = "echo 'Installing DEB package...'; sudo dpkg -i '$filePath' && sudo apt-get install -f -y; echo ''; echo 'Installation complete. Press Enter to close...'; read"

        for (terminal in availableTerminals) {
            try {
                Logger.d { "Trying terminal: ${terminal.name}" }
                val processBuilder = when (terminal) {
                    Terminal.GNOME_TERMINAL -> ProcessBuilder(
                        "gnome-terminal", "--", "bash", "-c", command
                    )
                    Terminal.KONSOLE -> ProcessBuilder(
                        "konsole", "-e", "bash", "-c", command
                    )
                    Terminal.XTERM -> ProcessBuilder(
                        "xterm", "-e", "bash", "-c", command
                    )
                    Terminal.XFCE4_TERMINAL -> ProcessBuilder(
                        "xfce4-terminal", "-e", "bash -c \"$command\""
                    )
                    Terminal.ALACRITTY -> ProcessBuilder(
                        "alacritty", "-e", "bash", "-c", command
                    )
                    Terminal.KITTY -> ProcessBuilder(
                        "kitty", "bash", "-c", command
                    )
                    Terminal.TILIX -> ProcessBuilder(
                        "tilix", "-e", "bash -c \"$command\""
                    )
                    Terminal.MATE_TERMINAL -> ProcessBuilder(
                        "mate-terminal", "-e", "bash -c \"$command\""
                    )
                }

                processBuilder.start()
                Logger.d { "Terminal opened successfully: ${terminal.name}" }
                return
            } catch (e: IOException) {
                Logger.w { "Failed to open ${terminal.name}: ${e.message}" }
            }
        }

        throw IOException("Could not open any terminal emulator")
    }

    private fun openTerminalForRpmInstall(filePath: String) {
        Logger.d { "Opening terminal for RPM installation" }

        val availableTerminals = detectAvailableTerminals()

        if (availableTerminals.isEmpty()) {
            Logger.e { "No terminal emulator found on system" }

            tryShowNotification(
                "Installation Required",
                "Please install manually: sudo dnf install -y '$filePath'"
            )

            tryCopyToClipboard("sudo dnf install -y '$filePath'")

            throw IOException(
                "No terminal emulator found. Please install manually:\n" +
                        "sudo dnf install -y '$filePath'"
            )
        }

        val command = "echo 'Installing RPM package...'; sudo dnf install -y '$filePath' || sudo yum install -y '$filePath' || sudo rpm -i '$filePath'; echo ''; echo 'Installation complete. Press Enter to close...'; read"

        for (terminal in availableTerminals) {
            try {
                Logger.d { "Trying terminal: ${terminal.name}" }
                val processBuilder = when (terminal) {
                    Terminal.GNOME_TERMINAL -> ProcessBuilder(
                        "gnome-terminal", "--", "bash", "-c", command
                    )
                    Terminal.KONSOLE -> ProcessBuilder(
                        "konsole", "-e", "bash", "-c", command
                    )
                    Terminal.XTERM -> ProcessBuilder(
                        "xterm", "-e", "bash", "-c", command
                    )
                    Terminal.XFCE4_TERMINAL -> ProcessBuilder(
                        "xfce4-terminal", "-e", "bash -c \"$command\""
                    )
                    Terminal.ALACRITTY -> ProcessBuilder(
                        "alacritty", "-e", "bash", "-c", command
                    )
                    Terminal.KITTY -> ProcessBuilder(
                        "kitty", "bash", "-c", command
                    )
                    Terminal.TILIX -> ProcessBuilder(
                        "tilix", "-e", "bash -c \"$command\""
                    )
                    Terminal.MATE_TERMINAL -> ProcessBuilder(
                        "mate-terminal", "-e", "bash -c \"$command\""
                    )
                }

                processBuilder.start()
                Logger.d { "Terminal opened successfully: ${terminal.name}" }
                return
            } catch (e: IOException) {
                Logger.w { "Failed to open ${terminal.name}: ${e.message}" }
            }
        }

        throw IOException("Could not open any terminal emulator")
    }

    private enum class Terminal {
        GNOME_TERMINAL,
        KONSOLE,
        XTERM,
        XFCE4_TERMINAL,
        ALACRITTY,
        KITTY,
        TILIX,
        MATE_TERMINAL
    }

    private fun detectAvailableTerminals(): List<Terminal> {
        val terminals = mutableListOf<Terminal>()

        val terminalCommands = mapOf(
            Terminal.GNOME_TERMINAL to "gnome-terminal",
            Terminal.KONSOLE to "konsole",
            Terminal.XTERM to "xterm",
            Terminal.XFCE4_TERMINAL to "xfce4-terminal",
            Terminal.ALACRITTY to "alacritty",
            Terminal.KITTY to "kitty",
            Terminal.TILIX to "tilix",
            Terminal.MATE_TERMINAL to "mate-terminal"
        )

        for ((terminal, command) in terminalCommands) {
            try {
                val process = ProcessBuilder("which", command).start()
                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    terminals.add(terminal)
                    Logger.d { "Found terminal: $command" }
                }
            } catch (e: Exception) {
                // Terminal not available
            }
        }

        return terminals
    }

    private fun tryShowNotification(title: String, message: String) {
        try {
            ProcessBuilder(
                "notify-send",
                title,
                message,
                "-u", "critical",
                "-t", "10000"
            ).start()
        } catch (e: Exception) {
            Logger.w { "Could not show notification: ${e.message}" }
        }
    }

    private fun tryCopyToClipboard(text: String) {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(StringSelection(text), null)
            Logger.d { "Command copied to clipboard" }
        } catch (e: Exception) {
            Logger.w { "Could not copy to clipboard: ${e.message}" }
        }
    }


    private fun installAppImage(file: File) {
        Logger.d { "Installing AppImage: ${file.absolutePath}" }

        val desktopDir = getDesktopDirectory()
        Logger.d { "Desktop directory: ${desktopDir.absolutePath}" }
        Logger.d { "Desktop exists: ${desktopDir.exists()}, isDirectory: ${desktopDir.isDirectory}, canWrite: ${desktopDir.canWrite()}" }

        val destinationFile = File(desktopDir, file.name)

        val finalDestination = if (destinationFile.exists()) {
            Logger.d { "File already exists, generating unique name" }
            generateUniqueFileName(desktopDir, file.name)
        } else {
            destinationFile
        }

        Logger.d { "Final destination: ${finalDestination.absolutePath}" }

        try {
            Logger.d { "Copying file..." }
            file.copyTo(finalDestination, overwrite = false)
            Logger.d { "Copy successful, file size: ${finalDestination.length()} bytes" }

            val executableSet = finalDestination.setExecutable(true, false)
            Logger.d { "Set executable: $executableSet" }

            if (!finalDestination.exists()) {
                throw IllegalStateException("File was copied but doesn't exist at destination")
            }

            try {
                Logger.d { "Attempting to open desktop folder..." }
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(desktopDir)
                    Logger.d { "Desktop folder opened" }
                } else {
                    Logger.w { "Desktop not supported, trying xdg-open" }
                    ProcessBuilder("xdg-open", desktopDir.absolutePath).start()
                }
            } catch (e: Exception) {
                Logger.w { "Could not open desktop folder: ${e.message}" }
            }

            Logger.d { "AppImage installation completed successfully" }
        } catch (e: IOException) {
            Logger.e { "Failed to copy AppImage: ${e.message}" }
            e.printStackTrace()
            throw IllegalStateException(
                "Failed to copy AppImage to desktop: ${e.message}. " +
                        "Desktop path: ${desktopDir.absolutePath}. " +
                        "Please ensure you have write permissions to your Desktop folder.",
                e
            )
        } catch (e: SecurityException) {
            Logger.e { "Security exception: ${e.message}" }
            e.printStackTrace()
            throw IllegalStateException(
                "Security restrictions prevent copying AppImage to desktop.",
                e
            )
        } catch (e: Exception) {
            Logger.e { "Unexpected error: ${e.message}" }
            e.printStackTrace()
            throw IllegalStateException("Failed to install AppImage: ${e.message}", e)
        }
    }

    private fun getDesktopDirectory(): File {
        try {
            val process = ProcessBuilder("xdg-user-dir", "DESKTOP").start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()

            if (output.isNotEmpty() && output != "DESKTOP") {
                val xdgDesktop = File(output)
                if (xdgDesktop.exists() && xdgDesktop.isDirectory) {
                    return xdgDesktop
                }
            }
        } catch (e: Exception) {
        }

        val homeDir = System.getProperty("user.home")
        val desktopCandidates = listOf(
            File(homeDir, "Desktop"),
            File(homeDir, "desktop"),
            File(homeDir, ".local/share/Desktop"),
            File(homeDir)
        )

        return desktopCandidates.firstOrNull { it.exists() && it.isDirectory }
            ?: File(homeDir, "Desktop").also { it.mkdirs() }
    }

    private fun generateUniqueFileName(directory: File, originalName: String): File {
        val nameWithoutExtension = originalName.substringBeforeLast(".")
        val extension = originalName.substringAfterLast(".", "")

        var counter = 1
        var candidateFile: File

        do {
            val newName = if (extension.isNotEmpty()) {
                "${nameWithoutExtension}_$counter.$extension"
            } else {
                "${nameWithoutExtension}_$counter"
            }
            candidateFile = File(directory, newName)
            counter++
        } while (candidateFile.exists() && counter < 1000)

        if (candidateFile.exists()) {
            throw IllegalStateException("Could not generate unique filename on desktop")
        }

        return candidateFile
    }

}