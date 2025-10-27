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

                "login" -> LoginScreen(
                    onNavigateToRegister = { currentScreen = "register" },
                    onNavigateToRecovery = { currentScreen = "recovery" },
                    onNavigateToHome = { currentScreen = "home" }
                )

                "register" -> RegisterScreen(onBackToLogin = { currentScreen = "login" })

                "recovery" -> RecoveryScreen(onBackToLogin = { currentScreen = "login" })

                "home" -> HomeScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    onNavigateToMenu = { currentScreen = "menu" },
                    onNavigateToAdd = { currentScreen = "item_register" },
                    onNavigateToSubject = { currentScreen = "subject_detail" }
                )

                "menu" -> MenuScreen(
                    onCloseMenu = { currentScreen = "home" },
                    onNavigateToHome = { currentScreen = "home" }
                )

                "item_register" -> ItemRegisterScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    onNavigateToSubjectRegister = { currentScreen = "subject_register" }
                )

                "subject_detail" -> SubjectDetailScreen(
                    onNavigateToEdit = { currentScreen = "subject_edit" },
                    onNavigateToAbsenceControl = { /* TODO */ },
                    onNavigateToItemRegister = { currentScreen = "item_register" },
                    onNavigateToHome = { currentScreen = "home" }
                )

                "subject_register" -> SubjectRegisterScreen(
                    onNavigateToHome = { currentScreen = "home" }
                )

                "subject_edit" -> SubjectRegisterScreen(
                    onNavigateToHome = { currentScreen = "home" },
                    isEditing = true,
                    subjectData = Subject(
                        nome = "Engenharia de Software",
                        professor = "Anderson Barbosa",
                        frequencia = "75%",
                        dataInicio = "01/08/2025",
                        dataFim = "15/12/2025",
                        horasAula = "2h",
                        semestre = "5º"
                    )
                )
            }
        }
    }
}
