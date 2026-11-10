package pl.student.quantumduel.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.entropy.EntropyStatus
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.RandomnessSource
import pl.student.quantumduel.domain.settings.SettingsRepository
import javax.inject.Inject

/**
 * Formularz ustawień. Stan formularza trzymamy w ViewModelu i aktualizujemy synchronicznie,
 * a zapis do DataStore idzie w tle – dzięki temu kursor w polach tekstowych nie „skacze”.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val entropyRepository: EntropyRepository,
) : ViewModel() {

    private val _form = MutableStateFlow<AppSettings?>(null)
    val form: StateFlow<AppSettings?> = _form.asStateFlow()

    val entropyStatus: StateFlow<EntropyStatus> = entropyRepository.status

    init {
        viewModelScope.launch { _form.value = settingsRepository.settings.first() }
    }

    fun onPlayerANameChange(name: String) = edit { it.copy(playerAName = name.take(AppSettings.MAX_NAME_LENGTH)) }

    fun onPlayerBNameChange(name: String) = edit { it.copy(playerBName = name.take(AppSettings.MAX_NAME_LENGTH)) }

    fun onDefaultModeChange(mode: GameMode) = edit { it.copy(defaultMode = mode) }

    fun onSourceChange(source: RandomnessSource) = edit { it.copy(randomnessSource = source) }

    fun onAnuKeyChange(key: String) = edit { it.copy(anuApiKey = key.trim()) }

    fun onOutshiftKeyChange(key: String) = edit { it.copy(outshiftApiKey = key.trim()) }

    fun onSoundChange(enabled: Boolean) = edit { it.copy(soundEnabled = enabled) }

    fun onVibrationChange(enabled: Boolean) = edit { it.copy(vibrationEnabled = enabled) }

    /** Wymusza pobranie partii bajtów – pozwala od razu sprawdzić, czy klucz API działa. */
    fun onTestConnection() = entropyRepository.prefetch(force = true)

    private fun edit(transform: (AppSettings) -> AppSettings) {
        val current = _form.value ?: return
        val updated = transform(current)
        if (updated == current) return
        _form.value = updated
        viewModelScope.launch { settingsRepository.update { updated } }
    }
}
