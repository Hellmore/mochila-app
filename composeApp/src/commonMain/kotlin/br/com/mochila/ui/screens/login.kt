package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.painterResource
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.fundo_quadriculado
import mochila_app.composeapp.generated.resources.fundo_curvas
import mochila_app.composeapp.generated.resources.logo

@Composable
fun LoginScreen() {
    // 🎨 Paleta de cores
    val RoxoEscuro = Color(0xFF5336CB)
    val VerdeLima = Color(0xFFC5E300)
    val RoxoClaro = Color(0xFF7F55CE)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Fundo quadriculado
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .zIndex(0f),
            contentScale = ContentScale.Crop
        )

        // 🔹 Curvas coloridas no canto inferior
        Image(
            painter = painterResource(Res.drawable.fundo_curvas),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            contentScale = ContentScale.Crop
        )

        // 🔹 Conteúdo principal
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .zIndex(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Logo circular
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(RoxoEscuro, shape = RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo Mochila Hub",
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Campo de nome de usuário
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nome de usuário", fontFamily = FontFamily.SansSerif) },
                placeholder = { Text("Insira o seu usuário", fontFamily = FontFamily.SansSerif) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Campo de senha
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha", fontFamily = FontFamily.SansSerif) },
                placeholder = { Text("Insira a sua senha", fontFamily = FontFamily.SansSerif) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Botão Esqueci a senha
            Button(
                onClick = { /* TODO: Esqueci a senha */ },
                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Esqueci a senha", color = Color.White, fontFamily = FontFamily.SansSerif)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Botão Cadastre-se
            Button(
                onClick = { /* TODO: Cadastrar */ },
                colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastre-se", color = Color.White, fontFamily = FontFamily.SansSerif)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Botão Login
            Button(
                onClick = { /* TODO: Login */ },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Login",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp
                )
            }
        }
    }
}
