package pl.student.quantumduel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta: głęboki granat kasyna, złoto zwycięzcy i fiolet „kwantowy”.
private val Gold = Color(0xFFF5C451)
private val GoldDeep = Color(0xFF8A5A00)
private val Violet = Color(0xFF9C8CFF)
private val VioletDeep = Color(0xFF5B47D6)
private val Cyan = Color(0xFF4FD8D6)
private val CyanDeep = Color(0xFF00696A)

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF2A1F00),
    primaryContainer = Color(0xFF4A3800),
    onPrimaryContainer = Color(0xFFFFE08F),
    secondary = Violet,
    onSecondary = Color(0xFF1B0F5E),
    secondaryContainer = Color(0xFF3A2C8F),
    onSecondaryContainer = Color(0xFFE4DFFF),
    tertiary = Cyan,
    onTertiary = Color(0xFF003737),
    background = Color(0xFF0E0B1F),
    onBackground = Color(0xFFE7E2F5),
    surface = Color(0xFF0E0B1F),
    onSurface = Color(0xFFE7E2F5),
    surfaceVariant = Color(0xFF2A2540),
    onSurfaceVariant = Color(0xFFC9C3DC),
    surfaceContainer = Color(0xFF1A1630),
    surfaceContainerHigh = Color(0xFF231E3B),
    surfaceContainerHighest = Color(0xFF2D2847),
    outline = Color(0xFF6E6787),
    outlineVariant = Color(0xFF3F3956),
)

private val LightColors = lightColorScheme(
    primary = GoldDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDEA0),
    onPrimaryContainer = Color(0xFF2A1F00),
    secondary = VioletDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4DFFF),
    onSecondaryContainer = Color(0xFF1B0F5E),
    tertiary = CyanDeep,
    onTertiary = Color.White,
    background = Color(0xFFF8F6FF),
    onBackground = Color(0xFF1B1830),
    surface = Color(0xFFF8F6FF),
    onSurface = Color(0xFF1B1830),
    surfaceVariant = Color(0xFFE6E1F2),
    onSurfaceVariant = Color(0xFF484459),
    surfaceContainer = Color(0xFFEEEAF8),
    surfaceContainerHigh = Color(0xFFE8E4F3),
    surfaceContainerHighest = Color(0xFFE2DEEE),
    outline = Color(0xFF79748A),
    outlineVariant = Color(0xFFCAC4D8),
)

/** Styl zapisu stanów kwantowych, np. |101⟩. */
val KetTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    letterSpacing = 0.5.sp,
)

@Composable
fun QuantumDuelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
