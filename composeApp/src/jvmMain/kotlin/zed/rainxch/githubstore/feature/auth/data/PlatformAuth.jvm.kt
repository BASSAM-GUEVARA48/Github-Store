package zed.rainxch.githubstore.feature.auth.data

import kotlinx.serialization.json.Json
import zed.rainxch.githubstore.BuildConfig
import zed.rainxch.githubstore.core.domain.model.DeviceTokenSuccess
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.prefs.Preferences

class DesktopTokenStore : TokenStore {
    private val prefs: Preferences = Preferences.userRoot().node("zed.rainxch.githubstore")
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun save(token: DeviceTokenSuccess) {
        prefs.put("token", json.encodeToString(DeviceTokenSuccess.serializer(), token))
    }

    override suspend fun load(): DeviceTokenSuccess? {
        val raw = prefs.get("token", null) ?: return null
        return runCatching { json.decodeFromString(DeviceTokenSuccess.serializer(), raw) }.getOrNull()
    }

    override suspend fun clear() { prefs.remove("token") }
}

actual fun getGithubClientId(): String {
    val fromSys = System.getProperty("GITHUB_CLIENT_ID")?.trim().orEmpty()
    if (fromSys.isNotEmpty()) return fromSys

    val fromEnv = System.getenv("GITHUB_CLIENT_ID")?.trim().orEmpty()
    if (fromEnv.isNotEmpty()) return fromEnv

    return BuildConfig.GITHUB_CLIENT_ID
}