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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    var success by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun validarEmail(email: String): Boolean {
        return email.contains("@") && email.length <= 30
    }

    fun validarNome(nome: String): Boolean {
        return nome.length in 3..30
    }

    fun validarSenha(senha: String): Boolean {
        val regex = Regex(
            pattern = """^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$"""
        )
        return regex.matches(senha)
    }

    fun register() {

        val emailValido = validarEmail(email)
        val nomeValido = validarNome(username)
        val senhaValida = validarSenha(password)

        if (!emailValido && !nomeValido && !senhaValida) {
            message = "Todos os campos estão incorretos. Verifique e tente novamente."
            success = false
            return
        }

        if (!emailValido) {
            message = "E-mail inválido. Deve conter '@' e ter no máximo 30 caracteres."
            success = false
            return
        }

        if (!nomeValido) {
            message = "O nome de usuário deve ter entre 3 e 30 caracteres."
            success = false
            return
        }

        if (!senhaValida) {
            message = "Senha inválida. Deve ter 8 a 25 caracteres, incluir letra maiúscula, número e caractere especial."
            success = false
            return
        }

        val conn = DatabaseHelper.connect()

        if (conn != null) {
            try {
                val stmt: PreparedStatement = conn.prepareStatement(
                    "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)"
                )
                stmt.setString(1, username)
                stmt.setString(2, email)
                stmt.setString(3, password)
                stmt.executeUpdate()
                stmt.close()

                message = "Usuário cadastrado com sucesso!"
                success = true
                println("✅ Usuário cadastrado: $email")

                //  Redireciona automaticamente para Login
                scope.launch {
                    delay(3000)
                    onBackToLogin()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                message = "Erro ao cadastrar usuário. Verifique se o e-mail já existe."
                success = false
            } finally {
                DatabaseHelper.close()
            }
        } else {
            message = "Erro ao conectar ao banco."
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
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 Fundo curvas
        Image(
            painter = painterResource(Res.drawable.fundo_curvas),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Conteúdo principal
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão Voltar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack = onBackToLogin)
            }

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
                onValueChange = { input ->
                    if (input.length <= 30) email = input
                },
                label = { Text("Insira o seu e-mail") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedLabelColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de usuário
            OutlinedTextField(
                value = username,
                onValueChange = { input ->
                    if (input.length <= 30) username = input
                },
                label = { Text("Insira o seu usuário") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedLabelColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de senha
            OutlinedTextField(
                value = password,
                onValueChange = { input ->
                    if (input.length <= 25) password = input
                },
                label = { Text("Crie uma senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedLabelColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            message?.let { msg ->
                Box(
                    modifier = Modifier
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

            Button(
                onClick = { register() },
                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text("Registrar", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Voltar ao Login
            TextButton(onClick = onBackToLogin) {
                Text("Já tem conta? Faça o Login", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
