package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.TaskPriorityCache
import br.com.mochila.model.Task
import br.com.mochila.model.TaskPriority
import br.com.mochila.presenter.TaskDetailPresenter
import br.com.mochila.presenter.TaskDetailView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.UserAvatarButton
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskDetailScreen(
    userId: Int,
    taskId: Int,
    onNavigateToEdit: (Task) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val rosa = Color(0xFFFF6694)
    val fundoTela = Color(0xFFF8F8F8)

    var task by remember { mutableStateOf<Task?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val presenter = remember {
        object : TaskDetailView {
            override fun showTask(t: Task) { task = t }
            override fun showTaskNotFound() {}
            override fun showDeleteSuccess() {}
            override fun showDeleteError() {}
            override fun navigateToTasksList() { onNavigateToTasksList() }
            override fun navigateToEdit(t: Task) { onNavigateToEdit(t) }
            override fun navigateBack() { onBack() }
        }.let { view -> TaskDetailPresenter(view) }
    }

    LaunchedEffect(taskId) {
        presenter.loadTask(taskId)
    }

    @Composable
    fun FieldDisplay(value: String, label: String) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label, color = rosa, fontSize = 14.sp) },
            singleLine = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = rosa,
                unfocusedBorderColor = rosa,
                focusedLabelColor = rosa,
                unfocusedLabelColor = rosa,
                focusedTextColor = Color.Black.copy(alpha = 0.85f),
                unfocusedTextColor = Color.Black.copy(alpha = 0.85f),
                cursorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela)
    ) {
        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.50f,
        )
        task?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BackButton(onBack = onBack, backgroundColor = rosa, iconTint = Color.White)
                    UserAvatarButton(size = 60.dp, onClick = onNavigateToAccountSettings)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tarefa",
                    color = rosa,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldDisplay(value = t.title, label = "Título")
                FieldDisplay(value = t.description.ifBlank { "Sem descrição" }, label = "Descrição")
                FieldDisplay(value = t.status, label = "Status")
                FieldDisplay(value = t.blockers ?: "Nenhum", label = "Blockers")
                FieldDisplay(value = t.dueDate ?: "Não definida", label = "Data limite")

                Spacer(modifier = Modifier.height(8.dp))

                TaskPrioritySelector(
                    taskId = t.id,
                    accentColor = rosa,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { presenter.onEditClicked(t) },
                    colors = ButtonDefaults.buttonColors(containerColor = rosa),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                ) {
                    Text("Editar", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                ) {
                    Text("Excluir Tarefa", color = Color.White, fontWeight = FontWeight.Bold)
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = {
                            Text("Confirmar Exclusão", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        },
                        text = {
                            Text("Tem certeza que deseja excluir a tarefa \"${t.title}\"?")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false
                                presenter.onDeleteConfirmed(userId, t)
                            }) {
                                Text("Excluir", color = Color(0xFFD9534F), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        containerColor = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .background(rosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showMenu = true }) {
                            Image(
                                painter = painterResource(Res.drawable.menu),
                                contentDescription = "Menu lateral",
                                modifier = Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        IconButton(onClick = onNavigateToTasksList) {
                            Image(
                                painter = painterResource(Res.drawable.add),
                                contentDescription = "Lista de tarefas",
                                modifier = Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        IconButton(onClick = onNavigateToHome) {
                            Image(
                                painter = painterResource(Res.drawable.home),
                                contentDescription = "Home",
                                modifier = Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMenu) {
        MenuScreen(
            onCloseMenu = { showMenu = false },
            onNavigateToHome = { showMenu = false; onNavigateToHome() },
            onNavigateToTasksList = { showMenu = false; onNavigateToTasksList() },
            onNavigateToAccountSettings = { showMenu = false; onNavigateToAccountSettings() },
            onLogout = { showMenu = false; onLogout() }
        )
    }
}

@Composable
private fun TaskPrioritySelector(
    taskId: Int,
    accentColor: Color,
) {
    var expanded by remember { mutableStateOf(false) }
    val priorityRevision = TaskPriorityCache.revision
    val priority = remember(taskId, priorityRevision) { TaskPriorityCache.get(taskId) }

    Column(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth(0.9f),
    ) {
        Text(
            text = "Prioridade",
            color = accentColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, accentColor, RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = priority.label,
                    color = Color.Black.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Image(
                    painter = painterResource(Res.drawable.drop),
                    contentDescription = "Selecionar prioridade",
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(accentColor),
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                TaskPriority.options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                color = if (option == priority) accentColor else Color(0xFF333333),
                                fontWeight = if (option == priority) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            TaskPriorityCache.set(taskId, option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
