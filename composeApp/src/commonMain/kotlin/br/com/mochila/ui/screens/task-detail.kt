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
import br.com.mochila.data.*
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskDetailScreen(
    userId: Int,
    taskId: Int,
    onNavigateToEdit: (Tarefa) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    @Composable
    fun CampoRoxoExibicao(
        valor: String,
        label: String
    ) {
        val RoxoClaro = Color(0xFF7F55CE)

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

    var showMenu by remember { mutableStateOf(false) }
    val tarefa = remember(taskId) { TarefaRepository.buscarPorId(taskId) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (tarefa == null) {
        Text("Erro: tarefa não encontrada.")
        return
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

        // Conteúdo principal
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

            CampoRoxoExibicao(valor = tarefa.titulo, label = "Título")
            CampoRoxoExibicao(valor = tarefa.descricao ?: "Sem descrição", label = "Descrição")
            CampoRoxoExibicao(valor = tarefa.status, label = "Status")
            CampoRoxoExibicao(valor = tarefa.blockers ?: "Nenhum", label = "Blockers")
            CampoRoxoExibicao(valor = tarefa.data_limite ?: "Não definida", label = "Data limite")

            Spacer(modifier = Modifier.height(20.dp))

            // Botão Editar
            Button(
                onClick = { onNavigateToEdit(tarefa) },
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
                        Text("Tem certeza que deseja excluir a tarefa \"${tarefa.titulo}\"?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val apagou = TarefaRepository.deletarTarefa(
                                    idUsuario = userId,
                                    idTarefa = tarefa.id_tarefa
                                )

                                showDeleteDialog = false

                                if (apagou) {
                                    println("Tarefa deletada com sucesso!")
                                    onNavigateToTasksList()
                                } else {
                                    println("Erro ao deletar tarefa.")
                                }
                            }
                        ) {
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

            if (showMenu) {
                MenuScreen(
                    userId = userId,
                    onCloseMenu = { showMenu = false },
                    onNavigateToHome = {
                        showMenu = false
                        onNavigateToHome()
                    },
                    onNavigateToTasksList = {
                        showMenu = false
                        onNavigateToTasksList()
                    },
                    onNavigateToAccountSettings = {
                        showMenu = false
                        onNavigateToAccountSettings()
                    },
                    onLogout = {
                        showMenu = false
                        onLogout()
                    }
                )
            }
        }
    }
}
