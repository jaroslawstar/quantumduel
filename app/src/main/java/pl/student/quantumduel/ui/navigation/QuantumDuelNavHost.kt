package pl.student.quantumduel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.student.quantumduel.ui.game.GameRoute
import pl.student.quantumduel.ui.history.HistoryRoute
import pl.student.quantumduel.ui.settings.SettingsRoute

object Routes {
    const val GAME = "game"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

/** Nawigacja: Gra ↔ Historia ↔ Ustawienia. */
@Composable
fun QuantumDuelNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.GAME) {
        composable(Routes.GAME) {
            GameRoute(
                onOpenHistory = { navController.navigate(Routes.HISTORY) { launchSingleTop = true } },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) { launchSingleTop = true } },
            )
        }
        composable(Routes.HISTORY) {
            HistoryRoute(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsRoute(onBack = { navController.popBackStack() })
        }
    }
}
