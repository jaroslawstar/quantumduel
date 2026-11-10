package pl.student.quantumduel.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.entropy.EntropyStatus
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.RandomnessSource
import pl.student.quantumduel.presentation.SettingsViewModel
import pl.student.quantumduel.ui.components.ModeSelector
import pl.student.quantumduel.ui.components.labelRes

private const val ANU_SIGNUP_URL = "https://quantumnumbers.anu.edu.au"
private const val OUTSHIFT_SIGNUP_URL = "https://outshift.cisco.com/quantum-random-number-generator"

@Composable
fun SettingsRoute(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val entropy by viewModel.entropyStatus.collectAsStateWithLifecycle()
    SettingsScreen(
        form = form,
        entropy = entropy,
        onBack = onBack,
        onPlayerAChange = viewModel::onPlayerANameChange,
        onPlayerBChange = viewModel::onPlayerBNameChange,
        onDefaultModeChange = viewModel::onDefaultModeChange,
        onSourceChange = viewModel::onSourceChange,
        onAnuKeyChange = viewModel::onAnuKeyChange,
        onOutshiftKeyChange = viewModel::onOutshiftKeyChange,
        onSoundChange = viewModel::onSoundChange,
        onVibrationChange = viewModel::onVibrationChange,
        onTestConnection = viewModel::onTestConnection,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    form: AppSettings?,
    entropy: EntropyStatus,
    onBack: () -> Unit,
    onPlayerAChange: (String) -> Unit,
    onPlayerBChange: (String) -> Unit,
    onDefaultModeChange: (GameMode) -> Unit,
    onSourceChange: (RandomnessSource) -> Unit,
    onAnuKeyChange: (String) -> Unit,
    onOutshiftKeyChange: (String) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onTestConnection: () -> Unit,
) {
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
        if (form == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SettingsForm(
                form = form,
                entropy = entropy,
                modifier = Modifier.padding(padding),
                onPlayerAChange = onPlayerAChange,
                onPlayerBChange = onPlayerBChange,
                onDefaultModeChange = onDefaultModeChange,
                onSourceChange = onSourceChange,
                onAnuKeyChange = onAnuKeyChange,
                onOutshiftKeyChange = onOutshiftKeyChange,
                onSoundChange = onSoundChange,
                onVibrationChange = onVibrationChange,
                onTestConnection = onTestConnection,
            )
        }
    }
}

@Composable
private fun SettingsForm(
    form: AppSettings,
    entropy: EntropyStatus,
    modifier: Modifier,
    onPlayerAChange: (String) -> Unit,
    onPlayerBChange: (String) -> Unit,
    onDefaultModeChange: (GameMode) -> Unit,
    onSourceChange: (RandomnessSource) -> Unit,
    onAnuKeyChange: (String) -> Unit,
    onOutshiftKeyChange: (String) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onTestConnection: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Section(title = stringResource(R.string.settings_players)) {
            OutlinedTextField(
                value = form.playerAName,
                onValueChange = onPlayerAChange,
                label = { Text(stringResource(R.string.settings_player_a)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.playerBName,
                onValueChange = onPlayerBChange,
                label = { Text(stringResource(R.string.settings_player_b)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Section(title = stringResource(R.string.settings_default_mode)) {
            ModeSelector(selected = form.defaultMode, onSelect = onDefaultModeChange, modifier = Modifier.fillMaxWidth())
        }

        Section(title = stringResource(R.string.settings_source)) {
            Column(Modifier.selectableGroup()) {
                SourceOption(
                    title = stringResource(R.string.source_anu),
                    description = stringResource(R.string.source_anu_desc),
                    selected = form.randomnessSource == RandomnessSource.ANU,
                    onClick = { onSourceChange(RandomnessSource.ANU) },
                )
                SourceOption(
                    title = stringResource(R.string.source_outshift),
                    description = stringResource(R.string.source_outshift_desc),
                    selected = form.randomnessSource == RandomnessSource.OUTSHIFT,
                    onClick = { onSourceChange(RandomnessSource.OUTSHIFT) },
                )
                SourceOption(
                    title = stringResource(R.string.source_local),
                    description = stringResource(R.string.source_local_desc),
                    selected = form.randomnessSource == RandomnessSource.LOCAL,
                    onClick = { onSourceChange(RandomnessSource.LOCAL) },
                )
            }
        }

        Section(title = stringResource(R.string.settings_keys)) {
            ApiKeyField(
                label = stringResource(R.string.key_anu),
                value = form.anuApiKey,
                onValueChange = onAnuKeyChange,
                signupUrl = ANU_SIGNUP_URL,
            )
            ApiKeyField(
                label = stringResource(R.string.key_outshift),
                value = form.outshiftApiKey,
                onValueChange = onOutshiftKeyChange,
                signupUrl = OUTSHIFT_SIGNUP_URL,
            )
            Text(
                text = stringResource(R.string.keys_quota_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Section(title = stringResource(R.string.settings_pool)) {
            PoolStatus(entropy)
            OutlinedButton(
                onClick = onTestConnection,
                enabled = !entropy.isFetching && form.randomnessSource != RandomnessSource.LOCAL,
            ) {
                Text(stringResource(R.string.pool_fetch_now))
            }
        }

        Section(title = stringResource(R.string.settings_effects)) {
            SwitchRow(stringResource(R.string.settings_sound), form.soundEnabled, onSoundChange)
            SwitchRow(stringResource(R.string.settings_vibration), form.vibrationEnabled, onVibrationChange)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun SourceOption(title: String, description: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(Modifier.padding(start = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ApiKeyField(label: String, value: String, onValueChange: (String) -> Unit, signupUrl: String) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { visible = !visible }) {
                    Text(stringResource(if (visible) R.string.key_hide else R.string.key_show))
                }
            },
            supportingText = {
                Text(stringResource(if (value.isBlank()) R.string.key_missing else R.string.key_saved))
            },
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(onClick = { runCatching { uriHandler.openUri(signupUrl) } }) {
            Text(stringResource(R.string.key_get, signupUrl.removePrefix("https://")))
        }
    }
}

@Composable
private fun PoolStatus(entropy: EntropyStatus) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.pool_status_bytes, entropy.pooledBytes), style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(
                R.string.pool_status_last,
                entropy.lastOrigin?.let { stringResource(it.labelRes) } ?: stringResource(R.string.pool_status_none),
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
        if (entropy.isFetching) {
            Text(stringResource(R.string.pool_fetching), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        entropy.lastError?.let {
            Text(
                text = stringResource(R.string.pool_status_error, it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
