package pl.student.quantumduel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.student.quantumduel.presentation.GameUiState
import pl.student.quantumduel.ui.game.DRAW_BUTTON_TAG
import pl.student.quantumduel.ui.game.GameScreen
import pl.student.quantumduel.ui.theme.QuantumDuelTheme

/** Test interfejsu bezstanowego ekranu gry – bez Hilta i bez sieci. ETAP WSTĘPNY. */
@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun drawButtonInvokesCallback() {
        var clicks = 0
        composeRule.setContent {
            QuantumDuelTheme {
                GameScreen(
                    state = GameUiState(),
                    onDrawClick = { clicks++ },
                    onModeSelected = {},
                    onOpenHistory = {},
                    onOpenSettings = {},
                )
            }
        }
        composeRule.onNodeWithTag(DRAW_BUTTON_TAG).performClick()
        assertEquals(1, clicks)
    }

    @Ignore("TODO: wyniki rundy w DuelPanel")
    @Test
    fun revealedRoundHighlightsWinnerAndSource() {
        // po rozstrzygnięciu widać „WYGRYWA”, plakietkę „Kwantowe (ANU)” i kety „|11⟩  →  |00⟩”
    }
}
