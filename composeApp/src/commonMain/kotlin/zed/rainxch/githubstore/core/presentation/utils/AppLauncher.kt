package zed.rainxch.githubstore.core.presentation.utils

import zed.rainxch.githubstore.core.data.local.db.entities.InstalledApp

interface AppLauncher {
    suspend fun launchApp(installedApp: InstalledApp): Result<Unit>
    suspend fun canLaunchApp(installedApp: InstalledApp): Boolean
}