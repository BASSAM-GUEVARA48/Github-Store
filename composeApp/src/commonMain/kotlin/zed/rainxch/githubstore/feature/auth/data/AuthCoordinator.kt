package zed.rainxch.githubstore.feature.auth.data

import zed.rainxch.githubstore.core.domain.model.DeviceTokenSuccess

interface TokenStore {
    suspend fun save(token: DeviceTokenSuccess)
    suspend fun load(): DeviceTokenSuccess?
    suspend fun clear()
}

expect fun getGithubClientId(): String