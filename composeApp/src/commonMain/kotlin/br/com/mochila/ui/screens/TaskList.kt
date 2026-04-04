package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.model.Task
import br.com.mochila.presenter.TaskListPresenter
import br.com.mochila.presenter.TaskListView
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskListScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val presenter = remember {
        object : TaskListView {
            override fun showTasks(list: List<Task>) { tasks = list }
            override fun showEmptyState() { tasks = emptyList() }
            override fun navigateToTaskDetail(taskId: Int) { onNavigateToTaskDetail(taskId) }
        }.let { view -> TaskListPresenter(view) }
    }

    LaunchedEffect(userId) {
        presenter.loadTasks(userId)
    }

    val filteredTasks = remember(tasks, searchQuery) {
        presenter.filterTasks(tasks, searchQuery)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(Res.drawable.pin),
            contentDescription = "Pin decorativo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.95f),
            contentScale = ContentScale.FillHeight
        )
        Image(
            painter = painterResource(Res.drawable.mochila),
            contentDescription = "Mochila decorativa",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.65f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Cabeçalho
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Tarefas",
                    color = RoxoEscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(RoxoClaro)
                        .clickable { onNavigateToAccountSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuário",
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Botão de filtro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(RoxoClaro)
                        .clickable {
                            isSearchExpanded = !isSearchExpanded
                            if (!isSearchExpanded) searchQuery = ""
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Filtro", color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.drop),
                            contentDescription = "Abrir filtro",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Campo de busca colapsável
            if (isSearchExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Buscar tarefa por título") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista
            when {
                tasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma tarefa cadastrada.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                filteredTasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma tarefa encontrada para esse filtro.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(filteredTasks) { task ->
                            TaskItem(task) { taskId ->
                                presenter.onTaskClicked(taskId)
                            }
                        }
                    }
                }
            }
        }

        // Botão voltar
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Image(
                    painter = painterResource(Res.drawable.esquerda),
                    contentDescription = "Voltar",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Menu inferior
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = RoxoEscuro.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenMenu) {
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu lateral",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onNavigateToAdd) {
                    Image(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Adicionar",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Início",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onClick: (Int) -> Unit) {
    val RoxoEscuro = Color(0xFF5336CB)
    val VerdeClaro = Color(0xFFE6F5B0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(task.id) }
            .background(VerdeClaro, RoundedCornerShape(16.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = task.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = RoxoEscuro
        )
        if (!task.description.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = task.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}