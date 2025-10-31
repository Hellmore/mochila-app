package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import br.com.mochila.ui.screens.*

@Composable
fun App() {
    // 🔹 Pilha de telas
    var screenStack by remember { mutableStateOf(listOf("login")) }

    // 🔹 Tela atual
    val currentScreen = screenStack.last()

    // 🔹 Navegar para nova tela (sem duplicar)
    fun navigateTo(screen: String) {
        if (screenStack.last() != screen) {
            screenStack = screenStack + screen
        }
    }

    // 🔹 Voltar uma tela
    fun goBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    MaterialTheme {
        Surface {
            when (currentScreen) {

                // 🔸 Tela de Login
                "login" -> LoginScreen(
                    onNavigateToRegister = { navigateTo("register") },
//                    onNavigateToRecovery = { navigateTo("recovery") },
                    onNavigateToHome = { navigateTo("home") }
                )

                // 🔸 Tela de Cadastro
                "register" -> RegisterScreen(onBackToLogin = { goBack() })

                // 🔸 Tela de Recuperação
                "recovery" -> RecoveryScreen(onBackToLogin = { goBack() })

                // 🔸 Tela Home
                "home" -> HomeScreen(
                    onNavigateToHome = { /* Evita empilhar home novamente */ },
                    onNavigateToMenu = { navigateTo("menu") },
                    onNavigateToAdd = { navigateTo("item_register") },
                    onNavigateToSubject = { navigateTo("subject_detail") }
                )

                // 🔸 Menu lateral (modal)
                "menu" -> MenuScreen(
                    onCloseMenu = { goBack() },
                    onNavigateToHome = { navigateTo("home") }
                )

                // 🔸 Tela de Registro de Itens
                "item_register" -> ItemRegisterScreen(
                    onNavigateToHome = { navigateTo("home") },
                    onNavigateToSubjectRegister = { navigateTo("subject_register") },
                    onBack = { goBack() }
                )

                // 🔸 Tela de Cadastro de Matéria
                "subject_register" -> SubjectRegisterScreen(
                    onNavigateToHome = { navigateTo("home") },
                    onBack = { goBack() }
                )

                // 🔸 Tela de Detalhes da Matéria
                "subject_detail" -> SubjectDetailScreen(
                    onNavigateToEdit = { navigateTo("subject_edit") },
                    onNavigateToAbsenceControl = { /* TODO: tela de faltas */ },
                    onNavigateToItemRegister = { navigateTo("item_register") },
                    onNavigateToHome = { navigateTo("home") },
                    onBack = { goBack() }
                )

                // 🔸 Tela de Edição de Matéria
                "subject_edit" -> SubjectRegisterScreen(
                    onNavigateToHome = { navigateTo("home") },
                    onBack = { goBack() },
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
