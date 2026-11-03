package pl.student.quantumduel.domain.game

/** Rozstrzyganie pojedynku – czysta funkcja, łatwa do testowania. */
object GameRules {

    /** Zwraca zwycięzcę albo `null`, gdy jest remis (wtedy oba okienka są mierzone ponownie). */
    fun winner(a: PanelResult, b: PanelResult): Player? = when {
        a is SlotsPanel && b is SlotsPanel -> {
            val cmp = a.hand.compareTo(b.hand)
            when {
                cmp > 0 -> Player.A
                cmp < 0 -> Player.B
                else -> null
            }
        }

        a is RpsPanel && b is RpsPanel -> when {
            a.move == b.move -> null
            a.move.beats(b.move) -> Player.A
            else -> Player.B
        }

        else -> throw IllegalArgumentException("Okienka muszą pochodzić z tego samego trybu gry")
    }
}
