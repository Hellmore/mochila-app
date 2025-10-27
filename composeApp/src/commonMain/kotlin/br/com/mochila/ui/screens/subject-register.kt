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
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun SubjectRegisterScreen(
    onNavigateToHome: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var showMenu by remember { mutableStateOf(false) }

    // Campos de input
    var nomeMateria by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var frequenciaMin by remember { mutableStateOf("") }
    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }
    var horasPorAula by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🟣 Fundo quadriculado
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = "Fundo quadriculado",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 📌 Pin decorativo
        Image(
            painter = painterResource(Res.drawable.pin),
            contentDescription = "Pin decorativo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.95f),
            contentScale = ContentScale.FillHeight
        )

        // 🎒 Mochila decorativa
        Image(
            painter = painterResource(Res.drawable.mochila),
            contentDescription = "Mochila decorativa",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.65f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        // 📋 Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Cabeçalho com usuário
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
                Text("Nome usuário", color = Color.Gray, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(70.dp))

            // 🔹 Título centralizado
            Text(
                "Nova Matéria",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🟪 Campos de input (largura máxima 600 dp)
            val campos = listOf(
                Pair("Nome da Matéria", nomeMateria) to { it: String -> nomeMateria = it },
                Pair("Professor", professor) to { it: String -> professor = it },
                Pair("Semestre", semestre) to { it: String -> semestre = it },
                Pair("Frequência mínima (%)", frequenciaMin) to { it: String -> frequenciaMin = it },
                Pair("Data de Início", dataInicio) to { it: String -> dataInicio = it },
                Pair("Data de Fim", dataFim) to { it: String -> dataFim = it },
                Pair("Horas por Aula", horasPorAula) to { it: String -> horasPorAula = it },
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
                        .widthIn(max = 600.dp)               // ✅ largura máxima
                        .fillMaxWidth(0.9f)                  // ✅ ocupa 90% em telas menores
                        .align(Alignment.CenterHorizontally) // ✅ centraliza
                        .padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Botão Salvar — largura máxima e centralizado
            Button(
                onClick = { onNavigateToHome() },
                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
                    .height(45.dp)
            ) {
                Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                // 📋 Menu lateral
                IconButton(onClick = { showMenu = true }) {
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu lateral",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // 🏠 Voltar para Home
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 🔹 Overlay do menu lateral
        if (showMenu) {
            MenuScreen(
                onCloseMenu = { showMenu = false },
                onNavigateToHome = {
                    showMenu = false
                    onNavigateToHome()
                }
            )
        }
    }
}
