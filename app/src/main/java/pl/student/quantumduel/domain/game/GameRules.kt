package pl.student.quantumduel.domain.game

/** Rozstrzyganie pojedynku – czysta funkcja, łatwa do testowania. ETAP WSTĘPNY. */
object GameRules {

    /** Zwraca zwycięzcę albo `null`, gdy jest remis (wtedy oba okienka są mierzone ponownie). */
    fun winner(a: PanelResult, b: PanelResult): Player? {
        // TODO: automat – porównać a.hand z b.hand (większy wygrywa, równe = null)
        // TODO: KPN – ten sam ruch = null, w przeciwnym razie wygrywa ruch, który bije drugi (RpsMove.beats)
        // TODO: okienka z różnych trybów → IllegalArgumentException
        TODO("GameRules.winner")
    }
}
