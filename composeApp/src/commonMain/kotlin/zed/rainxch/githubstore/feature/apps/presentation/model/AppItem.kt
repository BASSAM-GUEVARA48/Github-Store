package zed.rainxch.githubstore.feature.apps.presentation.model

import zed.rainxch.githubstore.core.data.local.db.entities.InstalledApp

data class AppItem(
    val installedApp: InstalledApp,
    val updateState: UpdateState = UpdateState.Idle,
    val downloadProgress: Int? = null,
    val error: String? = null
)