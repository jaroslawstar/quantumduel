package pl.student.quantumduel.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.entropy.EntropyStatus
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.RandomnessSource
import pl.student.quantumduel.domain.settings.SettingsRepository
import javax.inject.Inject

/**
 * Formularz ustawień. ETAP WSTĘPNY: formularz pokazuje wartości domyślne, zapis nie jest jeszcze podłączony.
 *
 * Plan: stan formularza w ViewModelu aktualizowany synchronicznie, zapis do DataStore w tle
 * (dzięki temu kursor w polach tekstowych nie „skacze”).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val entropyRepository: EntropyRepository,
) : ViewModel() {

    // TODO: wczytać formularz z settingsRepository.settings.first() w init
    private val _form = MutableStateFlow<AppSettings?>(AppSettings())
    val form: StateFlow<AppSettings?> = _form.asStateFlow()

    val entropyStatus: StateFlow<EntropyStatus> = entropyRepository.status

    // TODO: każda zmiana → nowy stan formularza + settingsRepository.update { … } w viewModelScope
    //       (nazwy graczy przycięte do AppSettings.MAX_NAME_LENGTH, klucze API bez białych znaków)

    fun onPlayerANameChange(name: String) {}

    fun onPlayerBNameChange(name: String) {}

    fun onDefaultModeChange(mode: GameMode) {}

    fun onSourceChange(source: RandomnessSource) {}

    fun onAnuKeyChange(key: String) {}

    fun onOutshiftKeyChange(key: String) {}

    fun onSoundChange(enabled: Boolean) {}

    fun onVibrationChange(enabled: Boolean) {}

    /** Ma wymusić pobranie partii bajtów – pozwala od razu sprawdzić, czy klucz API działa. */
    fun onTestConnection() {
        // TODO: entropyRepository.prefetch(force = true)
    }
}
