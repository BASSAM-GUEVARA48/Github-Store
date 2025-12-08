package zed.rainxch.githubstore.feature.auth.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey.*
import zed.rainxch.githubstore.BuildConfig
import kotlinx.serialization.json.Json
import zed.rainxch.githubstore.core.presentation.utils.AppContextHolder
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import zed.rainxch.githubstore.core.domain.model.DeviceTokenSuccess

actual fun getGithubClientId(): String = BuildConfig.GITHUB_CLIENT_ID

actual fun copyToClipboard(label: String, text: String): Boolean {
    return try {
        val ctx: Context = AppContextHolder.appContext
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
        true
    } catch (_: Throwable) {
        false
    }
}

class AndroidTokenStore(
    private val dataStore: DataStore<Preferences>,
) : TokenStore {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun save(token: DeviceTokenSuccess) {
        val jsonString = json.encodeToString(DeviceTokenSuccess.serializer(), token)
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = jsonString
        }
    }

    override suspend fun load(): DeviceTokenSuccess? {
        return runCatching {
            val preferences = dataStore.data.first()
            val raw = preferences[TOKEN_KEY] ?: return null
            json.decodeFromString(DeviceTokenSuccess.serializer(), raw)
        }.getOrNull()
    }

    override suspend fun clear() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
