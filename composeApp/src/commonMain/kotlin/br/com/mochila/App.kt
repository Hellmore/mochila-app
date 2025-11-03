package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import br.com.mochila.ui.screens.*

@Composable
fun App() {
    // 🔹 Gerenciamento de estado centralizado
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var screenStack by remember { mutableStateOf(listOf("login")) }

    val currentScreen = screenStack.last()

    fun navigateTo(screen: String) {
        if (screenStack.last() != screen) {
            screenStack = screenStack + screen
        }
    }

    fun goBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    // 🔹 Logout - Limpa o usuário e volta para a tela de login
    fun logout() {
        currentUserId = null
        screenStack = listOf("login")
    }

    // 🔹 Callback de sucesso do login
    fun onLoginSuccess(userId: Int) {
        currentUserId = userId
        navigateTo("home")
    }

    MaterialTheme {
        Surface {
            when (currentScreen) {

                // 🔸 Tela de Login
                "login" -> LoginScreen(
                    onNavigateToRegister = { navigateTo("register") },
                    onNavigateToRecovery = { navigateTo("recovery") },
                    onLoginSuccess = { userId -> onLoginSuccess(userId) } // ✅ Passa o ID
                )

                // 🔸 Tela de Cadastro
                "register" -> RegisterScreen(onBackToLogin = { goBack() })

                // 🔸 Tela de Recuperação
                "recovery" -> RecoveryScreen(onBackToLogin = { goBack() })

                // 🔸 Tela Home - Agora recebe o ID do usuário
                "home" -> {
                    currentUserId?.let {
                        HomeScreen(
                            userId = it,
                            onNavigateToHome = { },
                            onNavigateToMenu = { navigateTo("menu") },
                            onNavigateToAdd = { navigateTo("item_register") },
                            onNavigateToSubject = { navigateTo("subject_detail") }, // TODO: Passar dados da matéria
                            onLogout = { logout() }
                        )
                    } ?: logout() // Se não houver usuário, volta para o login
                }

                // 🔸 Menu lateral (modal)
                "menu" -> MenuScreen(
                    onCloseMenu = { goBack() },
                    onNavigateToHome = { navigateTo("home") },
                    onLogout = { logout() }
                )

                // 🔸 Tela de Registro de Itens
                "item_register" -> ItemRegisterScreen(
                    onNavigateToHome = { navigateTo("home") },
                    onNavigateToSubjectRegister = { navigateTo("subject_register") },
                    onBack = { goBack() },
                    onLogout = { logout() }
                )

                // 🔸 Tela de Cadastro de Matéria
                "subject_register" -> {
                    currentUserId?.let {
                        SubjectRegisterScreen(
                            userId = it,
                            onNavigateToHome = { navigateTo("home") },
                            onBack = { goBack() },
                            onLogout = { logout() }
                        )
                    } ?: logout()
                }

                // 🔸 Tela de Detalhes da Matéria
                "subject_detail" -> {
                     currentUserId?.let { 
                        SubjectDetailScreen(
                            onNavigateToEdit = { navigateTo("subject_edit") },
                            onNavigateToAbsenceControl = { /* TODO */ },
                            onNavigateToItemRegister = { navigateTo("item_register") },
                            onNavigateToHome = { navigateTo("home") },
                            onBack = { goBack() },
                            onLogout = { logout() }
                        )
                    } ?: logout()
                }

                // 🔸 Tela de Edição de Matéria
                "subject_edit" -> {
                    currentUserId?.let {
                        SubjectRegisterScreen(
                            userId = it,
                            onNavigateToHome = { navigateTo("home") },
                            onBack = { goBack() },
                            onLogout = { logout() },
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
                    } ?: logout()
                }
            }
        }
    }
}
