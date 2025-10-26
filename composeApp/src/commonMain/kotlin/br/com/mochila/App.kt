package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import br.com.mochila.ui.screens.LoginScreen
import br.com.mochila.ui.screens.RegisterScreen

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("login") }

    MaterialTheme {
        Surface {
            when (currentScreen) {
                "login" -> LoginScreen(onNavigateToRegister = { currentScreen = "register" })
                "register" -> RegisterScreen(onBackToLogin = { currentScreen = "login" })
            }
        }
    }
}
