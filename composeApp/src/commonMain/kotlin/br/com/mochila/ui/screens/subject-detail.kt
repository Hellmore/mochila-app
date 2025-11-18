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
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import br.com.mochila.data.*

@Composable
fun SubjectDetailScreen(
    userId: Int,
    materia: Materia,
    onNavigateToEdit: (Materia) -> Unit,
    onNavigateToAbsenceControl: (Materia) -> Unit,
    onNavigateToItemRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fundo decorativo
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(Res.drawable.pin),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.95f),
            contentScale = ContentScale.FillHeight
        )

        Image(
            painter = painterResource(Res.drawable.mochila),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.65f)
                .aspectRatio(1f),
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

            // Título
            Text(
                "Matéria",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val campos = mapOf(
//                "ID da Disciplina" to materia.id_disciplina.toString(),
                "Nome" to materia.nome,
                "Professor" to materia.professor,
                "Frequência Mínima (%)" to materia.frequencia_minima.toString(),
                "Data de Início" to materia.data_inicio,
                "Data de Término" to materia.data_fim,
                "Horas/Aula" to materia.hora_aula.toString(),
                "Semestre" to materia.semestre
            )

            campos.forEach { (label, value) ->
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "$label:",
                        color = RoxoEscuro,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = value,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoxoClaro,
                            unfocusedBorderColor = RoxoClaro,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth(0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão Editar
            Button(
                onClick = { onNavigateToEdit(materia) },
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

//            // Botão Controle de Faltas
//            Button(
//                onClick = { onNavigateToAbsenceControl(materia) },
//                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
//                shape = RoundedCornerShape(8.dp),
//                modifier = Modifier
//                    .widthIn(max = 600.dp)
//                    .fillMaxWidth(0.9f)
//                    .height(45.dp)
//            ) {
//                Text("Controle de Faltas", color = Color.Black, fontWeight = FontWeight.Bold)
//            }


            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text("Excluir Matéria", color = Color.White, fontWeight = FontWeight.Bold)
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
                        Text("Tem certeza que deseja excluir a matéria \"${materia.nome}\"?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val apagou = MateriaRepository.deletarMateria(
                                    idUsuario = userId,
                                    idDisciplina = materia.id_disciplina
                                )

                                showDeleteDialog = false

                                if (apagou) {
                                    println("Disciplina deletada com sucesso!")
                                    onNavigateToHome()
                                } else {
                                    println("Erro ao deletar disciplina.")
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

            // Menu inferior
            Row(
                modifier = Modifier
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
                    IconButton(onClick = { showMenu = true }) {
                        Image(
                            painter = painterResource(Res.drawable.menu),
                            contentDescription = "Menu lateral",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onNavigateToItemRegister) {
                        Image(
                            painter = painterResource(Res.drawable.add),
                            contentDescription = "Registrar item",
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
