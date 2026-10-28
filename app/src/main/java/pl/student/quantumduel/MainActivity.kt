package pl.student.quantumduel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pl.student.quantumduel.ui.navigation.QuantumDuelNavHost
import pl.student.quantumduel.ui.theme.QuantumDuelTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuantumDuelTheme {
                QuantumDuelNavHost()
            }
        }
    }
}
