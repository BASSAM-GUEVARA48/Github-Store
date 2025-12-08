package zed.rainxch.githubstore.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zed.rainxch.githubstore.core.domain.repository.ThemesRepository
import zed.rainxch.githubstore.core.presentation.utils.BrowserHelper

class SettingsViewModel(
    private val browserHelper: BrowserHelper,
    private val themesRepository: ThemesRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadCurrentTheme()

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState()
        )

    private fun loadCurrentTheme() {
        viewModelScope.launch {
            themesRepository.getThemeColor().collect { theme ->
                _state.update {
                    it.copy(
                        selectedThemeColor = theme
                    )
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnHelpClick -> {
                browserHelper.openUrl(
                    url = "https://github.com/rainxchzed/Github-Store/issues"
                )
            }

            is SettingsAction.OnThemeColorSelected -> {
                viewModelScope.launch {
                    themesRepository.setThemeColor(action.themeColor)
                }
            }

            SettingsAction.OnNavigateBackClick -> {
                /* Handed in composable */
            }
        }
    }

}