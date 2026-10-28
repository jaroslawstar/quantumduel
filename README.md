# Quantum Duel – etap wstępny (szkielet)

Aplikacja na Androida (Kotlin, Jetpack Compose) na zajęcia **Projekt Aplikacji Mobilnej 1**:
dwa okienka graczy (automat kasynowy albo kamień–papier–nożyczki), przycisk **LOSUJ** pośrodku
i wynik wyznaczany pomiarem symulowanych kubitów z losowością z kwantowego generatora liczb losowych.

To jest **szkielet projektu**: gotowa konfiguracja, architektura i struktura pakietów, modele danych,
interfejsy i ekrany zastępcze. Logika jest oznaczona `TODO`. Aplikacja się uruchamia, a przycisk LOSUJ
pokazuje komunikat, że losowanie nie jest jeszcze zaimplementowane.

Szczegółowy opis szkieletu, stanu każdego pliku i sposobu uzupełniania: **[SZKIELET.md](SZKIELET.md)**.

## Uruchomienie

1. Android Studio → **File → Open** → folder projektu → poczekaj na *Gradle Sync*.
2. Wybierz emulator lub telefon (Android 8.0 / API 26+) i kliknij **Run ‘app’**.
3. Testy: `./gradlew testDebugUnitTest` (testy logiki są na razie oznaczone `@Ignore`).
