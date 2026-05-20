@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserSession
import br.com.mochila.model.Task
import br.com.mochila.presenter.TaskListPresenter
import br.com.mochila.presenter.TaskListView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val fundoTela = Color(0xFFF8F8F8)
private val laranjaHeader = Color(0xFFFFBA5E)
private val rosa = Color(0xFFFF6694)
private val verdeConclusao = Color(0xFF6B9A78)
private val vermelhoCancelamento = Color(0xFFC47A7A)
private val taskCellHeight = 110.dp
private val taskCardPadding = 10.dp
private val taskActionHeight = 28.dp

private val taskStatusFilterOptions = listOf(
    "Todos",
    "Pendente",
    "Em andamento",
    "Cancelada",
    "Concluida",
)

private val monthNamesPt = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private fun formatDatePt(date: LocalDate): String {
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    val mon = monthNamesPt[date.monthNumber - 1]
    val yy = (date.year % 100).toString().padStart(2, '0')
    return "$dd $mon $yy"
}

@Composable
private fun CalendarGlyph(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.menu_icon_today),
        contentDescription = "Calendário",
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint),
    )
}

@Composable
private fun TaskSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Buscar tarefa",
                color = Color.Gray,
                fontSize = 12.sp,
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = rosa,
            unfocusedBorderColor = rosa.copy(alpha = 0.6f),
            focusedTextColor = Color(0xFF333333),
            unfocusedTextColor = Color(0xFF333333),
            cursorColor = rosa,
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 13.sp,
            color = Color(0xFF333333),
        ),
        shape = RoundedCornerShape(7.dp),
    )
}

@Composable
private fun TaskStatusFilterDropdown(
    value: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = "Status",
            color = laranjaHeader,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.White)
                    .border(1.dp, rosa, RoundedCornerShape(7.dp))
                    .clickable { expanded = true }
                    .padding(start = 10.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    color = rosa,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(rosa)
                        .clickable { expanded = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.drop),
                        contentDescription = "Abrir filtro de status",
                        modifier = Modifier.size(14.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                taskStatusFilterOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = if (option == value) rosa else Color(0xFF333333),
                                fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = { onSelect(option); expanded = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListBottomBar(
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .background(rosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onOpenMenu) {
                Image(
                    painter = painterResource(Res.drawable.menu),
                    contentDescription = "Menu lateral",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            IconButton(onClick = onNavigateToAdd) {
                Image(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = "Adicionar",
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
fun TaskListScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onOpenMenu: () -> Unit,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("Todos") }

    val presenter = remember {
        object : TaskListView {
            override fun showTasks(list: List<Task>) {
                tasks = list
            }

            override fun showEmptyState() {
                tasks = emptyList()
            }

            override fun navigateToTaskDetail(taskId: Int) {
                onNavigateToTaskDetail(taskId)
            }
        }.let { TaskListPresenter(it) }
    }

    val filteredTasks = remember(tasks, searchQuery, statusFilter) {
        presenter.filterTasks(tasks, searchQuery, statusFilter)
    }

    LaunchedEffect(userId) {
        presenter.loadTasks(userId)
    }

    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val dateLabel = remember(today) { formatDatePt(today) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            TaskListDesktopLayout(
                tasks = tasks,
                filteredTasks = filteredTasks,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                statusFilter = statusFilter,
                onStatusFilterChange = { statusFilter = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onTaskClick = { id -> presenter.onTaskClicked(id) },
                onCompleteTask = { task -> presenter.completeTask(userId, task) },
                onCancelTask = { task -> presenter.cancelTask(userId, task) },
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.50f,
            )
            TaskListMobileLayout(
                tasks = tasks,
                filteredTasks = filteredTasks,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                statusFilter = statusFilter,
                onStatusFilterChange = { statusFilter = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onTaskClick = { id -> presenter.onTaskClicked(id) },
                onCompleteTask = { task -> presenter.completeTask(userId, task) },
                onCancelTask = { task -> presenter.cancelTask(userId, task) },
            )
        }
    }
}

@Composable
private fun TaskListMobileLayout(
    tasks: List<Task>,
    filteredTasks: List<Task>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    statusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onCancelTask: (Task) -> Unit,
) {
    val hasActiveFilters = searchQuery.isNotBlank() || statusFilter != "Todos"
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Column(Modifier.fillMaxSize()) {
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
                        name = name,
                        photoPath = user?.photoPath,
                        size = 40.dp,
                        accentColor = Color.White,
                        onClick = onNavigateToAccountSettings,
                    )
                    Text(
                        text = name.ifBlank { " " },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 120.dp),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = dateLabel,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                    )
                    CalendarGlyph(
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            TaskSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
            )
            TaskStatusFilterDropdown(
                value = statusFilter,
                onSelect = onStatusFilterChange,
                modifier = Modifier.width(140.dp),
            )
        }

        TaskGrid(
            tasks = filteredTasks,
            allTasksEmpty = tasks.isEmpty(),
            noFilterResults = tasks.isNotEmpty() && filteredTasks.isEmpty() && hasActiveFilters,
            columns = 2,
            onNavigateToAdd = onNavigateToAdd,
            onTaskClick = onTaskClick,
            onCompleteTask = onCompleteTask,
            onCancelTask = onCancelTask,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .padding(bottom = 16.dp),
        )

        TaskListBottomBar(
            onOpenMenu = onOpenMenu,
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToHome = onNavigateToHome,
        )
    }
}

@Composable
private fun TaskListDesktopLayout(
    tasks: List<Task>,
    filteredTasks: List<Task>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    statusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onCancelTask: (Task) -> Unit,
) {
    val hasActiveFilters = searchQuery.isNotBlank() || statusFilter != "Todos"
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(rosa),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "Logo Mochila Hub",
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("Mochila Hub", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text("Organize sua vida acadêmica", color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileAvatar(
                    name = name,
                    photoPath = user?.photoPath,
                    size = 48.dp,
                    accentColor = Color.White,
                    onClick = onNavigateToAccountSettings,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = name.ifBlank { " " },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .background(fundoTela),
        ) {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.50f,
            )
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(
                    onBack = onBack,
                    backgroundColor = rosa.copy(alpha = 0.92f),
                    iconTint = Color.White,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = dateLabel,
                    color = Color(0xFF333333),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.width(8.dp))
                CalendarGlyph(
                    tint = rosa,
                    modifier = Modifier.size(22.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                TaskSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                )
                TaskStatusFilterDropdown(
                    value = statusFilter,
                    onSelect = onStatusFilterChange,
                    modifier = Modifier.width(180.dp),
                )
            }

            TaskGrid(
                tasks = filteredTasks,
                allTasksEmpty = tasks.isEmpty(),
                noFilterResults = tasks.isNotEmpty() && filteredTasks.isEmpty() && hasActiveFilters,
                columns = 3,
                onNavigateToAdd = onNavigateToAdd,
                onTaskClick = onTaskClick,
                onCompleteTask = onCompleteTask,
                onCancelTask = onCancelTask,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(bottom = 8.dp),
            )

            TaskListBottomBar(
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToHome = onNavigateToHome,
            )
        }
        }
    }
}

@Composable
private fun TaskGrid(
    tasks: List<Task>,
    allTasksEmpty: Boolean,
    noFilterResults: Boolean,
    columns: Int,
    onNavigateToAdd: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onCancelTask: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (allTasksEmpty) {
        val gridSpacing = 16.dp
        BoxWithConstraints(modifier = modifier.verticalScroll(rememberScrollState())) {
            val cellWidth =
                ((maxWidth - gridSpacing * (columns - 1)) / columns).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(24.dp))
                AddTaskCell(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .width(cellWidth)
                        .height(taskCellHeight),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Nenhuma tarefa cadastrada",
                    color = Color.Gray,
                    fontSize = 16.sp,
                )
            }
        }
        return
    }

    if (noFilterResults) {
        BoxWithConstraints(modifier = modifier.verticalScroll(rememberScrollState())) {
            val gridSpacing = 16.dp
            val cellWidth =
                ((maxWidth - gridSpacing * (columns - 1)) / columns).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AddTaskCell(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .width(cellWidth)
                        .height(taskCellHeight),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Nenhuma tarefa encontrada",
                    color = Color.Gray,
                    fontSize = 16.sp,
                )
            }
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AddTaskCell(
                onClick = onNavigateToAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(taskCellHeight),
            )
        }
        itemsIndexed(tasks, key = { _, t -> t.id }) { _, task ->
            TaskCard(
                task = task,
                onClick = { onTaskClick(task.id) },
                onComplete = { onCompleteTask(task) },
                onCancel = { onCancelTask(task) },
            )
        }
    }
}

