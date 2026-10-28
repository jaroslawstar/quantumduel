package pl.student.quantumduel.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.game.GameMode

/**
 * Okienko jednego gracza. ETAP WSTĘPNY: układ karty i puste bębny.
 *
 * TODO: liczba wygranych w sesji i plakietka „WYGRYWA”
 * TODO: animacja kręcenia bębnów (AnimatedContent) i zatrzymywanie ich po kolei
 * TODO: zmierzone stany kubitów (np. |101⟩) i opis układu („Para · 17 pkt”)
 * TODO: podświetlenie zwycięzcy (złota ramka, powiększenie) i przyciemnienie przegranego
 */
@Composable
fun DuelPanel(
    name: String,
    mode: GameMode,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(if (mode == GameMode.SLOTS) 3 else 1) { PlaceholderReel() }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.placeholder_panel),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Pusty bęben – znak zapytania zamiast symbolu. */
@Composable
private fun PlaceholderReel() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "?", fontSize = 32.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
