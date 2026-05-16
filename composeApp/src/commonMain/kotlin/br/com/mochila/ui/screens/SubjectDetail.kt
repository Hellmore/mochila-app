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
import br.com.mochila.model.Subject
import br.com.mochila.presenter.SubjectDetailPresenter
import br.com.mochila.presenter.SubjectDetailView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.UserAvatarButton
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun SubjectDetailScreen(
    userId: Int,
    subjectId: Int,
    onNavigateToEdit: (Subject) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTaskRegister: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    var subject by remember { mutableStateOf<Subject?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val presenter = remember {
        object : SubjectDetailView {
            override fun showSubject(s: Subject) { subject = s }
            override fun showDeleteSuccess() { /* feedback opcional via snackbar futuro */ }
            override fun showDeleteError() { /* feedback opcional via snackbar futuro */ }
            override fun navigateToHome() { onNavigateToHome() }
            override fun navigateToEdit(s: Subject) { onNavigateToEdit(s) }
            override fun navigateBack() { onBack() }
        }.let { view -> SubjectDetailPresenter(view) }
    }

    LaunchedEffect(subjectId) {
        presenter.loadSubject(subjectId)
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

        subject?.let { s ->
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
                        onClick = onNavigateToAccountSettings
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Matéria",
                    color = RoxoEscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                FieldDisplay(value = s.name, label = "Nome")
                FieldDisplay(value = s.teacher, label = "Professor")
                FieldDisplay(value = s.minFrequency.toString(), label = "Frequência Mínima (%)")
                FieldDisplay(value = s.startDate, label = "Data de Início")
                FieldDisplay(value = s.endDate, label = "Data de Término")
                FieldDisplay(value = s.classHours.toString(), label = "Horas/Aula")
                FieldDisplay(value = s.semester, label = "Semestre")

                Spacer(modifier = Modifier.height(20.dp))

                // Botão Editar
                Button(
                    onClick = { presenter.onEditClicked(s) },
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
                    Text("Excluir Matéria", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Dialog de confirmação de exclusão
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
                            Text("Tem certeza que deseja excluir a matéria \"${s.name}\"?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                    presenter.onDeleteConfirmed(userId, s)
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
                        IconButton(onClick = onNavigateToTaskRegister) {
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