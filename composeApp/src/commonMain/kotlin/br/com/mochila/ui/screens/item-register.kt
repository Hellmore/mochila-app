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

@Composable
fun ItemRegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSubjectRegister: () -> Unit
) {
    val RoxoClaro = Color(0xFF7F55CE)
    val RoxoEscuro = Color(0xFF5336CB)

    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🟣 Fundo notebook
        Image(
            painter = painterResource(Res.drawable.notebook),
            contentDescription = "Fundo caderno",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🌟 Estrela verde — decorativo, no fundo (não interfere no layout)
        Image(
            painter = painterResource(Res.drawable.star),
            contentDescription = "Decoração estrela",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = (600).dp, y = -150.dp)
                .size(600.dp),
            contentScale = ContentScale.Fit
        )

        // ⬅️ Chevron — decorativo, canto esquerdo inferior
        Image(
            painter = painterResource(Res.drawable.chevron),
            contentDescription = "Decoração chevron",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-100).dp, y = 260.dp)
                .size(600.dp),
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
            // 🔹 Cabeçalho: usuário + sino
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(28.dp)) // reserva espaço à esquerda (chevron)

                // 👤 Usuário
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(RoxoEscuro),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.user),
                            contentDescription = "Usuário",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Nome usuário",
                        color = Color.Gray,
                        fontSize = 22.sp
                    )
                }

                // 🔔 Sino clicável com círculo VerdeLima
                IconButton(
                    onClick = { /* TODO: abrir tela de notificações futuramente */ },
                    modifier = Modifier
                        .size(70.dp)
                        .padding(end = 35.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC5E300)), // ✅ VerdeLima
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.bell),
                            contentDescription = "Notificações",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

        // 🔹 Título “Registros”
            Text(
                text = "Registros",
                color = RoxoEscuro,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally) // ✅ centraliza horizontalmente
            )


            Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Botões principais
            val botoes = listOf(
                "Nova Matéria" to onNavigateToSubjectRegister,
                "Novo Evento" to { /* TODO */ },
                "Nova Tarefa" to { /* TODO */ },
                "Nova Falta" to { /* TODO */ }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                botoes.forEach { (titulo, acao) ->
                    Button(
                        onClick = acao,
                        colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .widthIn(max = 800.dp)
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp)
                            .height(50.dp)
                    ) {
                        Text(titulo, color = Color.White, fontSize = 15.sp)
                    }
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
                        color = RoxoClaro.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 📋 Menu lateral (abre sobre a tela atual)
                IconButton(onClick = { showMenu = true }) {
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu lateral",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // 🏠 Voltar para home
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
