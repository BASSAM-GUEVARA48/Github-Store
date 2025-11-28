package zed.rainxch.githubstore.core.domain

import zed.rainxch.githubstore.core.domain.model.PlatformType

class AndroidPlatform : Platform {
    override val type: PlatformType
        get() = PlatformType.ANDROID

}

actual fun getPlatform(): Platform {
    return AndroidPlatform()
}