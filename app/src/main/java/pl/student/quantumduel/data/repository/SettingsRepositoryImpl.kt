package pl.student.quantumduel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pl.student.quantumduel.BuildConfig
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.SettingsRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Ustawienia w DataStore. Klucze API domyślnie pochodzą z local.properties (BuildConfig). */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it.toSettings() }
        .distinctUntilChanged()

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.edit { prefs ->
            val new = transform(prefs.toSettings())
            prefs[Keys.PLAYER_A] = new.playerAName
            prefs[Keys.PLAYER_B] = new.playerBName
            prefs[Keys.DEFAULT_MODE] = new.defaultMode.name
            prefs[Keys.SOURCE] = new.randomnessSource.name
            prefs[Keys.ANU_KEY] = new.anuApiKey
            prefs[Keys.OUTSHIFT_KEY] = new.outshiftApiKey
            prefs[Keys.SOUND] = new.soundEnabled
            prefs[Keys.VIBRATION] = new.vibrationEnabled
        }
    }

    private fun Preferences.toSettings(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            playerAName = this[Keys.PLAYER_A] ?: defaults.playerAName,
            playerBName = this[Keys.PLAYER_B] ?: defaults.playerBName,
            defaultMode = enumOrDefault(this[Keys.DEFAULT_MODE], defaults.defaultMode),
            randomnessSource = enumOrDefault(this[Keys.SOURCE], defaults.randomnessSource),
            anuApiKey = this[Keys.ANU_KEY] ?: BuildConfig.ANU_API_KEY,
            outshiftApiKey = this[Keys.OUTSHIFT_KEY] ?: BuildConfig.OUTSHIFT_API_KEY,
            soundEnabled = this[Keys.SOUND] ?: defaults.soundEnabled,
            vibrationEnabled = this[Keys.VIBRATION] ?: defaults.vibrationEnabled,
        )
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T =
        name?.let { value -> enumValues<T>().firstOrNull { it.name == value } } ?: default

    private object Keys {
        val PLAYER_A = stringPreferencesKey("player_a_name")
        val PLAYER_B = stringPreferencesKey("player_b_name")
        val DEFAULT_MODE = stringPreferencesKey("default_mode")
        val SOURCE = stringPreferencesKey("randomness_source")
        val ANU_KEY = stringPreferencesKey("anu_api_key")
        val OUTSHIFT_KEY = stringPreferencesKey("outshift_api_key")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
    }
}
