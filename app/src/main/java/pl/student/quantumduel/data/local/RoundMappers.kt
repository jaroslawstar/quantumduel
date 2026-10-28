package pl.student.quantumduel.data.local

import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.history.PanelSummary
import pl.student.quantumduel.domain.history.RoundRecord

// Mapowanie między modelem domeny a wierszem bazy danych. ETAP WSTĘPNY.
// Format zapisu okienka: nazwy enumów rozdzielone przecinkami, np. "SEVEN,SEVEN,CHERRY" albo "ROCK".

fun PanelSummary.encode(): String = TODO("PanelSummary.encode")

fun decodePanel(mode: GameMode, value: String): PanelSummary = TODO("decodePanel")

fun DuelRound.toEntity(): RoundEntity = TODO("DuelRound.toEntity")

/** Ma zwracać null dla uszkodzonego wiersza, żeby jeden zły wpis nie blokował całej historii. */
fun RoundEntity.toDomainOrNull(): RoundRecord? = TODO("RoundEntity.toDomainOrNull")
