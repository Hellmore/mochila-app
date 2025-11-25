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

    @Composable
    fun CampoRoxo(
        valor: String,
        label: String,
        onChange: (String) -> Unit,
    ) {
        val RoxoClaro = Color(0xFF7F55CE)

        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            label = {
                Text(
                    text = label,
                    color = RoxoClaro,
                    fontSize = 14.sp
                )
            },
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
    fun CampoRoxoData(
        valor: String,
        label: String,
        onChange: (String) -> Unit
    ) {
        CampoRoxo(
            valor = valor,
            label = label,
            onChange = { entrada ->

                // Permite apagar sempre
                if (entrada.length < valor.length) {
                    onChange(entrada)
                    return@CampoRoxo
                }

                // Apenas números
                val digits = entrada.filter { it.isDigit() }.take(8)

                // Mascara DD/MM/AAAA
                val formatted = buildString {
                    for (i in digits.indices) {
                        append(digits[i])
                        if (i == 1 || i == 3) append('/')
                    }
                }

                onChange(formatted)
            }
        )
    }

    @Composable
    fun CampoRoxoStatus(
        valor: String,
        label: String = "Status",
        opcoes: List<String>,
        onChange: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        val RoxoClaro = Color(0xFF7F55CE)

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
                label = {
                    Text(
                        text = label,
                        color = RoxoClaro,
                        fontSize = 14.sp
                    )
                },
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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
                        onClick = {
                            onChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    fun dataValida(data: String): Boolean {
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(data)) return false

        val partes = data.split("/")
        val dia = partes[0].toIntOrNull() ?: return false
        val mes = partes[1].toIntOrNull() ?: return false
        val ano = partes[2].toIntOrNull() ?: return false

        if (mes !in 1..12) return false

        val diasNoMes = when (mes) {
            1,3,5,7,8,10,12 -> 31
            4,6,9,11 -> 30
            2 -> if ((ano % 4 == 0 && ano % 100 != 0) || ano % 400 == 0) 29 else 28
            else -> return false
        }

        return dia in 1..diasNoMes
    }

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

        if (dataLimite.isNotBlank() && !dataValida(dataLimite)) {
            message = "Data limite inválida."
            success = false
            return
        }

        val sucesso = if (isEditing && taskId != null) {
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

        if (sucesso) {
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
        // Fundo quadriculado
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
                text = if (isEditing) "Editar Tarefa" else "Nova Tarefa",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoRoxo(
                valor = titulo,
                label = "Título",
                onChange = { if (it.length <= 30) titulo = it }
            )

            CampoRoxo(
                valor = descricao,
                label = "Descrição",
                onChange = { if (it.length <= 50) descricao = it }
            )

            CampoRoxo(
                valor = blockers,
                label = "Blockers",
                onChange = { if (it.length <= 20) blockers = it }
            )

            CampoRoxoData(
                valor = dataLimite,
                label = "Data limite (DD/MM/AAAA)",
                onChange = { dataLimite = it }
            )

            CampoRoxoStatus(
                valor = status,
                label = "Status",
                opcoes = listOf("Pendente", "Em andamento", "Cancelada", "Concluida"),
                onChange = { status = it }
            )

            message?.let { msg ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
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

            Button(
                onClick = { salvarTarefa() },
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

            // Botão excluir
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
