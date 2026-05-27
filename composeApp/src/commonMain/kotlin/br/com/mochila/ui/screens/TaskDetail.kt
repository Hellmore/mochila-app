package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.TaskRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import br.com.mochila.model.TaskPriority
import br.com.mochila.presenter.TaskDetailPresenter
import br.com.mochila.presenter.TaskDetailView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Tela de visualizacao e edicao de uma tarefa
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
    // Paleta e estado da tela
    val fundoTela = Color(0xFFF8F8F8)
    val laranjaHeader = Color(0xFFFFBA5E)
    val rosa = Color(0xFFFF6694)

    var task by remember { mutableStateOf<Task?>(null) }
    var blockerTitle by remember { mutableStateOf<String?>(null) }
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

    LaunchedEffect(task?.blockers) {
        val blockerId = task?.blockers?.toIntOrNull()
        blockerTitle = if (blockerId != null) {
            withContext(Dispatchers.IO) { TaskRepository.findById(blockerId) }?.title
        } else null
    }

    val monthNamesPt = remember {
        listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
        )
    }

    fun formatDatePt(date: LocalDate): String {
        val dd = date.dayOfMonth.toString().padStart(2, '0')
        val mon = monthNamesPt[date.monthNumber - 1]
        val yy = (date.year % 100).toString().padStart(2, '0')
        return "$dd $mon $yy"
    }

    val user = UserSession.currentUser
    val displayName = user?.name.orEmpty()
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val dateLabel = remember(today) { formatDatePt(today) }

    @Composable
    fun FieldDisplay(
        label: String,
        value: String,
        singleLine: Boolean,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label, color = rosa, fontSize = 12.sp) },
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = rosa,
                unfocusedBorderColor = rosa,
                focusedLabelColor = rosa,
                unfocusedLabelColor = rosa,
                focusedTextColor = rosa,
                unfocusedTextColor = rosa,
                cursorColor = Color.Transparent,
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraLight,
                color = rosa,
            ),
            shape = RoundedCornerShape(7.dp),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    @Composable
    fun OrangeHeaderBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(laranjaHeader)
                .padding(vertical = 20.dp, horizontal = 22.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BackButton(
                        onBack = onBack,
                        backgroundColor = Color.Transparent,
                        iconTint = Color.White,
                        buttonSize = 40.dp,
                        iconSize = 22.dp,
                    )
                    ProfileAvatar(
                        name = displayName,
                        photoPath = user?.photoPath,
                        size = 40.dp,
                        accentColor = Color.White,
                        onClick = onNavigateToAccountSettings,
                    )
                    Text(
                        text = displayName.ifBlank { " " },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 120.dp),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(text = dateLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Image(
                        painter = painterResource(Res.drawable.menu_icon_today),
                        contentDescription = "Calendário",
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
        }
    }

    @Composable
    fun BottomBar() {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .background(rosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { showMenu = true }) {
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
                IconButton(onClick = onNavigateToTasksList) {
                    Image(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Lista de tarefas",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Início",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
        }
    }

    @Composable
    fun ContentBody(modifier: Modifier = Modifier, fieldsMaxWidth: Dp? = null) {
        val widthCap = fieldsMaxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            task?.let { t ->
                Column(
                    modifier = widthCap
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 36.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = "Detalhes da Tarefa",
                        color = laranjaHeader,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(20.dp))

                    FieldDisplay(label = "Título", value = t.title, singleLine = true)
                    Spacer(Modifier.height(14.dp))
                    FieldDisplay(label = "Descrição", value = t.description.ifBlank { "Sem descrição" }, singleLine = false)
                    Spacer(Modifier.height(14.dp))
                    FieldDisplay(label = "Status", value = t.status, singleLine = true)
                    Spacer(Modifier.height(14.dp))
                    FieldDisplay(label = "Blocker", value = blockerTitle ?: "Nenhuma", singleLine = true)
                    Spacer(Modifier.height(14.dp))
                    FieldDisplay(label = "Data limite", value = t.dueDate ?: "Não definida", singleLine = true)

                    Spacer(Modifier.height(14.dp))

                    TaskPrioritySelector(
                        userId = userId,
                        task = t,
                        accentColor = rosa,
                        onPriorityChanged = { updated -> task = updated },
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { presenter.onEditClicked(t) },
                        colors = ButtonDefaults.buttonColors(containerColor = rosa, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                    ) {
                        Text("Editar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                    ) {
                        Text("Excluir", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        task?.let { t ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir tarefa", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = { Text("Tem certeza que deseja excluir \"${t.title}\"?", fontSize = 14.sp) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        presenter.onDeleteConfirmed(userId, t)
                    }) {
                        Text("Excluir", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = rosa)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(fundoTela),
    ) {
        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.50f,
        )

        // Layout responsivo desktop ou mobile
        val wide = maxWidth >= 700.dp
        if (wide) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BackButton(onBack = onBack, backgroundColor = rosa.copy(alpha = 0.92f), iconTint = Color.White)
                    Spacer(Modifier.weight(1f))
                    Text(text = dateLabel, color = Color(0xFF333333), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(Res.drawable.menu_icon_today),
                        contentDescription = "Calendário",
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(rosa),
                    )
                }
                ContentBody(modifier = Modifier.weight(1f), fieldsMaxWidth = 600.dp)
                BottomBar()
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                OrangeHeaderBar()
                ContentBody(modifier = Modifier.weight(1f))
                BottomBar()
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

// Dropdown para alterar prioridade da tarefa
@Composable
private fun TaskPrioritySelector(
    userId: Int,
    task: Task,
    accentColor: Color,
    onPriorityChanged: (Task) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val priority = task.priority

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
                            val updated = task.copy(priority = option)
                            if (TaskRepository.update(userId, updated)) {
                                onPriorityChanged(updated)
                            }
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
