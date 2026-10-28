package pl.student.quantumduel.domain.game

/** Tryb gry wybierany na ekranie pojedynku. */
enum class GameMode {
    /** Automat kasynowy: 3 bębny po 8 symboli w każdym okienku. */
    SLOTS,

    /** Kamień–papier–nożyczki. */
    RPS,
}

/** Gracz A (górne okienko) albo B (dolne okienko). */
enum class Player { A, B }
