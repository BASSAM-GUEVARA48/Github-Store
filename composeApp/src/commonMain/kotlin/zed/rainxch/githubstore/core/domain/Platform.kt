package zed.rainxch.githubstore.core.domain

import zed.rainxch.githubstore.core.domain.model.PlatformType

interface Platform {
    val type: PlatformType
}

expect fun getPlatform(): Platform