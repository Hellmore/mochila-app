package br.com.mochila.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import br.com.mochila.data.DatabaseHelper
import java.sql.PreparedStatement
import java.sql.ResultSet

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val VerdeLima = Color(0xFFC5E300)
    val RoxoClaro = Color(0xFF7F55CE)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun login() {
        val conn = DatabaseHelper.connect()
        if (conn != null) {
            try {
                val stmt: PreparedStatement = conn.prepareStatement(
                    "SELECT * FROM usuario WHERE email = ? AND nome = ?"
                )
                stmt.setString(1, email)
                stmt.setString(2, password) // ⚠️ temporário — será substituído pelo campo de senha real
                val rs: ResultSet = stmt.executeQuery()

                if (rs.next()) {
                    println("✅ Login bem-sucedido para $email")
                    onNavigateToHome()
                } else {
                    errorMessage = "Usuário ou senha inválidos."
                }
                rs.close()
                stmt.close()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Erro ao acessar o banco."
            } finally {
                DatabaseHelper.close()
            }
        } else {
            errorMessage = "Erro ao conectar ao banco."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Fundo
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
            // Logo circular
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

            // Campo de e-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Insira o seu e-mail") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de senha
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Insira a sua senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensagem de erro
            errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botões
            Button(
                onClick = onNavigateToRecovery,
                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text("Esqueci a senha", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToRegister,
                colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text("Cadastre-se", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { login() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text("Login", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
