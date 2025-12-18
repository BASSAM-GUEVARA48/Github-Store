package zed.rainxch.githubstore.feature.apps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import zed.rainxch.githubstore.core.data.PackageMonitor
import zed.rainxch.githubstore.core.data.local.db.entities.InstalledApp
import zed.rainxch.githubstore.core.domain.repository.InstalledAppsRepository
import zed.rainxch.githubstore.feature.apps.domain.repository.AppsRepository
import zed.rainxch.githubstore.feature.apps.presentation.model.AppItem
import zed.rainxch.githubstore.feature.apps.presentation.model.UpdateAllProgress
import zed.rainxch.githubstore.feature.apps.presentation.model.UpdateState
import zed.rainxch.githubstore.feature.details.data.Downloader
import zed.rainxch.githubstore.feature.details.data.Installer

class AppsViewModel(
    private val appsRepository: AppsRepository,
    private val installer: Installer,
    private val downloader: Downloader,
    private val installedAppsRepository: InstalledAppsRepository,
    private val packageMonitor: PackageMonitor
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val activeUpdates = mutableMapOf<String, Job>()
    private var updateAllJob: Job? = null

    private val _state = MutableStateFlow(AppsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadApps()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AppsState()
        )

    private val _events = Channel<AppsEvent>()
    val events = _events.receiveAsFlow()

    private fun loadApps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                appsRepository.getApps().collect { apps ->
                    val appItems = apps.map { app ->
                        val existing = _state.value.apps.find {
                            it.installedApp.packageName == app.packageName
                        }
                        AppItem(
                            installedApp = app,
                            updateState = existing?.updateState ?: UpdateState.Idle,
                            downloadProgress = existing?.downloadProgress,
                            error = existing?.error
                        )
                    }

                    _state.update {
                        it.copy(
                            apps = appItems,
                            isLoading = false,
                            updateAllButtonEnabled = appItems.any { item ->
                                item.installedApp.isUpdateAvailable
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.e { "Failed to load apps: ${e.message}" }
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load apps"
                    )
                }
            }
        }
    }

    fun onAction(action: AppsAction) {
        when (action) {
            AppsAction.OnNavigateBackClick -> {
                // Handled in UI
            }

            is AppsAction.OnSearchChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }

            is AppsAction.OnOpenApp -> {
                openApp(action.app)
            }

            is AppsAction.OnUpdateApp -> {
                updateSingleApp(action.app)
            }

            is AppsAction.OnCancelUpdate -> {
                cancelUpdate(action.packageName)
            }

            AppsAction.OnUpdateAll -> {
                updateAllApps()
            }

            AppsAction.OnCancelUpdateAll -> {
                cancelAllUpdates()
            }

            AppsAction.OnCheckAllForUpdates -> {
                checkAllForUpdates()
            }

            is AppsAction.OnNavigateToRepo -> {
                viewModelScope.launch {
                    _events.send(AppsEvent.NavigateToRepo(action.repoId))
                }
            }
        }
    }

    private fun openApp(app: InstalledApp) {
        viewModelScope.launch {
            try {
                appsRepository.openApp(
                    installedApp = app,
                    onCantLaunchApp = {
                        viewModelScope.launch {
                            _events.send(
                                AppsEvent.ShowError("Cannot launch ${app.appName}")
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Logger.e { "Failed to open app: ${e.message}" }
                _events.send(AppsEvent.ShowError("Failed to open ${app.appName}"))
            }
        }
    }

    private fun updateSingleApp(app: InstalledApp) {
        if (activeUpdates.containsKey(app.packageName)) {
            Logger.w { "Update already in progress for ${app.packageName}" }
            return
        }

        val job = viewModelScope.launch {
            try {
                updateAppState(app.packageName, UpdateState.CheckingUpdate)

                // Get latest release info
                val latestAssetUrl = app.latestAssetUrl
                val latestAssetName = app.latestAssetName
                val latestVersion = app.latestVersion
                val latestAssetSize = app.latestAssetSize

                if (latestAssetUrl == null || latestAssetName == null || latestVersion == null) {
                    throw IllegalStateException("Update information not available")
                }

                // Ensure permissions
                val ext = latestAssetName.substringAfterLast('.', "").lowercase()
                installer.ensurePermissionsOrThrow(ext)

                // Download
                updateAppState(app.packageName, UpdateState.Downloading)

                downloader.download(latestAssetUrl, latestAssetName).collect { progress ->
                    updateAppProgress(app.packageName, progress.percent)
                }

                val filePath = downloader.getDownloadedFilePath(latestAssetName)
                    ?: throw IllegalStateException("Downloaded file not found")

                // Update database before installation
                updateAppInDatabase(
                    app = app,
                    newVersion = latestVersion,
                    assetName = latestAssetName,
                    assetUrl = latestAssetUrl,
                    assetSize = latestAssetSize ?: 0L,
                    filePath = filePath
                )

                // Install
                updateAppState(app.packageName, UpdateState.Installing)
                installer.install(filePath, ext)

                // Wait a bit for installation to complete
                delay(2000)

                // Verify installation
                val isInstalled = packageMonitor.isPackageInstalled(app.packageName)
                if (isInstalled) {
                    // Clear pending status
                    installedAppsRepository.updatePendingStatus(app.packageName, false)

                    // Update version from system
                    val systemInfo = packageMonitor.getInstalledPackageInfo(app.packageName)
                    if (systemInfo != null) {
                        installedAppsRepository.updateAppVersion(
                            packageName = app.packageName,
                            newVersion = systemInfo.versionName,
                            newAssetName = latestAssetName,
                            newAssetUrl = latestAssetUrl
                        )
                    }

                    updateAppState(app.packageName, UpdateState.Success)
                    delay(2000)
                    updateAppState(app.packageName, UpdateState.Idle)

                    Logger.d { "Successfully updated ${app.appName}" }
                } else {
                    throw IllegalStateException("Installation verification failed")
                }

            } catch (e: CancellationException) {
                Logger.d { "Update cancelled for ${app.packageName}" }
                cleanupUpdate(app.packageName, app.latestAssetName)
                updateAppState(app.packageName, UpdateState.Idle)
                throw e
            } catch (e: Exception) {
                Logger.e { "Update failed for ${app.packageName}: ${e.message}" }
                cleanupUpdate(app.packageName, app.latestAssetName)
                updateAppState(
                    app.packageName,
                    UpdateState.Error(e.message ?: "Update failed")
                )
                _events.send(AppsEvent.ShowError("Failed to update ${app.appName}"))
            } finally {
                activeUpdates.remove(app.packageName)
            }
        }

        activeUpdates[app.packageName] = job
    }

    private fun updateAllApps() {
        if (_state.value.isUpdatingAll) {
            Logger.w { "Update all already in progress" }
            return
        }

        updateAllJob = viewModelScope.launch {
            try {
                _state.update { it.copy(isUpdatingAll = true) }

                val appsToUpdate = _state.value.apps.filter {
                    it.installedApp.isUpdateAvailable &&
                            it.updateState !is UpdateState.Success
                }

                if (appsToUpdate.isEmpty()) {
                    _events.send(AppsEvent.ShowError("No updates available"))
                    return@launch
                }

                Logger.d { "Starting update all for ${appsToUpdate.size} apps" }

                appsToUpdate.forEachIndexed { index, appItem ->
                    if (!isActive) {
                        Logger.d { "Update all cancelled" }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            updateAllProgress = UpdateAllProgress(
                                current = index + 1,
                                total = appsToUpdate.size,
                                currentAppName = appItem.installedApp.appName
                            )
                        )
                    }

                    Logger.d { "Updating ${index + 1}/${appsToUpdate.size}: ${appItem.installedApp.appName}" }

                    // Update this app and wait for completion
                    val job = viewModelScope.launch {
                        updateSingleApp(appItem.installedApp)
                    }

                    // Wait for this app to complete before starting next
                    job.join()

                    // Small delay between apps
                    delay(1000)
                }

                Logger.d { "Update all completed successfully" }
                _events.send(AppsEvent.ShowSuccess("All apps updated successfully"))

            } catch (e: CancellationException) {
                Logger.d { "Update all cancelled" }
            } catch (e: Exception) {
                Logger.e { "Update all failed: ${e.message}" }
                _events.send(AppsEvent.ShowError("Update all failed: ${e.message}"))
            } finally {
                _state.update {
                    it.copy(
                        isUpdatingAll = false,
                        updateAllProgress = null
                    )
                }
                updateAllJob = null
            }
        }
    }

    private fun cancelUpdate(packageName: String) {
        activeUpdates[packageName]?.cancel()
        activeUpdates.remove(packageName)

        val app = _state.value.apps.find { it.installedApp.packageName == packageName }
        app?.installedApp?.latestAssetName?.let { assetName ->
            viewModelScope.launch {
                cleanupUpdate(packageName, assetName)
            }
        }

        updateAppState(packageName, UpdateState.Idle)
    }

    private fun cancelAllUpdates() {
        updateAllJob?.cancel()
        updateAllJob = null

        activeUpdates.values.forEach { it.cancel() }
        activeUpdates.clear()

        // Cleanup all downloaded files
        viewModelScope.launch {
            _state.value.apps.forEach { appItem ->
                if (appItem.updateState != UpdateState.Idle &&
                    appItem.updateState != UpdateState.Success) {

                    appItem.installedApp.latestAssetName?.let { assetName ->
                        cleanupUpdate(appItem.installedApp.packageName, assetName)
                    }
                    updateAppState(appItem.installedApp.packageName, UpdateState.Idle)
                }
            }
        }

        _state.update {
            it.copy(
                isUpdatingAll = false,
                updateAllProgress = null
            )
        }
    }

    private fun checkAllForUpdates() {
        viewModelScope.launch {
            try {
                _state.value.apps.forEach { appItem ->
                    try {
                        installedAppsRepository.checkForUpdates(appItem.installedApp.packageName)
                    } catch (e: Exception) {
                        Logger.w { "Failed to check updates for ${appItem.installedApp.packageName}" }
                    }
                }
            } catch (e: Exception) {
                Logger.e { "Check all for updates failed: ${e.message}" }
            }
        }
    }

    private fun updateAppState(packageName: String, state: UpdateState) {
        _state.update { currentState ->
            currentState.copy(
                apps = currentState.apps.map { appItem ->
                    if (appItem.installedApp.packageName == packageName) {
                        appItem.copy(
                            updateState = state,
                            downloadProgress = if (state is UpdateState.Downloading)
                                appItem.downloadProgress else null,
                            error = if (state is UpdateState.Error) state.message else null
                        )
                    } else {
                        appItem
                    }
                }
            )
        }
    }

    private fun updateAppProgress(packageName: String, progress: Int?) {
        _state.update { currentState ->
            currentState.copy(
                apps = currentState.apps.map { appItem ->
                    if (appItem.installedApp.packageName == packageName) {
                        appItem.copy(downloadProgress = progress)
                    } else {
                        appItem
                    }
                }
            )
        }
    }

    private suspend fun updateAppInDatabase(
        app: InstalledApp,
        newVersion: String,
        assetName: String,
        assetUrl: String,
        assetSize: Long,
        filePath: String
    ) {
        try {
            installedAppsRepository.updateAppVersion(
                packageName = app.packageName,
                newVersion = newVersion,
                newAssetName = assetName,
                newAssetUrl = assetUrl
            )

            installedAppsRepository.updatePendingStatus(app.packageName, true)

            Logger.d { "Updated database for ${app.packageName} to version $newVersion" }
        } catch (e: Exception) {
            Logger.e { "Failed to update database: ${e.message}" }
        }
    }

    private suspend fun cleanupUpdate(packageName: String, assetName: String?) {
        try {
            if (assetName != null) {
                val deleted = downloader.cancelDownload(assetName)
                Logger.d { "Cleanup for $packageName - file deleted: $deleted" }
            }
        } catch (e: Exception) {
            Logger.w { "Cleanup failed for $packageName: ${e.message}" }
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Cancel all active updates
        updateAllJob?.cancel()
        activeUpdates.values.forEach { it.cancel() }

        // Cleanup downloaded files
        viewModelScope.launch {
            _state.value.apps.forEach { appItem ->
                if (appItem.updateState != UpdateState.Idle &&
                    appItem.updateState != UpdateState.Success) {
                    appItem.installedApp.latestAssetName?.let { assetName ->
                        downloader.cancelDownload(assetName)
                    }
                }
            }
        }
    }
}