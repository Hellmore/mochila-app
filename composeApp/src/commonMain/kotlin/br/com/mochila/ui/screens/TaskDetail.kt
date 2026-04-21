package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import br.com.mochila.presenter.TaskDetailPresenter
import br.com.mochila.presenter.TaskDetailView
import br.com.mochila.ui.screens.components.BackButton
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
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    var task by remember { mutableStateOf<Task?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val presenter = remember {
        object : TaskDetailView {
            override fun showTask(t: Task) { task = t }
            override fun showTaskNotFound() { /* navega de volta via navigateBack */ }
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
            label = { Text(text = label, color = RoxoClaro, fontSize = 14.sp) },
            singleLine = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = RoxoClaro,
                unfocusedBorderColor = RoxoClaro,
                focusedLabelColor = RoxoClaro,
                unfocusedLabelColor = RoxoClaro,
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
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(Res.drawable.star),
            contentDescription = "Decoração estrela",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 600.dp, y = (-150).dp)
                .size(600.dp),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(Res.drawable.chevron),
            contentDescription = "Decoração chevron",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-100).dp, y = 260.dp)
                .size(600.dp),
            contentScale = ContentScale.Fit
        )

        task?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabeçalho
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BackButton(onBack = onBack)
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(RoxoClaro),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.user),
                            contentDescription = "Usuário",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tarefa",
                    color = RoxoEscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldDisplay(value = t.title, label = "Título")
                FieldDisplay(value = t.description.ifBlank { "Sem descrição" }, label = "Descrição")
                FieldDisplay(value = t.status, label = "Status")
                FieldDisplay(value = t.blockers ?: "Nenhum", label = "Blockers")
                FieldDisplay(value = t.dueDate ?: "Não definida", label = "Data limite")

                Spacer(modifier = Modifier.height(20.dp))

                // Botão Editar
                Button(
                    onClick = { presenter.onEditClicked(t) },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                ) {
                    Text("Editar", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botão Excluir
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
                            Text(
                                "Confirmar Exclusão",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        text = {
                            Text("Tem certeza que deseja excluir a tarefa \"${t.title}\"?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                    presenter.onDeleteConfirmed(userId, t)
                                }
                            ) {
                                Text(
                                    "Excluir",
                                    color = Color(0xFFD9534F),
                                    fontWeight = FontWeight.Bold
                                )
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

                if (showMenu) {
                    MenuScreen(
                        userId = userId,
                        onCloseMenu = { showMenu = false },
                        onNavigateToHome = { showMenu = false; onNavigateToHome() },
                        onNavigateToTasksList = { showMenu = false; onNavigateToTasksList() },
                        onNavigateToAccountSettings = { showMenu = false; onNavigateToAccountSettings() },
                        onLogout = { showMenu = false; onLogout() }
                    )
                }
            }
        }
    }
}