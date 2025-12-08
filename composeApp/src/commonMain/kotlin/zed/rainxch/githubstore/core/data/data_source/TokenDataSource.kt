package zed.rainxch.githubstore.core.data.data_source

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import zed.rainxch.githubstore.core.domain.model.DeviceTokenSuccess
import zed.rainxch.githubstore.feature.auth.data.TokenStore
import kotlin.concurrent.atomics.ExperimentalAtomicApi

interface TokenDataSource {
    val tokenFlow: StateFlow<DeviceTokenSuccess?>
    suspend fun save(token: DeviceTokenSuccess)
    suspend fun reloadFromStore(): DeviceTokenSuccess?
    suspend fun clear()

    fun current(): DeviceTokenSuccess?
}

@OptIn(ExperimentalAtomicApi::class)
class DefaultTokenDataSource(
    private val tokenStore: TokenStore,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : TokenDataSource {
    private val _flow = MutableStateFlow<DeviceTokenSuccess?>(null)
    override val tokenFlow: StateFlow<DeviceTokenSuccess?> = _flow

    private val isInitialized = CompletableDeferred<Unit>()

    init {
        scope.launch {
            try {
                val token = tokenStore.load()
                _flow.value = token
            } finally {
                isInitialized.complete(Unit)
            }
        }
    }

    override suspend fun save(token: DeviceTokenSuccess) {
        tokenStore.save(token)
        _flow.value = token
    }

    override suspend fun reloadFromStore(): DeviceTokenSuccess? {
        isInitialized.await()
        return _flow.value
    }

    override suspend fun clear() {
        tokenStore.clear()
        _flow.value = null
    }

    override fun current(): DeviceTokenSuccess? = _flow.value
}
