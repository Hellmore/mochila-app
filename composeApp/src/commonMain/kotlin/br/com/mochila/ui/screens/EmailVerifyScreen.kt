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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.presenter.EmailVerificationPresenter
import br.com.mochila.ui.screens.components.BackButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmailVerifyScreen(
    email: String,
    onBackToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)

    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val presenter = remember { EmailVerificationPresenter() }

    fun onVerify() {
        errorMessage = null
        scope.launch {
            isLoading = true
            val error = withContext(Dispatchers.Default) {
                presenter.verifyCode(email, code)
            }
            isLoading = false
            if (error == null) {
                successMessage = "E-mail confirmado! Redirecionando para o login..."
                delay(2000)
                onNavigateToLogin()
            } else {
                errorMessage = error
            }
        }
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

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val formWidth = minOf(maxWidth - 48.dp, 360.dp).coerceAtLeast(0.dp)

        Column(
            modifier = Modifier
                .width(formWidth)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack = onBackToRegister)
            }

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(RoxoEscuro, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo Mochila Hub",
                    modifier = Modifier.clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Confirme seu cadastro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enviamos um código de 6 dígitos para\n$email",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) code = it },
                placeholder = { Text("Código de 6 dígitos", textAlign = TextAlign.Center) },
                singleLine = true,
                enabled = !isLoading,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            errorMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFCDD2), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(text = msg, color = Color(0xFFB71C1C), fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            successMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFB9F6CA), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(text = msg, color = Color(0xFF1B5E20), fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = { onVerify() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = RoxoEscuro),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Confirmar Cadastro", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onBackToRegister) {
                Text("Não recebi o código", color = Color.White, fontSize = 14.sp)
            }
        }
        }
    }
}
