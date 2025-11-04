package br.com.mochila

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import br.com.mochila.ui.screens.*
import br.com.mochila.ui.screens.Subject // ✅ Importação adicionada

@Composable
fun App() {
    // 🔹 Gerenciamento de estado centralizado
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var screenStack by remember { mutableStateOf(listOf("login")) }
    var isMenuVisible by remember { mutableStateOf(false) } // ✅ Estado do menu

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

    fun openMenu() { isMenuVisible = true }
    fun closeMenu() { isMenuVisible = false }

    // 🔹 Logout - Limpa o usuário, fecha o menu e volta para a tela de login
    fun logout() {
        currentUserId = null
        isMenuVisible = false
        screenStack = listOf("login")
    }

    // 🔹 Callback de sucesso do login
    fun onLoginSuccess(userId: Int) {
        currentUserId = userId
        navigateTo("home")
    }

    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                // 🔹 Conteúdo principal da tela
                when (currentScreen) {

                    "login" -> LoginScreen(
                        onNavigateToRegister = { navigateTo("register") },
                        onNavigateToRecovery = { navigateTo("recovery") },
                        onLoginSuccess = { userId -> onLoginSuccess(userId) }
                    )

                    "register" -> RegisterScreen(onBackToLogin = { goBack() })

                    "recovery" -> RecoveryScreen(onBackToLogin = { goBack() })

                    "home" -> {
                        currentUserId?.let {
                            HomeScreen(
                                userId = it,
                                onNavigateToHome = { },
                                onOpenMenu = { openMenu() }, // ✅ Parâmetro corrigido
                                onNavigateToAdd = { navigateTo("item_register") },
                                onNavigateToSubject = { navigateTo("subject_detail") },
                                onLogout = { logout() }
                            )
                        } ?: logout()
                    }

                    "item_register" -> ItemRegisterScreen(
                        onNavigateToHome = { navigateTo("home") },
                        onNavigateToSubjectRegister = { navigateTo("subject_register") },
                        onBack = { goBack() },
                        onLogout = { logout() }
                    )

                    "subject_register" -> {
                        currentUserId?.let {
                            SubjectRegisterScreen(
                                userId = it,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = { goBack() },
                                onLogout = { logout() },
                                onOpenMenu = { openMenu() } // ✅ Parâmetro adicionado
                            )
                        } ?: logout()
                    }

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

                    "subject_edit" -> {
                        currentUserId?.let {
                            SubjectRegisterScreen(
                                userId = it,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = { goBack() },
                                onLogout = { logout() },
                                onOpenMenu = { openMenu() }, // ✅ Parâmetro adicionado
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

                // 🔹 Menu renderizado sobre a tela atual
                if (isMenuVisible) {
                    MenuScreen(
                        onCloseMenu = { closeMenu() }, // ✅ Apenas fecha o menu
                        onNavigateToHome = {
                            closeMenu()
                            navigateTo("home")
                        },
                        onLogout = { logout() }
                    )
                }
            }
        }
    }
}
