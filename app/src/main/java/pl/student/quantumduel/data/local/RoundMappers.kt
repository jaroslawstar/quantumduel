package pl.student.quantumduel.data.local

import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotSymbol
import pl.student.quantumduel.domain.history.PanelSummary
import pl.student.quantumduel.domain.history.RoundRecord
import pl.student.quantumduel.domain.history.toSummary

private const val SEPARATOR = ","

fun PanelSummary.encode(): String = when (this) {
    is PanelSummary.Slots -> symbols.joinToString(SEPARATOR) { it.name }
    is PanelSummary.Rps -> move.name
}

fun decodePanel(mode: GameMode, value: String): PanelSummary = when (mode) {
    GameMode.SLOTS -> PanelSummary.Slots(value.split(SEPARATOR).map { SlotSymbol.valueOf(it) })
    GameMode.RPS -> PanelSummary.Rps(RpsMove.valueOf(value))
}

fun DuelRound.toEntity(): RoundEntity = RoundEntity(
    timestampMillis = timestampMillis,
    mode = mode.name,
    winner = winner.name,
    panelA = panelA.toSummary().encode(),
    panelB = panelB.toSummary().encode(),
    attempts = attempts,
    measuredQubits = measuredQubits,
    origin = origin.name,
)

/** Zwraca null dla uszkodzonego wiersza, żeby jeden zły wpis nie blokował całej historii. */
fun RoundEntity.toDomainOrNull(): RoundRecord? = runCatching {
    val gameMode = GameMode.valueOf(mode)
    RoundRecord(
        id = id,
        timestampMillis = timestampMillis,
        mode = gameMode,
        winner = Player.valueOf(winner),
        panelA = decodePanel(gameMode, panelA),
        panelB = decodePanel(gameMode, panelB),
        attempts = attempts,
        measuredQubits = measuredQubits,
        origin = EntropyOrigin.valueOf(origin),
    )
}.getOrNull()
