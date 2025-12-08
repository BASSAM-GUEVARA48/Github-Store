package zed.rainxch.githubstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zed.rainxch.githubstore.core.data.TokenDataSource
import zed.rainxch.githubstore.core.domain.repository.ThemesRepository

class MainViewModel(
    private val tokenDataSource: TokenDataSource,
    private val themesRepository: ThemesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val initialToken = tokenDataSource.reloadFromStore()
            _state.update {
                it.copy(
                    isCheckingAuth = false,
                    isLoggedIn = initialToken != null
                )
            }
            Logger.d("MainViewmodel") { "Initial token loaded: ${initialToken != null}" }
        }

        viewModelScope.launch {
            tokenDataSource
                .tokenFlow
                .drop(1) // Skip initial emission (already handled above)
                .distinctUntilChanged()
                .collect { authInfo ->
                    _state.update { it.copy(isLoggedIn = authInfo != null) }
                    Logger.d("MainViewmodel") { "Token updated: ${authInfo != null}" }
                }
        }

        viewModelScope.launch {
            themesRepository
                .getThemeColor()
                .collect { theme ->
                    _state.update {
                        it.copy(currentColorTheme = theme)
                    }
                }
        }
    }
}