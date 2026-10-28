package pl.student.quantumduel.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.presentation.SettingsViewModel
import pl.student.quantumduel.ui.components.PlannedFeatures

@Composable
fun SettingsRoute(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    SettingsScreen(form = form, onBack = onBack)
}

/**
 * Ustawienia. ETAP WSTĘPNY: ekran zastępczy pokazujący wartości domyślne i listę planowanych sekcji.
 *
 * TODO: formularz – pola nazw graczy, wybór trybu i źródła losowości, pola kluczy API (maskowane),
 *       stan puli entropii z przyciskiem „Pobierz partię teraz”, przełączniki dźwięku i wibracji
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(form: AppSettings?, onBack: () -> Unit) {
    val settings = form ?: AppSettings()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        PlannedFeatures(
            intro = stringResource(
                R.string.placeholder_settings,
                settings.playerAName,
                settings.playerBName,
                settings.randomnessSource.name,
            ),
            items = stringArrayResource(R.array.plan_settings).toList(),
            modifier = Modifier.padding(padding),
        )
    }
}
