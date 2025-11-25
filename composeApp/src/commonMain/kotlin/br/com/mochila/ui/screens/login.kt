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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UsuarioRepository
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onLoginSuccess: (userId: Int) -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val VerdeLima = Color(0xFFC5E300)
    val RoxoClaro = Color(0xFF7F55CE)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "E-mail e senha não podem estar em branco."
            return
        }

        val emailExiste = UsuarioRepository.emailExiste(email)
        val userId = UsuarioRepository.validarLogin(email, password)

        // Mensagens de erro
        if (!emailExiste && userId == null) {
            errorMessage = "E-mail e senha incorretos."
            return
        }

        if (!emailExiste) {
            errorMessage = "E-mail incorreto."
            return
        }

        if (emailExiste && userId == null) {
            errorMessage = "Senha incorreta."
            return
        }

        onLoginSuccess(userId!!)
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
                .fillMaxWidth()
                .onKeyEvent {
                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                        login()
                        true
                    } else false
                },
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

            OutlinedTextField(
                value = email,
                onValueChange = { input ->
                    if (input.length <= 30) {
                        email = input
                    }
                },
                label = { Text("Insira o seu e-mail") },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedLabelColor = Color.White,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { input ->
                    if (input.length <= 25) {
                        password = input
                    }
                },
                label = { Text("Insira a sua senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedLabelColor = Color.White,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            errorMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFCDD2), // Vermelho claro (erro)
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = msg,
                        color = Color(0xFFB71C1C), // Vermelho escuro (erro)
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = { login() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text("Login", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

//            Button(
//                onClick = onNavigateToRecovery,
//                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
//                shape = RoundedCornerShape(8.dp),
//                modifier = Modifier.fillMaxWidth().height(42.dp)
//            ) {
//                Text("Esqueci a senha", color = Color.White, fontSize = 14.sp)
//            }
        }
    }
}
