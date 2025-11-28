package zed.rainxch.githubstore.core.domain

import zed.rainxch.githubstore.core.domain.model.PlatformType

class DesktopPlatform : Platform {
    override val type = when {
        System.getProperty("os.name").lowercase().contains("win") -> PlatformType.WINDOWS
        System.getProperty("os.name").lowercase().contains("mac") -> PlatformType.MACOS
        else -> PlatformType.LINUX
    }
}

actual fun getPlatform(): Platform {
    return DesktopPlatform()
}