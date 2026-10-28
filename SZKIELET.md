# Quantum Duel – opis szkieletu projektu (etap wstępny)

Ten projekt to **etap wstępny** aplikacji Quantum Duel. Konfiguracja, architektura, struktura pakietów,
modele danych, interfejsy, wstrzykiwanie zależności i baza danych są już gotowe. Logika gry i ekrany
docelowe są zastąpione miejscami oznaczonymi `TODO`.

Szkielet ma **dokładnie te same ścieżki plików** i **te same publiczne nazwy klas, funkcji i parametrów**
co wersja docelowa. Dzięki temu pliki z pełnego projektu można później wkleić w miejsce plików szkieletu
bez zmieniania czegokolwiek innego (patrz [Jak uzupełnić szkielet](#7-jak-uzupełnić-szkielet)).

---

## 1. Co już działa

| Element | Stan |
| --- | --- |
| Budowanie | Pełna konfiguracja Gradle, wszystkie biblioteki, Hilt, Room i KSP. Projekt buduje się i uruchamia. |
| Start aplikacji | `QuantumDuelApp` (Hilt) → `MainActivity` → motyw → nawigacja. |
| Nawigacja | Działają przejścia Gra ↔ Historia ↔ Ustawienia i przycisk Wstecz. |
| Ekran gry | Docelowy układ: przełącznik trybu, okienko gracza A, przycisk **LOSUJ** z wynikiem sesji, okienko gracza B. Zmiana trybu przełącza liczbę bębnów (3 lub 1). |
| Przycisk LOSUJ | Pokazuje komunikat „Etap wstępny: losowanie nie jest jeszcze zaimplementowane”. |
| Historia, Ustawienia | Ekrany zastępcze z listą planowanych funkcji. |
| Wstrzykiwanie zależności | Kompletny graf Hilt: Retrofit, OkHttp, Room, DataStore, repozytoria, przypadki użycia, ViewModele. |
| Testy | 8 testów jednostkowych modeli przechodzi; 33 testy logiki są zaplanowane i wyłączone (`@Ignore`). Test interfejsu sprawdza kliknięcie LOSUJ. |

Nic, co jest wywoływane przy starcie aplikacji ani przy przechodzeniu między ekranami, nie wywołuje
niezaimplementowanych funkcji, więc aplikacja nie zawiesza się w szkielecie.

## 2. Uruchomienie

1. Android Studio → **File → Open** → folder projektu → poczekaj na *Gradle Sync*
   (przy pierwszym otwarciu Studio pobierze Gradle 8.11.1, wtyczki i Android SDK 35).
2. Wybierz emulator lub telefon z Androidem 8.0 (API 26) lub nowszym i kliknij **Run ‘app’**.
3. Testy jednostkowe: `./gradlew testDebugUnitTest`, test interfejsu: `./gradlew connectedDebugAndroidTest`.

## 3. Konfiguracja (taka sama jak w wersji docelowej)

Te pliki są **identyczne** z pełnym projektem i nie trzeba ich później zmieniać:

| Plik | Zawartość |
| --- | --- |
| `settings.gradle.kts` | Repozytoria (Google, Maven Central, Gradle Plugin Portal), projekt `QuantumDuel`, moduł `:app`. |
| `build.gradle.kts` | Wtyczki z `apply false`. |
| `gradle/libs.versions.toml` | Katalog wersji: AGP 8.7.3, Kotlin 2.0.21, KSP 2.0.21-1.0.28, Compose BOM 2024.12.01, Hilt 2.52, Retrofit 2.11.0, OkHttp 4.12.0, Room 2.6.1, DataStore 1.1.1 i inne. |
| `app/build.gradle.kts` | `minSdk 26`, `compileSdk`/`targetSdk 35`, Java 17, Compose, BuildConfig z kluczami API z `local.properties`, wszystkie zależności. |
| `gradle.properties`, `gradle/wrapper/*`, `gradlew`, `gradlew.bat` | Ustawienia Gradle i wrapper 8.11.1. |
| `app/proguard-rules.pro`, `.gitignore` | Reguły R8 (minifikacja wyłączona), ignorowane pliki (m.in. `local.properties`). |
| `AndroidManifest.xml` | Uprawnienie `INTERNET`, klasa aplikacji, aktywność startowa. |
| `res/values/themes.xml`, `colors.xml`, ikona aplikacji | Motyw okna i adaptacyjna ikona wektorowa. |

Klucz API do kwantowego generatora liczb losowych można już teraz wpisać do `local.properties`
(`ANU_API_KEY=…`). Trafi do `BuildConfig`, ale szkielet jeszcze go nie używa.

## 4. Architektura i struktura pakietów

MVVM + warstwy Clean Architecture: `ui` → `presentation` → `domain` ← `data`, całość łączy Hilt (`di`).
Domena to czysty Kotlin bez Androida, więc testuje się ją zwykłymi testami JUnit.

Oznaczenia: ✅ gotowe (identyczne z wersją docelową) · 🧩 szkielet (modele i sygnatury gotowe, ciała `TODO`) ·
🖼️ wersja zastępcza (uproszczony interfejs).

```text
app/src/main/java/pl/student/quantumduel/
├── QuantumDuelApp.kt                    ✅ @HiltAndroidApp
├── MainActivity.kt                      ✅ Compose + motyw + nawigacja
├── di/AppModules.kt                     ✅ moduły Hilt
├── domain/
│   ├── quantum/Complex.kt               ✅ liczba zespolona
│   ├── quantum/Gates.kt                 ✅ bramki H i X
│   ├── quantum/QuantumRegister.kt       🧩 symulator kubitów
│   ├── entropy/Entropy.kt               ✅ EntropyByte, EntropyOrigin, EntropyRepository
│   ├── game/GameMode.kt                 ✅ tryby i gracze
│   ├── game/DuelResult.kt               ✅ wyniki okienek i rundy
│   ├── game/SlotRules.kt                🧩 symbole ✅, układy 🧩
│   ├── game/RpsRules.kt                 🧩 ruchy ✅, reguła beats() 🧩
│   ├── game/GameRules.kt                🧩 rozstrzyganie pojedynku
│   ├── history/History.kt               🧩 modele i interfejs ✅, toSummary() 🧩
│   ├── settings/AppSettings.kt          ✅ ustawienia i interfejs repozytorium
│   ├── usecase/PlayRoundUseCase.kt      🧩 runda z gwarantowanym zwycięzcą
│   └── usecase/GetStatsUseCase.kt       🧩 modele statystyk ✅, obliczenia i test χ² 🧩
├── data/
│   ├── remote/QrngApis.kt               ✅ API ANU i Outshift (Retrofit) + DTO
│   ├── remote/KotlinxJsonConverterFactory.kt ✅ konwerter JSON
│   ├── remote/OutshiftResponseParser.kt 🧩 parser odpowiedzi Outshift
│   ├── entropy/QuantumEntropyProvider.kt ✅ interfejs dostawcy + SecureRandom
│   ├── entropy/QrngProviders.kt         🧩 dostawcy ANU i Outshift
│   ├── entropy/EntropyPool.kt           🧩 pula bajtów (konfiguracja i stan ✅)
│   ├── local/RoundStorage.kt            ✅ Room: encja, DAO, baza
│   ├── local/RoundMappers.kt            🧩 mapowanie encja ↔ model
│   └── repository/*.kt                  🧩 3 repozytoria (konstruktory i wstrzykiwanie ✅)
├── presentation/
│   ├── GameViewModel.kt                 🧩 stan, fazy, efekty ✅; losowanie 🧩
│   ├── HistoryViewModel.kt              🧩 stały pusty stan
│   └── SettingsViewModel.kt             🧩 wartości domyślne, obsługa zmian 🧩
└── ui/
    ├── theme/Theme.kt                   ✅ kolory jasne i ciemne
    ├── navigation/QuantumDuelNavHost.kt ✅ trasy game / history / settings
    ├── components/Common.kt             🖼️ ModeSelector + PlannedFeatures
    ├── game/GameScreen.kt               🖼️ układ ekranu gry
    ├── game/DuelPanel.kt                🖼️ karta gracza z pustymi bębnami
    ├── history/HistoryScreen.kt         🖼️ ekran zastępczy
    └── settings/SettingsScreen.kt       🖼️ ekran zastępczy
```

## 5. Co jest do zrobienia w każdym pliku

| Plik | Co jest w szkielecie | Co dopisać |
| --- | --- | --- |
| `QuantumRegister.kt` | Konstruktor z walidacją, wektor stanu w \|0…0⟩, `probabilities()`, `amplitudesSnapshot()` | `apply`, `applyToAll`, `probabilityOfZero`, `measure` (reguła Borna + kolaps), `measureAll`, `ket`, `toIndex` |
| `SlotRules.kt` | `SlotSymbol` (8 symboli, punkty 1–8), `SlotHandType` | `SlotHand.compareTo`, `SlotHand.of` |
| `RpsRules.kt` | `RpsMove`, `fromIndexOrNull` (\|11⟩ → `null`) | `beats` |
| `GameRules.kt` | Sygnatura `winner(a, b)` | Porównanie układów, macierz KPN, remis = `null`, błąd przy różnych trybach |
| `History.kt` | `PanelSummary`, `RoundRecord`, `HistoryRepository` | `toSummary()` |
| `PlayRoundUseCase.kt` | Konstruktor, stałe (3 bębny × 3 kubity, 2 kubity KPN, limity) | Pomiar okienek, ponowny pomiar przy remisie, liczenie kubitów i źródła |
| `GetStatsUseCase.kt` | `DuelStats`, `UniformityTest`, progi χ² | Zliczanie wygranych, symboli i ruchów, test χ² |
| `OutshiftResponseParser.kt` | Sygnatura `parse()` i opis formatów | Odczyt tablicy liczb w kilku formatach |
| `QrngProviders.kt` | Klasy z `@Inject`, `origin` | `fetch()`: klucz z ustawień, wywołanie API, obsługa błędów HTTP |
| `EntropyPool.kt` | Konstruktor, `Config` (1024 / 256 / 5 s / 60 s), `status` | Bufor z `Mutex`, dobieranie w tle, kolejni dostawcy, limit czasu, karencja, `SecureRandom` |
| `RoundMappers.kt` | Sygnatury 4 funkcji | Zapis okienka jako tekst i odczyt z bazy |
| `EntropyRepositoryImpl.kt` | Tworzenie puli i `status` | `nextByte`, `prefetch`, kolejność dostawców według ustawień |
| `HistoryRepositoryImpl.kt` | Konstruktor z `RoundDao` | `observeHistory`, `save`, `clear` |
| `SettingsRepositoryImpl.kt` | Konstruktor z `DataStore` | Odczyt i zapis 8 ustawień, klucze API z `BuildConfig` jako domyślne |
| `GameViewModel.kt` | `DrawPhase`, `GameUiState`, `GameEffect`, zmiana trybu, zerowanie wyniku | Subskrypcje w `init`, pełny przebieg `onDrawClicked` |
| `HistoryViewModel.kt` | `HistoryUiState`, stały pusty stan | `combine` historii i ustawień, statystyki, czyszczenie |
| `SettingsViewModel.kt` | Formularz z wartościami domyślnymi | Wczytanie i zapisywanie ustawień, test połączenia |
| `ui/…` | Układ ekranów i ekrany zastępcze | Animacje bębnów, kety, podświetlenie zwycięzcy, układ poziomy, historia ze statystykami, formularz ustawień, dźwięk i wibracja |
| `strings.xml` | Teksty używane przez szkielet + teksty zastępcze | Pełny zestaw tekstów (ok. 80 pozycji) |
| Testy | 8 działających testów modeli, 33 testy z `@Ignore` | Treść testów logiki |

## 6. Konwencje szkieletu

- **`TODO("…")`** – funkcja Kotlina, która rzuca `NotImplementedError`. Oznacza ciało do napisania (33 miejsca).
- **`// TODO: …`** – komentarz z opisem, co ma się tam znaleźć. Wszystkie są widoczne w Android Studio:
  **View → Tool Windows → TODO**.
- **ETAP WSTĘPNY** w komentarzu KDoc klasy oznacza plik, który zostanie zastąpiony.
- **`@Ignore("TODO: …")`** przy teście – test zaplanowany, włączany po zaimplementowaniu danej funkcji.
- Klasy, które Hilt tworzy przy starcie (repozytoria, pula entropii, ViewModele), mają w szkielecie tylko
  bezpieczne konstruktory, a `TODO()` są w metodach, których szkielet jeszcze nie wywołuje.

## 7. Jak uzupełnić szkielet

### Wariant A – wszystko naraz

1. Skopiuj folder `app/src` z pełnego projektu do tego projektu i zgódź się na zastąpienie plików
   (szkielet nie ma żadnych dodatkowych plików, więc nic nie zostanie „osierocone”).
2. Skopiuj `README.md`, a `SZKIELET.md` usuń albo zostaw jako dokumentację etapu.
3. W Android Studio: **Build → Rebuild Project**, potem uruchom aplikację i `./gradlew testDebugUnitTest`.

### Wariant B – krok po kroku (warstwami, od dołu)

Pliki z warstw `domain`, `data` i `presentation` mają w szkielecie te same publiczne sygnatury co w wersji
docelowej, więc można je podmieniać nawet pojedynczo – projekt nadal się skompiluje. Najwygodniej jednak
iść warstwami od dołu, bo wtedy po każdym kroku aplikacja działa:

| Krok | Co skopiować z pełnego projektu | Efekt po kroku |
| --- | --- | --- |
| 1 | `domain/**` (pliki 🧩) oraz testy `QuantumRegisterTest`, `GameRulesTest`, `PlayRoundUseCaseTest`, `GetStatsUseCaseTest` | Symulator kubitów i reguły działają; testy domeny przechodzą. Aplikacja wygląda jak dotąd. |
| 2 | `data/**` (pliki 🧩) oraz testy `EntropyPoolTest`, `OutshiftResponseParserTest` | Pula entropii, QRNG, Room i DataStore działają; wszystkie testy jednostkowe przechodzą. |
| 3 | `presentation/*.kt` | LOSUJ naprawdę losuje: na ekranie szkieletu zmienia się wynik sesji, a ekran historii pokazuje liczbę zapisanych losowań. |
| 4 | `ui/components/Common.kt`, `ui/game/*`, `ui/history/*`, `ui/settings/*` i `res/values/strings.xml` – **razem** | Pełny interfejs: animacje, kety, statystyki, formularz ustawień. |
| 5 | `androidTest/…/GameScreenTest.kt`, `README.md` | Pełny test interfejsu i dokumentacja. |

Uwagi:

- W kroku 4 pliki interfejsu i `strings.xml` trzeba podmienić w jednym ruchu: pełne ekrany używają
  tekstów, których szkielet nie ma, a ekrany zastępcze – tekstów, których nie ma wersja pełna.
- Kolejność od dołu ma znaczenie przy **uruchamianiu**, nie przy kompilacji: np. pełny `GameViewModel`
  przy starcie pobiera bajty z puli entropii, więc z niezaimplementowaną warstwą danych zatrzymałby się
  na `TODO()`.
- Pliki oznaczone ✅ są identyczne w obu projektach – nie trzeba ich kopiować.
- Po podmianie plików z adnotacjami Hilt lub Room warto zrobić **Build → Rebuild Project**, żeby KSP
  wygenerował kod od nowa.

## 8. Plan kolejnych etapów

- [ ] Etap 2 – logika kwantowa i reguły gry (`domain`), testy domeny
- [ ] Etap 3 – źródła danych: QRNG (ANU, Outshift), pula entropii, Room, DataStore (`data`), testy danych
- [ ] Etap 4 – ViewModele: przebieg losowania, historia, ustawienia (`presentation`)
- [ ] Etap 5 – interfejs docelowy: animacje, statystyki z testem χ², formularz ustawień (`ui`)
- [ ] Etap 6 – test interfejsu, dokumentacja, klucz API ANU i sprawdzenie na urządzeniu
