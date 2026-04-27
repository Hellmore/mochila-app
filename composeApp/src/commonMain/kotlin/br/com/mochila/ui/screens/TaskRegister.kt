package br.com.mochila.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import br.com.mochila.presenter.TaskRegisterPresenter
import br.com.mochila.presenter.TaskRegisterView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.UserAvatarButton
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskRegisterScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    isEditing: Boolean = false,
    taskId: Int? = null
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Pendente") }
    var blockers by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    val presenter = remember {
        object : TaskRegisterView {
            override fun showTask(task: Task) {
                title = task.title
                description = task.description
                status = task.status
                blockers = task.blockers ?: ""
                dueDate = task.dueDate ?: ""
            }
            override fun showValidationError(msg: String) { message = msg; success = false }
            override fun showSaveSuccess(isEditing: Boolean) {
                message = if (isEditing) "Tarefa atualizada com sucesso!" else "Tarefa cadastrada com sucesso!"
                success = true
            }
            override fun showSaveError() { message = "Erro ao salvar tarefa."; success = false }
            override fun showDeleteSuccess() { message = "Tarefa excluída com sucesso!"; success = true }
            override fun showDeleteError() { message = "Erro ao excluir tarefa."; success = false }
            override fun navigateToTasksList() { onNavigateToTasksList() }
        }.let { view -> TaskRegisterPresenter(view) }
    }

    LaunchedEffect(taskId) {
        if (isEditing && taskId != null) {
            presenter.loadTask(taskId)
        }
    }

    @Composable
    fun FieldRoxo(valor: String, label: String, onChange: (String) -> Unit) {
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            label = { Text(text = label, color = RoxoClaro, fontSize = 14.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = RoxoClaro,
                unfocusedBorderColor = RoxoClaro,
                focusedLabelColor = RoxoClaro,
                cursorColor = RoxoClaro
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp)
        )
    }

    @Composable
    fun FieldDate(valor: String, label: String, onChange: (String) -> Unit) {
        FieldRoxo(valor = valor, label = label, onChange = { input ->
            if (input.length < valor.length) { onChange(input); return@FieldRoxo }
            val digits = input.filter { it.isDigit() }.take(8)
            val formatted = buildString {
                for (i in digits.indices) {
                    append(digits[i])
                    if (i == 1 || i == 3) append('/')
                }
            }
            onChange(formatted)
        })
    }

    @Composable
    fun FieldStatus(valor: String, opcoes: List<String>, onChange: (String) -> Unit) {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Status", color = RoxoClaro, fontSize = 14.sp) },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            painterResource(Res.drawable.drop),
                            contentDescription = "Selecionar status",
                            tint = RoxoClaro
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = RoxoClaro,
                    unfocusedBorderColor = RoxoClaro,
                    focusedLabelColor = RoxoClaro,
                    cursorColor = RoxoClaro,
                    focusedTextColor = Color.Black.copy(alpha = 0.85f),
                    unfocusedTextColor = Color.Black.copy(alpha = 0.85f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                opcoes.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                fontSize = 14.sp,
                                color = if (valor == option) RoxoClaro else Color.Black.copy(alpha = 0.85f),
                                fontWeight = if (valor == option) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = { onChange(option); expanded = false }
                    )
                }
            }
        }
    }

    fun buildTask() = Task(
        id = taskId ?: 0,
        userId = userId,
        title = title,
        description = description,
        status = status,
        blockers = blockers.ifBlank { null },
        dueDate = dueDate.ifBlank { null }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = "Fundo quadriculado",
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
                UserAvatarButton(
                    size = 60.dp,
                    onClick = {}
                )
            }

            Text(
                text = if (isEditing) "Editar Tarefa" else "Nova Tarefa",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            FieldRoxo(valor = title, label = "Título") { if (it.length <= 30) title = it }
            FieldRoxo(valor = description, label = "Descrição") { if (it.length <= 50) description = it }
            FieldRoxo(valor = blockers, label = "Blockers") { if (it.length <= 20) blockers = it }
            FieldDate(valor = dueDate, label = "Data limite (DD/MM/AAAA)") { dueDate = it }
            FieldStatus(
                valor = status,
                opcoes = listOf("Pendente", "Em andamento", "Cancelada", "Concluida"),
                onChange = { status = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Feedback
            message?.let { msg ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth(0.9f)
                            .background(
                                color = if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = msg,
                            color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Botão Salvar
            Button(
                onClick = { presenter.saveTask(userId, buildTask(), isEditing) },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text(
                    if (isEditing) "Salvar alterações" else "Cadastrar Tarefa",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Botão Excluir (apenas em edição)
            if (isEditing && taskId != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { presenter.deleteTask(userId, taskId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                ) {
                    Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Menu inferior fixo
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
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}