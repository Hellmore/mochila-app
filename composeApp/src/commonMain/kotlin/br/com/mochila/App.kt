package br.com.mochila

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import br.com.mochila.data.UserRepository
import br.com.mochila.data.UserSession
import br.com.mochila.ui.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
@Composable
fun App() {
    var currentUserId    by remember { mutableStateOf<Int?>(null) }
    var screenStack      by remember { mutableStateOf(listOf("login")) }
    var isMenuVisible    by remember { mutableStateOf(false) }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var selectedTaskId   by remember { mutableStateOf<Int?>(null) }
    var accountPasswordFeedback by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var pendingEmail     by remember { mutableStateOf("") }

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
        UserSession.clear()
    }

    fun onLoginSuccess(userId: Int) {
        currentUserId = userId
        screenStack = listOf("home")
    }

    LaunchedEffect(currentUserId) {
        val uid = currentUserId ?: return@LaunchedEffect
        val user = withContext(Dispatchers.IO) { UserRepository.findById(uid) }
        if (user != null) UserSession.set(user)
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

                    "register" -> RegisterScreen(
                        onBackToLogin = { goBack() },
                        onNavigateToEmailVerify = { email ->
                            pendingEmail = email
                            navigateTo("email_verify")
                        }
                    )

                    "email_verify" -> EmailVerifyScreen(
                        email = pendingEmail,
                        onBackToRegister = { goBack() },
                        onNavigateToLogin = {
                            pendingEmail = ""
                            screenStack = listOf("login")
                        }
                    )

                    "recovery" -> RecoveryScreen(
                        onBackToLogin = { goBack() },
                        onNavigateToCodeEntry = { email ->
                            pendingEmail = email
                            navigateTo("email_code")
                        }
                    )

                    "email_code" -> EmailCodeScreen(
                        email = pendingEmail,
                        onBackToRecovery = { goBack() },
                        onNavigateToNewPassword = { navigateTo("new_password") }
                    )

                    "new_password" -> NewPasswordScreen(
                        email = pendingEmail,
                        onNavigateToLogin = {
                            pendingEmail = ""
                            screenStack = listOf("login")
                        },
                        onBack = { goBack() }
                    )

                    "home" -> {
                        currentUserId?.let { userId ->
                            // HomeScreen em stand-by; tela principal = lista de matérias
                            SubjectListScreen(
                                userId = userId,
                                onNavigateToHome = {},
                                onOpenMenu = { openMenu() },
                                onBack = { goBack() },
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
                                onOpenMenu = { openMenu() },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
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
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
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

                if (isMenuVisible && currentUserId != null) {
                    MenuScreen(
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