package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import br.com.mochila.auth.AuthViewModel
import br.com.mochila.ui.screens.*

@Composable
fun App() {
    val authViewModel = remember { AuthViewModel() }
    val currentUser by authViewModel.currentUser.collectAsState()

    MaterialTheme {
        Surface {
            if (currentUser == null) {
                // Ninguém logado, mostrar fluxo de autenticação
                AuthNavigation(authViewModel)
            } else {
                // Usuário logado, mostrar conteúdo principal
                MainNavigation() // Removido o authViewModel daqui
            }
        }
    }
}

@Composable
fun AuthNavigation(authViewModel: AuthViewModel) {
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

    when (currentScreen) {
        "login" -> LoginScreen(
            authViewModel = authViewModel,
            onNavigateToRegister = { navigateTo("register") },
            onNavigateToRecovery = { navigateTo("recovery") }
        )
        "register" -> RegisterScreen(
            authViewModel = authViewModel,
            onBackToLogin = { goBack() }
        )
        "recovery" -> RecoveryScreen(
            authViewModel = authViewModel,
            onBackToLogin = { goBack() }
        )
    }
}

@Composable
fun MainNavigation() { // Removido o authViewModel daqui
    var screenStack by remember { mutableStateOf(listOf("home")) }
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

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToHome = { navigateTo("home") },
            onNavigateToMenu = { navigateTo("menu") },
            onNavigateToSubject = { navigateTo("subject_detail") },
            onNavigateToAdd = { navigateTo("item_register") }
        )
        "menu" -> MenuScreen(
            onCloseMenu = { goBack() },
            onNavigateToHome = { navigateTo("home") }
        )
        "item_register" -> ItemRegisterScreen(
            onNavigateToHome = { navigateTo("home") },
            onNavigateToSubjectRegister = { navigateTo("subject_register") },
            onBack = { goBack() }
        )
        "subject_register" -> SubjectRegisterScreen(
            onNavigateToHome = { navigateTo("home") },
            onBack = { goBack() }
        )
        "subject_detail" -> SubjectDetailScreen(
            onNavigateToEdit = { navigateTo("subject_edit") },
            onNavigateToAbsenceControl = { /* TODO: tela de faltas */ },
            onNavigateToItemRegister = { navigateTo("item_register") },
            onNavigateToHome = { navigateTo("home") },
            onBack = { goBack() }
        )
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