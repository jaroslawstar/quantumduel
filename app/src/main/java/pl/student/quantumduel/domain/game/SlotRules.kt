package pl.student.quantumduel.domain.game

/**
 * Symbol bębna. Indeks (0–7) to wynik pomiaru 3 kubitów: 2³ = 8 równie prawdopodobnych stanów.
 * [points] służy do rozstrzygania układów tego samego typu.
 */
enum class SlotSymbol(val points: Int) {
    CHERRY(1),
    LEMON(2),
    ORANGE(3),
    GRAPE(4),
    BELL(5),
    STAR(6),
    DIAMOND(7),
    SEVEN(8);

    companion object {
        fun fromIndex(index: Int): SlotSymbol = entries[index]
    }
}

/** Typ układu na bębnach – od najsłabszego do najmocniejszego. */
enum class SlotHandType(val rank: Int) {
    HIGH(0),
    PAIR(1),
    TRIPLE(2),
}

/**
 * Układ trzech symboli. Porównanie: trójka > para > brak układu;
 * przy tym samym typie wygrywa wyższa suma punktów. Równe układy = remis.
 *
 * ETAP WSTĘPNY: model gotowy, reguły do zaimplementowania.
 */
data class SlotHand(val type: SlotHandType, val points: Int) : Comparable<SlotHand> {

    override fun compareTo(other: SlotHand): Int {
        // TODO: najpierw porównać type.rank, a przy równym typie – points
        TODO("SlotHand.compareTo")
    }

    companion object {
        fun of(symbols: List<SlotSymbol>): SlotHand {
            // TODO: policzyć powtórzenia symboli (3 → TRIPLE, 2 → PAIR, inaczej HIGH) i zsumować punkty
            TODO("SlotHand.of")
        }
    }
}
