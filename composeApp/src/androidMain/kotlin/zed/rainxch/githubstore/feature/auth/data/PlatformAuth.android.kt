package zed.rainxch.githubstore.feature.auth.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import zed.rainxch.githubstore.BuildConfig
import zed.rainxch.githubstore.core.domain.model.DeviceTokenSuccess

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

actual fun getGithubClientId(): String = BuildConfig.GITHUB_CLIENT_ID