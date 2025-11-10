package br.com.mochila

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import br.com.mochila.ui.screens.*

@Composable
fun App() {
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var screenStack by remember { mutableStateOf(listOf("login")) }
    var isMenuVisible by remember { mutableStateOf(false) }

    val currentScreen = screenStack.last()

    fun navigateTo(screen: String) {
        if (screenStack.last() != screen) {
            screenStack = screenStack + screen
        }
        isMenuVisible = false
    }

    fun goBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun openMenu() { isMenuVisible = true }
    fun closeMenu() { isMenuVisible = false }

    fun logout() {
        currentUserId = null
        isMenuVisible = false
        screenStack = listOf("login")
    }

    fun onLoginSuccess(userId: Int) {
        currentUserId = userId
        navigateTo("home")
    }

    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {

                    "login" -> LoginScreen(
                        onNavigateToRegister = { navigateTo("register") },
                        onNavigateToRecovery = { navigateTo("recovery") },
                        onLoginSuccess = ::onLoginSuccess
                    )

                    "register" -> RegisterScreen(onBackToLogin = ::goBack)

                    "recovery" -> RecoveryScreen(onBackToLogin = ::goBack)

                    "home" -> {
                        currentUserId?.let {
                            HomeScreen(
                                userId = it,
                                onNavigateToHome = { navigateTo("home") },
                                onOpenMenu = ::openMenu,
                                onNavigateToAdd = { navigateTo("item_register") },
                                onNavigateToSubject = { navigateTo("subject_detail") },
                                onLogout = ::logout
                            )
                        } ?: logout()
                    }

                    "item_register" -> {
                        currentUserId?.let {
                            ItemRegisterScreen(
                                onNavigateToHome = { navigateTo("home") },
                                onNavigateToSubjectRegister = { navigateTo("subject_register") },
                                onBack = ::goBack,
                                onLogout = ::logout,
                                onOpenMenu = ::openMenu
                            )
                        } ?: logout()
                    }

                    "subject_register" -> {
                        currentUserId?.let {
                            SubjectRegisterScreen(
                                userId = it,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = ::goBack,
                                onLogout = ::logout,
                                onOpenMenu = ::openMenu
                            )
                        } ?: logout()
                    }

                    "subject_detail" -> {
                        currentUserId?.let {
                            SubjectDetailScreen(
                                onNavigateToEdit = { navigateTo("subject_edit") },
                                onNavigateToAbsenceControl = { },
                                onNavigateToItemRegister = { navigateTo("item_register") },
                                onNavigateToHome = { navigateTo("home") },
                                onBack = ::goBack,
                                onLogout = ::logout,
                                onOpenMenu = ::openMenu
                            )
                        } ?: logout()
                    }

                    "subject_edit" -> {
                        currentUserId?.let {
                            SubjectRegisterScreen(
                                userId = it,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = ::goBack,
                                onLogout = ::logout,
                                onOpenMenu = ::openMenu,
                                isEditing = true,
                                subjectData = Subject(
                                    id = 1,
                                    nome = "Engenharia de Software",
                                    professor = "A. Barbosa",
                                    frequencia = "75",
                                    dataInicio = "01/08/25",
                                    dataFim = "15/12/25",
                                    horasAula = "2",
                                    semestre = "5º"
                                )
                            )
                        } ?: logout()
                    }

                    "profile" -> {
                        currentUserId?.let {
                            ProfileScreen(
                                userId = it,
                                onBack = ::goBack
                            )
                        } ?: logout()
                    }
                }

                if (isMenuVisible) {
                    MenuScreen(
                        onCloseMenu = ::closeMenu,
                        onNavigateToHome = { navigateTo("home") },
                        onNavigateToProfile = { navigateTo("profile") }, // ✅ Parâmetro adicionado
                        onLogout = ::logout
                    )
                }
            }
        }
    }
}
