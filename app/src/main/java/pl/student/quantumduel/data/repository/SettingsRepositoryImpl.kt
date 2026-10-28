package pl.student.quantumduel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ustawienia w Preferences DataStore. ETAP WSTĘPNY.
 *
 * Plan: 8 kluczy (nazwy graczy, tryb domyślny, źródło losowości, 2 klucze API, dźwięk, wibracja).
 * Gdy klucza API nie ma w DataStore, wartością domyślną ma być BuildConfig.ANU_API_KEY / OUTSHIFT_API_KEY
 * (z local.properties). Błąd odczytu pliku (IOException) → ustawienia domyślne.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings>
        get() {
            // TODO: dataStore.data → AppSettings (z wartościami domyślnymi i obsługą IOException)
            TODO("SettingsRepositoryImpl.settings")
        }

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        // TODO: dataStore.edit { … } – zapisać wszystkie pola po transform()
        TODO("SettingsRepositoryImpl.update")
    }
}
