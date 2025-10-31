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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import mochila_app.composeapp.generated.resources.*
import br.com.mochila.data.DatabaseHelper
import java.sql.PreparedStatement

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    fun register() {
        val conn = DatabaseHelper.connect()
        if (conn != null) {
            try {
                val stmt: PreparedStatement = conn.prepareStatement(
                    "INSERT INTO usuario (nome, email) VALUES (?, ?)"
                )
                stmt.setString(1, username) // temporário até o campo senha ser adicionado no banco
                stmt.setString(2, email)
                stmt.executeUpdate()
                stmt.close()

                message = "Usuário cadastrado com sucesso!"
                println("✅ Usuário cadastrado: $email")
            } catch (e: Exception) {
                e.printStackTrace()
                message = "Erro ao cadastrar usuário. Verifique se o e-mail já existe."
            } finally {
                DatabaseHelper.close()
            }
        } else {
            message = "Erro ao conectar ao banco."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Fundo quadriculado
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 Fundo com curvas coloridas
        Image(
            painter = painterResource(Res.drawable.fundo_curvas),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 Conteúdo principal
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔙 Botão Voltar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack = onBackToLogin)
            }

            // 🔸 Logo circular
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

            // 🔸 Campo de e-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Insira o seu e-mail") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔸 Campo de usuário
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Insira o seu usuário") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔸 Campo de senha (oculta)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Crie uma senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Mensagem de feedback
            message?.let {
                Text(it, color = if (it.contains("sucesso")) Color.Green else Color.Red, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 🔸 Botão Registrar
            Button(
                onClick = { register() },
                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text("Registrar", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔸 Botão Voltar ao Login (texto simples)
            TextButton(onClick = onBackToLogin) {
                Text("Já tem conta? Faça o Login", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
