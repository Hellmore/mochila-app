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
import br.com.mochila.data.TarefaRepository
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

    val existingTask = remember(taskId) { taskId?.let { TarefaRepository.buscarPorId(it) } }

    var titulo by remember { mutableStateOf(existingTask?.titulo ?: "") }
    var descricao by remember { mutableStateOf(existingTask?.descricao ?: "") }
    var status by remember { mutableStateOf(existingTask?.status ?: "Pendente") }
    var blockers by remember { mutableStateOf(existingTask?.blockers ?: "") }
    var dataLimite by remember { mutableStateOf(existingTask?.data_limite ?: "") }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    fun salvarTarefa() {
        if (titulo.isBlank() || descricao.isBlank()) {
            message = "Título e descrição são obrigatórios."
            success = false
            return
        }

        val operacaoBemSucedida = if (isEditing && taskId != null) {
            TarefaRepository.atualizarTarefa(
                idTarefa = taskId,
                idUsuario = userId,
                titulo = titulo,
                descricao = descricao,
                status = status,
                blockers = blockers,
                dataLimite = dataLimite
            )
        } else {
            TarefaRepository.insertTarefa(
                idUsuario = userId,
                titulo = titulo,
                descricao = descricao,
                status = status,
                blockers = blockers,
                dataLimite = dataLimite
            )
        }

        if (operacaoBemSucedida) {
            message = if (isEditing) "Tarefa atualizada com sucesso!" else "Tarefa cadastrada com sucesso!"
            success = true
            onNavigateToTasksList()
        } else {
            message = "Erro ao salvar tarefa."
            success = false
        }
    }

    fun excluirTarefa() {
        if (!isEditing || taskId == null) return

        val operacaoBemSucedida = TarefaRepository.deletarTarefa(taskId, userId)
        if (operacaoBemSucedida) {
            message = "Tarefa excluída com sucesso!"
            success = true
            onNavigateToTasksList()
        } else {
            message = "Erro ao excluir tarefa."
            success = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Fundo decorativo
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = "Fundo quadriculado",
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

            Text(
                if (isEditing) "Editar Tarefa" else "Nova Tarefa",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val campos = listOf(
                Pair("Título", titulo) to { it: String -> titulo = it },
                Pair("Descrição", descricao) to { it: String -> descricao = it },
                Pair("Blockers", blockers) to { it: String -> blockers = it },
            )

            campos.forEach { (campo, setValue) ->
                val (placeholder, valor) = campo
                OutlinedTextField(
                    value = valor,
                    onValueChange = setValue,
                    placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = RoxoClaro,
                        unfocusedBorderColor = RoxoClaro
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 6.dp)
                )
            }

            var prevLength by remember { mutableStateOf(0) }

            OutlinedTextField(
                value = dataLimite,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(8)

                    val formatted = buildString {
                        for (i in digits.indices) {
                            append(digits[i])
                            if (i == 1 || i == 3) append('/')
                        }
                    }

                    dataLimite = formatted
                    prevLength = digits.length
                },
                placeholder = { Text("Data Limite (DD/MM/AAAA)", color = Color.Gray, fontSize = 14.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = RoxoClaro,
                    unfocusedBorderColor = RoxoClaro
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 6.dp)
            )

            var expandedStatus by remember { mutableStateOf(false) }
            val statusOptions = listOf("Pendente", "Em andamento", "Cancelada", "Concluida")

            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            "Status",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { expandedStatus = true }) {
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
                        focusedTextColor = Color.Black.copy(alpha = 0.85f),
                        unfocusedTextColor = Color.Black.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontSize = 14.sp,
                                    color = if (status == option) RoxoEscuro else Color.Black.copy(alpha = 0.85f),
                                    fontWeight = if (status == option) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                status = option
                                expandedStatus = false
                            }
                        )
                    }
                }
            }

            message?.let {
                Text(
                    it,
                    color = if (success) Color(0xFF00C853) else Color.Red,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 💾 Botão salvar
            Button(
                onClick = { salvarTarefa() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
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

            // 🗑️ Botão excluir (somente edição)
            if (isEditing) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { excluirTarefa() },
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

        // 🔹 Menu inferior fixo
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
