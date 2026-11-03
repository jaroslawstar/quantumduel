package pl.student.quantumduel.domain.game

/**
 * Ruch w kamień–papier–nożyczki. Indeks to wynik pomiaru 2 kubitów:
 * |00⟩ kamień, |01⟩ papier, |10⟩ nożyczki. Wynik |11⟩ jest odrzucany i mierzony ponownie,
 * dzięki czemu każdy ruch ma dokładnie 1/3 szans.
 */
enum class RpsMove {
    ROCK,
    PAPER,
    SCISSORS;

    fun beats(other: RpsMove): Boolean = when (this) {
        ROCK -> other == SCISSORS
        PAPER -> other == ROCK
        SCISSORS -> other == PAPER
    }

    companion object {
        /** Liczba poprawnych wyników pomiaru (3 z 4 możliwych). */
        const val VALID_OUTCOMES = 3

        fun fromIndexOrNull(index: Int): RpsMove? = entries.getOrNull(index)
    }
}
