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
                AuthNavigation(authViewModel)
            } else {
                MainNavigation(authViewModel)
            }
        }
    }
}

@Composable
private fun AuthNavigation(authViewModel: AuthViewModel) {
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
private fun MainNavigation(authViewModel: AuthViewModel) {
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
            authViewModel = authViewModel,
            onNavigateToHome = { /* Evita empilhar home novamente */ },
            onNavigateToMenu = { navigateTo("menu") },
            onNavigateToSubject = { navigateTo("subject_detail") },
            onNavigateToAdd = { navigateTo("item_register") }
        )
        "menu" -> MenuScreen(
            authViewModel = authViewModel, 
            onCloseMenu = { goBack() },
            onNavigateToHome = { navigateTo("home") }
        )
        "item_register" -> ItemRegisterScreen(
            authViewModel = authViewModel,
            onNavigateToHome = { navigateTo("home") },
            onNavigateToSubjectRegister = { navigateTo("subject_register") },
            onBack = { goBack() }
        )
        "subject_register" -> SubjectRegisterScreen(
            authViewModel = authViewModel,
            onNavigateToHome = { navigateTo("home") },
            onBack = { goBack() }
        )
        "subject_detail" -> SubjectDetailScreen(
            authViewModel = authViewModel, 
            onNavigateToEdit = { navigateTo("subject_edit") },
            onNavigateToAbsenceControl = { /* TODO: tela de faltas */ },
            onNavigateToItemRegister = { navigateTo("item_register") },
            onNavigateToHome = { navigateTo("home") },
            onBack = { goBack() }
        )
        "subject_edit" -> SubjectRegisterScreen(
            authViewModel = authViewModel,
            onNavigateToHome = { navigateTo("home") },
            onBack = { goBack() },
            isEditing = true
        )
    }
}