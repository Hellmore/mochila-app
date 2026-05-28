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
// Composable raiz: gerencia navegacao manual e estado global da aplicacao
@Composable
fun App() {
    // Estado de navegacao, usuario logado e selecoes entre telas
    var currentUserId    by remember { mutableStateOf<Int?>(null) }
    var screenStack      by remember { mutableStateOf(listOf("login")) }
    var isMenuVisible    by remember { mutableStateOf(false) }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var selectedTaskId   by remember { mutableStateOf<Int?>(null) }
    var selectedEventId  by remember { mutableStateOf<Int?>(null) }
    var selectedFaltaId       by remember { mutableStateOf<Int?>(null) }
    var selectedAdminUserId   by remember { mutableStateOf<Int?>(null) }
    var accountPasswordFeedback by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var pendingEmail     by remember { mutableStateOf("") }
    var taskSubjectId      by remember { mutableStateOf<Int?>(null) }
    var faltaSubjectId     by remember { mutableStateOf<Int?>(null) }
    var faltaSubjectFilter by remember { mutableStateOf("Todos") }
    var notifVersion       by remember { mutableStateOf(0) }

    val currentScreen = screenStack.last()

    // Helpers para empilhar telas, voltar e controlar o menu
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
        selectedEventId   = null
        UserSession.clear()
    }

    fun onLoginSuccess(userId: Int) {
        currentUserId = userId
        screenStack = listOf("home")
    }

    // Sincroniza UserSession quando o usuario autenticado muda
    LaunchedEffect(currentUserId) {
        val uid = currentUserId ?: return@LaunchedEffect
        val user = withContext(Dispatchers.IO) { UserRepository.findById(uid) }
        if (user != null) UserSession.set(user)
    }

    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                // Exibe a tela no topo da pilha de navegacao
                when (currentScreen) {

                    // Fluxo de login, cadastro e recuperacao de senha
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
                        onNavigateToNewPassword = {
                            screenStack = screenStack.dropLast(1) + "new_password"
                        }
                    )

                    "new_password" -> NewPasswordScreen(
                        email = pendingEmail,
                        onNavigateToLogin = {
                            pendingEmail = ""
                            screenStack = listOf("login")
                        },
                        onBack = { goBack() }
                    )

                    // Area principal apos autenticacao
                    "home" -> {
                        currentUserId?.let { userId ->
                            HomeScreen(
                                userId = userId,
                                onOpenMenu = { openMenu() },
                                onNavigateToAdd = { navigateTo("subject_register") },
                                onNavigateToSubjectsList = { navigateTo("subjects_list") },
                                onNavigateToSubject = { subjectId ->
                                    selectedSubjectId = subjectId
                                    navigateTo("subject_detail")
                                },
                                onNavigateToTasksList = { navigateTo("tasks_list") },
                                onNavigateToTaskDetail = { id ->
                                    selectedTaskId = id
                                    navigateTo("task_detail")
                                },
                                onNavigateToFaltasList = { name -> faltaSubjectFilter = name; navigateTo("faltas_list") },
                                onNavigateToEventsList = { navigateTo("events_list") },
                                onNavigateToEventDetail = { id ->
                                    selectedEventId = id
                                    navigateTo("event_detail")
                                },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onLogout = { logout() },
                                notifVersion = notifVersion,
                            )
                        } ?: logout()
                    }

                    "subjects_list" -> {
                        currentUserId?.let { userId ->
                            SubjectListScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onOpenMenu = { openMenu() },
                                onBack = { goBack() },
                                onNavigateToAdd = { navigateTo("subject_register") },
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
                                    onNavigateToTaskRegister = {
                                    taskSubjectId = selectedSubjectId
                                    navigateTo("task_register")
                                },
                                onNavigateToFaltaRegister = {
                                    faltaSubjectId = selectedSubjectId
                                    navigateTo("falta_register")
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

                    "subject_edit" -> {
                        currentUserId?.let { userId ->
                            selectedSubjectId?.let { subjectId ->
                                
                                
                                
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
                                onBack = { taskSubjectId = null; goBack() },
                                onLogout = { logout() },
                                onNavigateToTasksList = { taskSubjectId = null; navigateTo("tasks_list") },
                                onOpenMenu = { openMenu() },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                preselectedSubjectId = taskSubjectId,
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
                                onBack = { goBack() },
                                onOpenMenu = { openMenu() },
                                onNavigateToAdd = { taskSubjectId = null; navigateTo("task_register") },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onNavigateToHome = { navigateTo("home") },
                                onLogout = { logout() },
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
                                    onOpenMenu = { openMenu() },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
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

                    "events_list" -> {
                        currentUserId?.let { userId ->
                            EventListScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onOpenMenu = { openMenu() },
                                onBack = { goBack() },
                                onNavigateToAdd = { navigateTo("event_register") },
                                onNavigateToEventDetail = { id ->
                                    selectedEventId = id
                                    navigateTo("event_detail")
                                },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onLogout = { logout() },
                            )
                        } ?: logout()
                    }

                    "event_detail" -> {
                        currentUserId?.let { userId ->
                            selectedEventId?.let { eid ->
                                EventDetailScreen(
                                    userId = userId,
                                    eventId = eid,
                                    onNavigateToEdit = { event ->
                                        selectedEventId = event.id
                                        navigateTo("event_edit")
                                    },
                                    onNavigateToHome = { navigateTo("home") },
                                    onNavigateToEventsList = { navigateTo("events_list") },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
                                    onBack = { goBack() },
                                    onLogout = { logout() },
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "event_register" -> {
                        currentUserId?.let { userId ->
                            EventRegisterScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = { goBack() },
                                onLogout = { logout() },
                                onOpenMenu = { openMenu() },
                                onNavigateToEventsList = { goBack() },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                            )
                        } ?: logout()
                    }

                    "faltas_list" -> {
                        currentUserId?.let { userId ->
                            FaltaListScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onOpenMenu = { openMenu() },
                                onBack = { goBack() },
                                onNavigateToAdd = { navigateTo("falta_register") },
                                onNavigateToFaltaDetail = { id ->
                                    selectedFaltaId = id
                                    navigateTo("falta_detail")
                                },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                onLogout = { logout() },
                                initialSubjectFilter = faltaSubjectFilter,
                            )
                        } ?: logout()
                    }

                    "falta_detail" -> {
                        currentUserId?.let { userId ->
                            selectedFaltaId?.let { fid ->
                                FaltaDetailScreen(
                                    userId = userId,
                                    faltaId = fid,
                                    onNavigateToEdit = { falta ->
                                        selectedFaltaId = falta.id
                                        navigateTo("falta_edit")
                                    },
                                    onNavigateToHome = { navigateTo("home") },
                                    onNavigateToFaltasList = { navigateTo("faltas_list") },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
                                    onBack = { goBack() },
                                    onLogout = { logout() },
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "falta_register" -> {
                        currentUserId?.let { userId ->
                            FaltaRegisterScreen(
                                userId = userId,
                                onNavigateToHome = { navigateTo("home") },
                                onBack = { faltaSubjectId = null; goBack() },
                                onLogout = { logout() },
                                onOpenMenu = { openMenu() },
                                onNavigateToFaltasList = { faltaSubjectId = null; goBack() },
                                onNavigateToAccountSettings = { navigateTo("account_settings") },
                                preselectedSubjectId = faltaSubjectId,
                            )
                        } ?: logout()
                    }

                    "falta_edit" -> {
                        currentUserId?.let { userId ->
                            selectedFaltaId?.let { fid ->
                                FaltaRegisterScreen(
                                    userId = userId,
                                    isEditing = true,
                                    faltaId = fid,
                                    onNavigateToHome = { navigateTo("home") },
                                    onBack = { goBack() },
                                    onLogout = { logout() },
                                    onOpenMenu = { openMenu() },
                                    onNavigateToFaltasList = { goBack() },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    // Painel administrativo (somente admin)
                    "admin_panel" -> {
                        if (UserSession.currentUser?.isAdmin != true) { navigateTo("home") }
                        else AdminPanelScreen(
                            onNavigateToUsers = { navigateTo("admin_users") },
                            onNavigateToLogs = { navigateTo("admin_logs") },
                            onBack = { goBack() },
                        )
                    }

                    "admin_users" -> {
                        if (UserSession.currentUser?.isAdmin != true) { navigateTo("home") }
                        else currentUserId?.let { uid ->
                            AdminUsersScreen(
                                currentUserId = uid,
                                onBack = { goBack() },
                                onNavigateToUserDetail = { targetId ->
                                    selectedAdminUserId = targetId
                                    navigateTo("admin_user_detail")
                                },
                            )
                        } ?: logout()
                    }

                    "admin_user_detail" -> {
                        if (UserSession.currentUser?.isAdmin != true) { navigateTo("home") }
                        else currentUserId?.let { uid ->
                            selectedAdminUserId?.let { targetId ->
                                AdminUserDetailScreen(
                                    currentAdminId = uid,
                                    targetUserId = targetId,
                                    onBack = { goBack() },
                                )
                            } ?: goBack()
                        } ?: logout()
                    }

                    "admin_logs" -> {
                        if (UserSession.currentUser?.isAdmin != true) { navigateTo("home") }
                        else AdminLogsScreen(onBack = { goBack() })
                    }

                    "plans" -> PlansScreen(onBack = { goBack() })

                    "event_edit" -> {
                        currentUserId?.let { userId ->
                            selectedEventId?.let { eid ->
                                EventRegisterScreen(
                                    userId = userId,
                                    isEditing = true,
                                    eventId = eid,
                                    onNavigateToHome = { navigateTo("home") },
                                    onBack = { goBack() },
                                    onLogout = { logout() },
                                    onOpenMenu = { openMenu() },
                                    onNavigateToEventsList = { goBack() },
                                    onNavigateToAccountSettings = { navigateTo("account_settings") },
                                )
                            } ?: goBack()
                        } ?: logout()
                    }
                }

                // Monitores em segundo plano para lembretes e notificacoes
                currentUserId?.let { uid ->
                    EventReminderMonitor(userId = uid)
                    TaskReminderMonitor(userId = uid)
                    NotificationMonitor(userId = uid, onChecked = { notifVersion++ })
                }

                // Menu lateral sobreposto as telas autenticadas
                if (isMenuVisible && currentUserId != null) {
                    MenuScreen(
                        onCloseMenu = { closeMenu() },
                        onNavigateToHome = { closeMenu(); navigateTo("home") },
                        onNavigateToSubjectsList = { closeMenu(); navigateTo("subjects_list") },
                        onNavigateToTasksList = { closeMenu(); navigateTo("tasks_list") },
                        onNavigateToEventos = { closeMenu(); navigateTo("events_list") },
                        onNavigateToPlanoFaltas = { closeMenu(); faltaSubjectFilter = "Todos"; navigateTo("faltas_list") },
                        onNavigateToAccountSettings = { closeMenu(); navigateTo("account_settings") },
                        onNavigateToAdmin = { closeMenu(); navigateTo("admin_panel") },
                        onNavigateToPlus = { closeMenu(); navigateTo("plans") },
                        onLogout = { logout() }
                    )
                }
            }
        }
    }
}