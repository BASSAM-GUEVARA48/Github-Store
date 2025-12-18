package zed.rainxch.githubstore.feature.apps.domain.repository

import kotlinx.coroutines.flow.Flow
import zed.rainxch.githubstore.core.data.local.db.entities.InstalledApp

interface AppsRepository {
    suspend fun getApps(): Flow<List<InstalledApp>>
    suspend fun openApp(
        installedApp: InstalledApp,
        onCantLaunchApp : () -> Unit = { }
    )
}