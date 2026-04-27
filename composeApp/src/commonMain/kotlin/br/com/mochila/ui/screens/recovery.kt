package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.presenter.RecoveryPresenter
import br.com.mochila.ui.screens.components.BackButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val recoveryFormMaxWidth = 360.dp
private val recoveryFormHorizontalMargin = 48.dp

@Composable
fun RecoveryScreen(
    onBackToLogin: () -> Unit,
    onNavigateToCodeEntry: (email: String) -> Unit
) {
    val fundoTela = Color(0xFFF8F8F8)
    val rosa = Color(0xFFFF6694)
    val logoArea = Color(0xFFD9D9D9)

    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val presenter = remember { RecoveryPresenter() }

    fun onSend() {
        errorMessage = null
        scope.launch {
            isLoading = true
            val error = withContext(Dispatchers.Default) {
                presenter.sendRecoveryCode(email, confirmEmail)
            }
            isLoading = false
            if (error == null) {
                onNavigateToCodeEntry(email.trim())
            } else {
                errorMessage = error
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela)
    ) {
        val usableWidth = maxWidth - recoveryFormHorizontalMargin
        val formWidth = minOf(usableWidth, recoveryFormMaxWidth).coerceAtLeast(0.dp)

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
                    .padding(top = 32.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    onBack = onBackToLogin,
                    backgroundColor = rosa,
                    iconTint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(151.dp)
                    .height(140.dp)
                    .background(logoArea, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo Mochila Hub",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "E-mail",
                    color = rosa,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Insira o seu e-mail",
                            color = rosa.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = rosa
                    ),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = rosa,
                        focusedBorderColor = rosa,
                        cursorColor = rosa,
                        unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f),
                        focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Confirme o e-mail",
                    color = rosa,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
                OutlinedTextField(
                    value = confirmEmail,
                    onValueChange = { confirmEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Insira o seu e-mail novamente",
                            color = rosa.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = rosa
                    ),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = rosa,
                        focusedBorderColor = rosa,
                        cursorColor = rosa,
                        unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f),
                        focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Button(
                onClick = { onSend() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = rosa),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Enviar e-mail de Recuperação",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onBackToLogin,
                colors = ButtonDefaults.textButtonColors(contentColor = rosa)
            ) {
                Text(
                    "Voltar ao Login",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
