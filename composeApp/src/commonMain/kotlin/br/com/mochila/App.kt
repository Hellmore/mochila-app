package br.com.mochila

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import br.com.mochila.ui.screens.*
import br.com.mochila.data.*

@Composable
fun App() {
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var screenStack by remember { mutableStateOf(listOf("login")) }
    var isMenuVisible by remember { mutableStateOf(false) }
    var selectedMateriaId by remember { mutableStateOf<Int?>(null) }
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }

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

    fun logout() {
        currentUserId = null
        isMenuVisible = false
        screenStack = listOf("login")
        selectedMateriaId = null
        selectedTaskId = null
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
                                onNavigateToHome = { /* já estamos na home */ },
                                onOpenMenu = { openMenu() },
                                onNavigateToAdd = { navigateTo("item_register") },
                                onNavigateToSubject = { materiaId ->
                                    selectedMateriaId = materiaId
                                    navigateTo("subject_detail")
                                },
                                onNavigateToTasksList = {
                                    navigateTo("tasks_list")
                                },
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
                            selectedMateriaId?.let { materiaId ->
                                // usa o método existente no seu repositório para buscar por id
                                val materia: Materia? = MateriaRepository.buscarPorId(materiaId)

                                materia?.let { m ->
                                    SubjectDetailScreen(
                                        materia = m,
                                        onNavigateToEdit = { materia ->
                                            selectedMateriaId = materia.id_disciplina
                                            navigateTo("subject_edit")
                                        },
                                        onNavigateToAbsenceControl = { materia ->
                                            navigateTo("home")
                                        },
                                        onNavigateToItemRegister = { navigateTo("item_register") },
                                        onNavigateToHome = { navigateTo("home") },
                                        onBack = { goBack() },
                                        onNavigateToTasksList = { navigateTo("tasks_list") },
                                        onNavigateToAccountSettings = { navigateTo("account_settings") },
                                        userId = userId,
                                        onLogout = { logout() }
                                    )
                                } ?: run {
                                    // se não encontrou a matéria, volta para home
                                    goBack()
                                }
                            } ?: goBack()
                        } ?: logout()
                    }

                    "subject_edit" -> {
                        currentUserId?.let { userId ->
                            selectedMateriaId?.let { materiaId ->
                                val materia: Materia? = MateriaRepository.buscarPorId(materiaId)

                                materia?.let { m ->
                                    // converter Materia -> Subject (data class do subject-register.kt)
                                    val subjectData = Subject(
                                        id = m.id_disciplina,
                                        nome = m.nome,
                                        professor = m.professor,
                                        frequencia = m.frequencia_minima.toString(),
                                        dataInicio = m.data_inicio,
                                        dataFim = m.data_fim,
                                        horasAula = m.hora_aula.toString(),
                                        semestre = m.semestre
                                    )

                                    SubjectRegisterScreen(
                                        userId = userId,
                                        onNavigateToHome = { navigateTo("home") },
                                        onBack = { goBack() },
                                        onLogout = { logout() },
                                        onOpenMenu = { openMenu() },
                                        isEditing = true,
                                        subjectData = subjectData
                                    )
                                } ?: run {
                                    goBack()
                                }
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
                                onOpenMenu = { openMenu() },
                            )
                        } ?: logout()
                    }

                    "tasks_list" -> {
                        currentUserId?.let {
                            TaskListScreen(
                                userId = it,
                                onNavigateToTaskDetail = { id ->
                                    selectedTaskId = id
                                    navigateTo("task_detail")
                                },
                                onNavigateBack = { goBack() },
                                onOpenMenu = { openMenu() },
                                onNavigateToAdd = { navigateTo("item_register") },
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
                                    onNavigateToEdit = { tarefa ->
                                        selectedTaskId = tarefa.id_tarefa
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
                                onLogout = { logout() }
                            )
                        } ?: logout()
                    }
                }

                if (isMenuVisible) {
                    currentUserId?.let { userId ->
                        MenuScreen(
                            userId = userId,
                            onCloseMenu = { closeMenu() },
                            onNavigateToHome = {
                                closeMenu()
                                navigateTo("home")
                            },
                            onNavigateToTasksList = {
                                closeMenu()
                                navigateTo("tasks_list")
                            },
                            onNavigateToAccountSettings = {
                                closeMenu()
                                navigateTo("account_settings")
                            },
                            onLogout = { logout() }
                        )
                    }
                }
            }
        }
    }
}
