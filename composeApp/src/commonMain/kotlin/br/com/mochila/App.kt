package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import br.com.mochila.ui.screens.LoginScreen
import br.com.mochila.ui.screens.RegisterScreen
import br.com.mochila.ui.screens.RecoveryScreen

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("login") }

    MaterialTheme {
        Surface {
            when (currentScreen) {
                "login" -> LoginScreen(
                    onNavigateToRegister = { currentScreen = "register" },
                    onNavigateToRecovery = { currentScreen = "recovery" }
                )
                "register" -> RegisterScreen(
                    onBackToLogin = { currentScreen = "login" }
                )
                "recovery" -> RecoveryScreen(
                    onBackToLogin = { currentScreen = "login" }
                )
            }
        }
    }
}
