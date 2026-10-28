# Quantum Duel

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-26-informational)
![Status](https://img.shields.io/badge/status-work%20in%20progress-orange)

A two-player Android duel game where every outcome is decided by **measuring simulated qubits**
seeded with randomness from a **quantum random number generator (QRNG)**.

Two player panels face each other — either a three-reel **slot machine** or **rock–paper–scissors** —
with a single **DRAW** button in the middle. Pressing it puts the qubits into superposition, measures them,
and reveals the winner.

> [!NOTE]
> This repository currently contains the **project skeleton** (first milestone). The build setup,
> architecture, package structure, data models, interfaces, dependency injection and database are in place;
> the game logic is stubbed with `TODO`s. The app builds and runs, and the DRAW button shows a
> "not implemented yet" message. See [Roadmap](#roadmap) for what comes next.

Developed as a university project for the *Mobile Application Project 1* course.

## How it works

1. Each panel gets its own quantum register initialised to |0…0⟩.
2. A Hadamard gate is applied to every qubit, giving each basis state an equal probability.
3. The register is measured (Born rule + state collapse). The random bits needed for measurement come from
   a QRNG, falling back to `SecureRandom` when offline.
4. The results are compared; a tie voids both measurements and the round is re-measured, so every round has
   a winner and each player keeps exactly 50/50 odds.

### Game modes

| Mode | Qubits | Rules |
| --- | --- | --- |
| **Slot machine** | 3 reels × 3 qubits (2³ = 8 symbols per reel) | Three of a kind > pair > high card; equal hand types are decided by symbol points (🍒 1 … 7️⃣ 8). |
| **Rock–paper–scissors** | 2 qubits | \|00⟩ rock, \|01⟩ paper, \|10⟩ scissors. \|11⟩ is discarded and re-measured, so each move has exactly 1/3 probability. |

### Randomness sources

| Source | Description |
| --- | --- |
| [ANU Quantum Numbers](https://quantumnumbers.anu.edu.au/) | Quantum vacuum fluctuation measurements (Australian National University). Requires an API key. |
| [Outshift QRNG](https://qrng.outshift.com/) | Cisco's quantum random number service. Requires an API key. |
| `SecureRandom` | Local fallback used when no key is configured or the network is unavailable. |

Bytes are buffered in an entropy pool that is refilled in the background, so draws rarely have to wait on the
network; if a provider fails or times out, the next one is tried.

## Features

Planned for the full version:

- Slot machine and rock–paper–scissors modes with animated reels
- Qubit state display (kets) and winner highlighting
- Game history stored locally (Room) with win statistics and a χ² uniformity test of the results
- Settings (DataStore): player names, default mode, preferred randomness source, API keys, sound and vibration
- Portrait and landscape layouts, light and dark theme

## Tech stack

| Area | Libraries |
| --- | --- |
| Language | Kotlin 2.0.21, coroutines, kotlinx.serialization |
| UI | Jetpack Compose (BOM 2024.12.01), Material 3, Navigation Compose |
| Architecture | MVVM + Clean Architecture, Hilt 2.52 for dependency injection |
| Networking | Retrofit 2.11.0, OkHttp 4.12.0 |
| Persistence | Room 2.6.1, DataStore Preferences 1.1.1 |
| Build | Gradle 8.11.1, AGP 8.7.3, KSP, Java 17 |
| Testing | JUnit 4, Compose UI tests, Espresso |

## Architecture

The app follows MVVM with Clean Architecture layers, wired together with Hilt:

```text
ui  →  presentation  →  domain  ←  data
```

The `domain` layer is plain Kotlin with no Android dependencies, so it is covered by regular JUnit tests.

```text
app/src/main/java/pl/student/quantumduel/
├── QuantumDuelApp.kt        # @HiltAndroidApp
├── MainActivity.kt          # Compose entry point
├── di/                      # Hilt modules
├── domain/
│   ├── quantum/             # Complex numbers, H/X gates, qubit register simulator
│   ├── game/                # Game modes, slot and RPS rules, duel results
│   ├── entropy/             # Entropy abstractions
│   ├── history/             # Round history models
│   ├── settings/            # App settings
│   └── usecase/             # PlayRoundUseCase, GetStatsUseCase
├── data/
│   ├── remote/              # ANU and Outshift APIs (Retrofit)
│   ├── entropy/             # QRNG providers, entropy pool
│   ├── local/               # Room database
│   └── repository/          # Repository implementations
├── presentation/            # ViewModels
└── ui/                      # Compose screens: game, history, settings, theme, navigation
```

## Getting started

### Requirements

- Android Studio (Ladybug or newer recommended)
- JDK 17
- Android device or emulator running Android 8.0 (API 26) or newer

### Build and run

1. Clone the repository:
   ```bash
   git clone https://github.com/jaroslawstar/quantumduel.git
   ```
2. In Android Studio choose **File → Open**, select the project folder and wait for Gradle sync.
   On first open Android Studio downloads Gradle 8.11.1, the plugins and Android SDK 35.
3. Select an emulator or device and click **Run ‘app’**.

Or from the command line:

```bash
./gradlew assembleDebug
```

### API keys (optional)

QRNG API keys are read from `local.properties` (ignored by git) or environment variables and exposed through
`BuildConfig`:

```properties
ANU_API_KEY=your_anu_key
OUTSHIFT_API_KEY=your_outshift_key
```

Without keys the app uses the local `SecureRandom` source.

## Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented UI tests (requires a running emulator or connected device)
./gradlew connectedDebugAndroidTest
```

In the skeleton, the model tests pass and the logic tests are planned but disabled with `@Ignore`
until the corresponding features are implemented.

## Roadmap

- [x] Milestone 1 – project skeleton: build setup, architecture, models, DI, database, placeholder screens
- [ ] Milestone 2 – quantum logic and game rules (`domain`), domain tests
- [ ] Milestone 3 – data sources: QRNG (ANU, Outshift), entropy pool, Room, DataStore (`data`), data tests
- [ ] Milestone 4 – ViewModels: draw flow, history, settings (`presentation`)
- [ ] Milestone 5 – final UI: animations, statistics with χ² test, settings form (`ui`)
- [ ] Milestone 6 – UI tests, documentation, ANU API key and on-device verification

A detailed, file-by-file description of the skeleton and the steps to complete it is available in
[SZKIELET.md](SZKIELET.md) (in Polish).
