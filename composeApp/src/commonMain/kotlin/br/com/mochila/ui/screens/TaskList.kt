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
import org.jetbrains.compose.resources.painterResource

private val fundoTela = Color(0xFFF8F8F8)
private val laranjaHeader = Color(0xFFFFBA5E)
private val rosa = Color(0xFFFF6694)

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

    val filteredTasks = remember(tasks, searchQuery) {
        if (searchQuery.isBlank()) tasks
        else tasks.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

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
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onTaskClick = { id -> presenter.onTaskClicked(id) },
            )
        } else {
            TaskListMobileLayout(
                tasks = tasks,
                filteredTasks = filteredTasks,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onTaskClick = { id -> presenter.onTaskClicked(id) },
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
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onTaskClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Column(Modifier.fillMaxSize()) {
        val headerEdgeInset = 22.dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
                .background(laranjaHeader)
                .padding(vertical = 12.dp),
        ) {
            BackButton(
                onBack = onBack,
                backgroundColor = rosa.copy(alpha = 0.92f),
                iconTint = Color.White,
                buttonSize = 34.dp,
                iconSize = 18.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = headerEdgeInset),
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                ProfileAvatar(
                    name = name,
                    photoPath = user?.photoPath,
                    size = 40.dp,
                    accentColor = Color.White,
                    onClick = onNavigateToAccountSettings,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = name.ifBlank { " " },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 160.dp),
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = headerEdgeInset),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = dateLabel,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.End,
                )
                Spacer(Modifier.width(6.dp))
                CalendarGlyph(
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        TaskSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 8.dp),
        )

        TaskGrid(
            tasks = filteredTasks,
            allTasksEmpty = tasks.isEmpty(),
            noSearchResults = tasks.isNotEmpty() && filteredTasks.isEmpty() && searchQuery.isNotBlank(),
            columns = 2,
            onNavigateToAdd = onNavigateToAdd,
            onTaskClick = onTaskClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
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
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onTaskClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
        Box(
            modifier = Modifier
                .weight(0.22f)
                .fillMaxHeight()
                .background(rosa),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 20.dp, start = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
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
        }

        Column(
            modifier = Modifier
                .weight(0.78f)
                .fillMaxHeight()
                .background(fundoTela),
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

            TaskSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 8.dp),
            )

            TaskGrid(
                tasks = filteredTasks,
                allTasksEmpty = tasks.isEmpty(),
                noSearchResults = tasks.isNotEmpty() && filteredTasks.isEmpty() && searchQuery.isNotBlank(),
                columns = 3,
                onNavigateToAdd = onNavigateToAdd,
                onTaskClick = onTaskClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
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

@Composable
private fun TaskGrid(
    tasks: List<Task>,
    allTasksEmpty: Boolean,
    noSearchResults: Boolean,
    columns: Int,
    onNavigateToAdd: () -> Unit,
    onTaskClick: (Int) -> Unit,
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
                        .heightIn(min = 87.dp),
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

    if (noSearchResults) {
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
                        .heightIn(min = 87.dp),
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
                    .heightIn(min = 87.dp),
            )
        }
        itemsIndexed(tasks, key = { _, t -> t.id }) { _, task ->
            TaskCard(
                task = task,
                onClick = { onTaskClick(task.id) },
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

@Composable
private fun TaskCard(
    task: Task,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 87.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(laranjaHeader)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
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
        if (task.status.isNotBlank()) {
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
}
