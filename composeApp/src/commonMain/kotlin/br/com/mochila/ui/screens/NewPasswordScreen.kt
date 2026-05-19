package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import br.com.mochila.presenter.NewPasswordPresenter
import br.com.mochila.ui.screens.components.BackButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val newPassFormMaxWidth = 360.dp
private val newPassFormHorizontalMargin = 48.dp

@Composable
fun NewPasswordScreen(
    email: String,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
) {
    val fundoTela = Color(0xFFF8F8F8)
    val rosa = Color(0xFFFF6694)

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val presenter = remember { NewPasswordPresenter() }

    fun onReset() {
        errorMessage = null
        scope.launch {
            isLoading = true
            val error = withContext(Dispatchers.Default) {
                presenter.resetPassword(email, newPassword, confirmPassword)
            }
            isLoading = false
            if (error == null) {
                successMessage = "Senha atualizada com sucesso!"
                delay(2000)
                onNavigateToLogin()
            } else {
                errorMessage = error
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .background(rosa),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.logo),
                                contentDescription = "Logo Mochila Hub",
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Mochila Hub", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Organize sua vida acadêmica", color = Color.White.copy(alpha = 0.85f), fontSize = 15.sp)
                        Spacer(Modifier.height(32.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .background(fundoTela),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 1f,
                    )
                    Column(
                        modifier = Modifier
                            .widthIn(max = 420.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BackButton(onBack = onBack, backgroundColor = rosa, iconTint = Color.White)
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Nova Senha",
                            color = rosa,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Crie uma nova senha para sua conta",
                            color = Color(0xFF555555),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )

                        Spacer(Modifier.height(24.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nova senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { if (it.length <= 25) newPassword = it.filter { c -> c != ' ' } },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Digite a nova senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp) },
                                singleLine = true,
                                enabled = !isLoading,
                                visualTransformation = PasswordVisualTransformation(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                                    cursorColor = rosa,
                                    unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f),
                                ),
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Confirme a nova senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { if (it.length <= 25) confirmPassword = it.filter { c -> c != ' ' } },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Repita a nova senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp) },
                                singleLine = true,
                                enabled = !isLoading,
                                visualTransformation = PasswordVisualTransformation(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                                    cursorColor = rosa,
                                    unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f),
                                ),
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        errorMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                            ) { Text(msg, color = Color(0xFFB71C1C), fontSize = 15.sp) }
                            Spacer(Modifier.height(12.dp))
                        }

                        successMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFB9F6CA), RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                            ) { Text(msg, color = Color(0xFF1B5E20), fontSize = 15.sp) }
                            Spacer(Modifier.height(12.dp))
                        }

                        Button(
                            onClick = { onReset() },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = rosa),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Redefinir Senha", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        } else {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 1f,
            )
            val formWidth = minOf(maxWidth - newPassFormHorizontalMargin, newPassFormMaxWidth).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier
                    .width(formWidth)
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BackButton(onBack = onBack, backgroundColor = rosa, iconTint = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Nova Senha",
                    color = rosa,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Crie uma nova senha para sua conta",
                    color = Color(0xFF555555),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Spacer(Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nova senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { if (it.length <= 25) newPassword = it.filter { c -> c != ' ' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Digite a nova senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                            unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                            cursorColor = rosa,
                            unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f),
                        ),
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Confirme a nova senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { if (it.length <= 25) confirmPassword = it.filter { c -> c != ' ' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Repita a nova senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                            unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                            cursorColor = rosa,
                            unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f),
                        ),
                    )
                }

                Spacer(Modifier.height(24.dp))

                errorMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                    ) { Text(msg, color = Color(0xFFB71C1C), fontSize = 15.sp) }
                    Spacer(Modifier.height(12.dp))
                }

                successMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFB9F6CA), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                    ) { Text(msg, color = Color(0xFF1B5E20), fontSize = 15.sp) }
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick = { onReset() },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = rosa),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Redefinir Senha", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
