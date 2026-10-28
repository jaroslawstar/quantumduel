# Reguły ProGuard/R8 dla wersji release.
# Minifikacja jest domyślnie wyłączona (isMinifyEnabled = false), więc plik może zostać pusty.
# Po jej włączeniu warto zachować klasy DTO serializowane przez kotlinx.serialization:
# -keep class pl.student.quantumduel.data.remote.** { *; }
