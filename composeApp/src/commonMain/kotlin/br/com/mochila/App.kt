package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import br.com.mochila.ui.screens.*

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("login") }

    MaterialTheme {
        Surface {
            when (currentScreen) {

                // 🔹 Tela de Login
                "login" -> LoginScreen(
                    onNavigateToRegister = { currentScreen = "register" },
                    onNavigateToRecovery = { currentScreen = "recovery" },
                    onNavigateToHome = { currentScreen = "home" }
                )

                // 🔹 Tela de Registro
                "register" -> RegisterScreen(
                    onBackToLogin = { currentScreen = "login" }
                )

                // 🔹 Tela de Recuperação de Senha
                "recovery" -> RecoveryScreen(
                    onBackToLogin = { currentScreen = "login" }
                )

                // 🔹 Tela Home
                "home" -> HomeScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    onNavigateToMenu = { /* TODO: abrir menu lateral */ },
                    onNavigateToAdd = { /* TODO: adicionar matéria */ },
                    onNavigateToSubject = { subjectName ->
                        // TODO: navegar para a tela da matéria (ex: currentScreen = "subject_$subjectName")
                    }
                )

                // Menu Lateral
                "menu" -> MenuScreen(
                    onCloseMenu = { currentScreen = "home" },
                    onNavigateToHome = { currentScreen = "home" }
                )
            }
        }
    }
}