@Composable
private fun AddTaskCell(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .border(2.dp, rosa, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = "Adicionar tarefa",
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(rosa),
        )
    }
}

private fun taskStatusShowsActions(status: String) =
    status == "Pendente" || status == "Em andamento"

private fun taskStatusDisplayLabel(status: String) = when (status) {
    "Concluida" -> "Concluída"
    else -> status
}

@Composable
private fun TaskActionChip(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    iconTint: Color,
    contentDescription: String,
    label: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .height(taskActionHeight)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (label != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(12.dp),
                    colorFilter = ColorFilter.tint(iconTint),
                )
                Text(
                    text = label,
                    color = iconTint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Image(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(iconTint),
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
) {
    val showsActions = taskStatusShowsActions(task.status)
    val showsStatusInBody = showsActions && task.status.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(taskCellHeight)
            .clip(RoundedCornerShape(9.dp))
            .background(laranjaHeader)
            .padding(horizontal = 8.dp, vertical = taskCardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = task.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
            )
            if (showsStatusInBody) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = task.status,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp,
                )
            }
        }
        if (showsActions) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TaskActionChip(
                    modifier = Modifier.weight(1f),
                    icon = Res.drawable.task_icon_check,
                    iconTint = verdeConclusao,
                    contentDescription = "Concluir tarefa",
                    onClick = onComplete,
                )
                TaskActionChip(
                    modifier = Modifier.weight(1f),
                    icon = Res.drawable.menu_close,
                    iconTint = vermelhoCancelamento,
                    contentDescription = "Cancelar tarefa",
                    onClick = onCancel,
                )
            }
        } else {
            val (icon, tint) = when (task.status) {
                "Concluida" -> Res.drawable.task_icon_check to verdeConclusao
                "Cancelada" -> Res.drawable.menu_close to vermelhoCancelamento
                else -> Res.drawable.task_icon_check to Color(0xFF888888)
            }
            TaskActionChip(
                modifier = Modifier.fillMaxWidth(),
                icon = icon,
                iconTint = tint,
                contentDescription = task.status,
                label = taskStatusDisplayLabel(task.status),
            )
        }
    }
}
