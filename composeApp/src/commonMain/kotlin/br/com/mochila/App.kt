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
    var currentUserId    by remember { mutableStateOf<Int?>(null) }
    var screenStack      by remember { mutableStateOf(listOf("login")) }
    var isMenuVisible    by remember { mutableStateOf(false) }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var selectedTaskId   by remember { mutableStateOf<Int?>(null) }
    var accountPasswordFeedback by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    val currentScreen = screenStack.last()

    fun navigateTo(screen: String) {
        if (screenStack.last() != screen) screenStack = screenStack + screen
    }

    fun goBack() {
        if (screenStack.size > 1) screenStack = screenStack.dropLast(1)
    }

    fun openMenu()  { isMenuVisible = true  }
    fun closeMenu() { isMenuVisible = false }

    fun logout() {
        currentUserId     = null
        isMenuVisible     = false
        screenStack       = listOf("login")
        selectedSubjectId = null
        selectedTaskId    = null
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
                        onLoginSuccess = { userId -> onLoginSuccess(userId) }
                    )

                    "register" -> RegisterScreen(onBackToLogin = { goBack() })

                    "recovery" -> RecoveryScreen(onBackToLogin = { goBack() })

                    "home" -> {
                        currentUserId?.let { userId ->
                            HomeScreen(
                                userId = userId,
                                onNavigateToHome = {},
                                onOpenMenu = { openMenu() },
                                onNavigateToAdd = { navigateTo("item_register") },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onNavigateToSubject = { subjectId ->
                                    selectedSubjectId = subjectId
                                    navigateTo("subject_detail")
                                },
                                onNavigateToTasksList = { navigateTo("tasks_list") },
                                onLogout = { logout() }
                            )
                        } ?: logout()
                    }

                    "item_register" -> {
                        currentUserId?.let { userId ->
                            ItemRegisterScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onNavigateToSubjectRegister = { navigateTo("subject_register") },
                                onNavigateToTaskRegister = { navigateTo("task_register") },
                                onBack = { goBack() },
                                onNavigateToTasksList = { navigateTo("tasks_list") },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onLogout = { logout() }
                            )
                        } ?: logout()
                    }

                    "subject_register" -> {
                        currentUserId?.let { userId ->
                            SubjectRegisterScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = { goBack() },
                                onLogout = { logout() },
                                onOpenMenu = { openMenu() }
                            )
                        } ?: logout()
                    }

                    "subject_detail" -> {
                        currentUserId?.let { userId ->
                            selectedSubjectId?.let { subjectId ->
                                SubjectDetailScreen(
                                    userId = userId,
                                    subjectId = subjectId,
                                    onNavigateToEdit = { subject ->
                                        selectedSubjectId = subject.id
                                        navigateTo("subject_edit")
                                    },
                                    onNavigateToItemRegister = { navigateTo("item_register") },
                                    onNavigateToHome = { navigateTo("home") },
                                    onBack = { goBack() },
                                    onNavigateToTasksList = { navigateTo("tasks_list") },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
                                    onLogout = { logout() }
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "subject_edit" -> {
                        currentUserId?.let { userId ->
                            selectedSubjectId?.let { subjectId ->
                                // ✅ CORRIGIDO: não buscamos mais Subject aqui.
                                // SubjectRegisterScreen recebe apenas o ID e carrega
                                // os dados internamente via SubjectRegisterPresenter.loadSubjectForEdit()
                                SubjectRegisterScreen(
                                    userId = userId,
                                    onNavigateToHome = { navigateTo("home") },
                                    onBack = { goBack() },
                                    onLogout = { logout() },
                                    onOpenMenu = { openMenu() },
                                    isEditing = true,
                                    subjectId = subjectId
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "task_register" -> {
                        currentUserId?.let { userId ->
                            TaskRegisterScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = { goBack() },
                                onLogout = { logout() },
                                onNavigateToTasksList = { navigateTo("tasks_list") },
                                onOpenMenu = { openMenu() }
                            )
                        } ?: logout()
                    }

                    "tasks_list" -> {
                        currentUserId?.let { userId ->
                            TaskListScreen(
                                userId = userId,
                                onNavigateToTaskDetail = { id ->
                                    selectedTaskId = id
                                    navigateTo("task_detail")
                                },
                                onNavigateBack = { goBack() },
                                onOpenMenu = { openMenu() },
                                onNavigateToAdd = { navigateTo("item_register") },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onNavigateToHome = { navigateTo("home") }
                            )
                        } ?: logout()
                    }

                    "task_detail" -> {
                        currentUserId?.let { userId ->
                            selectedTaskId?.let { taskId ->
                                TaskDetailScreen(
                                    userId = userId,
                                    taskId = taskId,
                                    onNavigateToEdit = { task ->
                                        selectedTaskId = task.id
                                        navigateTo("task_edit")
                                    },
                                    onNavigateToHome = { navigateTo("home") },
                                    onBack = { goBack() },
                                    onNavigateToTasksList = { navigateTo("tasks_list") },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
                                    onLogout = { logout() }
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "task_edit" -> {
                        currentUserId?.let { userId ->
                            selectedTaskId?.let { taskId ->
                                TaskRegisterScreen(
                                    userId = userId,
                                    isEditing = true,
                                    taskId = taskId,
                                    onNavigateToHome = { navigateTo("home") },
                                    onBack = { goBack() },
                                    onLogout = { logout() },
                                    onNavigateToTasksList = { navigateTo("tasks_list") },
                                    onOpenMenu = { openMenu() }
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "account_settings" -> {
                        currentUserId?.let { userId ->
                            AccountSettingsScreen(
                                userId = userId,
                                onBack = { goBack() },
                                onLogout = { logout() },
                                onNavigateToChangePassword = { navigateTo("signed_recovery") },
                                passwordFlowMessage = accountPasswordFeedback,
                                onPasswordFlowMessageConsumed = { accountPasswordFeedback = null }
                            )
                        } ?: logout()
                    }

                    "signed_recovery" -> {
                        currentUserId?.let { userId ->
                            SignedRecoveryScreen(
                                userId = userId,
                                onBack = { goBack() },
                                onPasswordChangeFinished = { msg, ok ->
                                    accountPasswordFeedback = msg to ok
                                    goBack()
                                }
                            )
                        } ?: logout()
                    }
                }

                if (isMenuVisible) {
                    currentUserId?.let { userId ->
                        MenuScreen(
                            userId = userId,
                            onCloseMenu = { closeMenu() },
                            onNavigateToHome = { closeMenu(); navigateTo("home") },
                            onNavigateToTasksList = { closeMenu(); navigateTo("tasks_list") },
                            onNavigateToAccountSettings = { closeMenu(); navigateTo("account_settings") },
                            onLogout = { logout() }
                        )
                    }
                }
            }
        }
    }
}