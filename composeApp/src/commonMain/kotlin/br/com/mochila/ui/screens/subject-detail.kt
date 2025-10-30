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
import br.com.mochila.auth.AuthViewModel
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun SubjectDetailScreen(
    authViewModel: AuthViewModel, // ✨ Parâmetro adicionado
    onNavigateToEdit: () -> Unit,
    onNavigateToAbsenceControl: () -> Unit,
    onNavigateToItemRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var showMenu by remember { mutableStateOf(false) }

    val subject = mapOf(
        "Nome da Matéria" to "Engenharia de Software",
        "Professor" to "Anderson Barbosa",
        "Semestre" to "5º",
        "Frequência Mínima (%)" to "75%",
        "Data de Início" to "01/08/2025",
        "Data de Fim" to "15/12/2025",
        "Horas por Aula" to "2h"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Fundo decorativo
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

        // 📚 Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔙 Botão voltar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack = onBack)
            }

            // 👤 Cabeçalho de usuário
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
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
                Spacer(modifier = Modifier.width(15.dp))
                Text(authViewModel.currentUser.value?.email ?: "Usuário", color = Color.Gray, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🏷️ Título
            Text(
                "Matéria",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Campos da matéria
            subject.forEach { (label, value) ->
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

            // ✏️ Botão Editar
            Button(
                onClick = onNavigateToEdit,
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

            // 📅 Botão Controle de Faltas
            Button(
                onClick = onNavigateToAbsenceControl,
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text("Controle de Faltas", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(120.dp))
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

        // 🔹 Menu lateral
        if (showMenu) {
            MenuScreen(
                authViewModel = authViewModel, // ✨ Passando o ViewModel
                onCloseMenu = { showMenu = false },
                onNavigateToHome = {
                    showMenu = false
                    onNavigateToHome()
                }
            )
        }
    }
}
