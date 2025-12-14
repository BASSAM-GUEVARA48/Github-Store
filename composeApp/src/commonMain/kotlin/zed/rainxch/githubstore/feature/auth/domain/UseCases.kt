package zed.rainxch.githubstore.feature.auth.domain

import kotlinx.coroutines.flow.Flow
import zed.rainxch.githubstore.core.domain.model.DeviceStart
import zed.rainxch.githubstore.core.domain.model.DeviceTokenSuccess
import zed.rainxch.githubstore.feature.auth.domain.repository.AuthRepository

class StartDeviceFlowUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(): DeviceStart = repo.startDeviceFlow()
}

class AwaitDeviceTokenUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(start: DeviceStart): DeviceTokenSuccess =
        repo.awaitDeviceToken(start)
}

class ObserveAccessTokenUseCase(private val repo: AuthRepository) {
    operator fun invoke(): Flow<String?> = repo.accessTokenFlow
}

class IsAuthenticatedUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(): Boolean = repo.isAuthenticated()
    fun observe(): Flow<Boolean> = repo.isAuthenticatedFlow
}