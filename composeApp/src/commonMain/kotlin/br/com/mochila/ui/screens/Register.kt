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
import br.com.mochila.data.TokenRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.presenter.RegisterPresenter
import br.com.mochila.presenter.RegisterView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.util.EmailService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val registerFormMaxWidth = 360.dp
private val registerFormHorizontalMargin = 48.dp

// Tela de cadastro de novo usuario
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onNavigateToEmailVerify: (email: String) -> Unit
) {
    // Cores padrao das telas de autenticacao
    val fundoTela = Color(0xFFF8F8F8)
    val rosa = Color(0xFFFF6694)
    val logoArea = Color(0xFFFF6694)

    // Estado do formulario de cadastro
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Presenter com envio de codigo de verificacao por e-mail
    val presenter = remember {
        object : RegisterView {
            override fun showValidationError(msg: String) { message = msg; success = false }
            override fun showRegisterSuccess() { message = "Usuário cadastrado! Enviando código de verificação..."; success = true }
            override fun showRegisterError(msg: String) { message = msg; success = false }

            override fun navigateToEmailVerify(registeredEmail: String) {
                scope.launch {
                    isLoading = true
                    val shouldVerify = withContext(Dispatchers.Default) {
                        if (!EmailService.isConfigured) {
                            UserRepository.verifyEmail(registeredEmail)
                            false
                        } else {
                            val code = (100000..999999).random().toString()
                            TokenRepository.saveToken(registeredEmail, code)
                            val sent = EmailService.sendVerificationEmail(registeredEmail, code)
                            if (!sent) {
                                UserRepository.verifyEmail(registeredEmail)
                            }
                            sent
                        }
                    }
                    isLoading = false
                    if (shouldVerify) {
                        onNavigateToEmailVerify(registeredEmail)
                    } else {
                        message = "Conta criada com sucesso!"
                        success = true
                        kotlinx.coroutines.delay(2000)
                        onBackToLogin()
                    }
                }
            }
        }.let { view -> RegisterPresenter(view) }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela)
    ) {
        val isWide = maxWidth >= 700.dp

        // Versao desktop com painel lateral de marca
        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .background(rosa),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.logo),
                                contentDescription = "Logo Mochila Hub",
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Mochila Hub", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Organize sua vida acadêmica", color = Color.White.copy(alpha = 0.85f), fontSize = 15.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .background(fundoTela),
                    contentAlignment = Alignment.Center
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BackButton(onBack = onBackToLogin, backgroundColor = rosa, iconTint = Color.White)
                        }

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("E-mail", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = email,
                                onValueChange = { if (it.length <= 30) email = it.filter { c -> c != ' ' } },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Insira o seu e-mail", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                                singleLine = true,
                                enabled = !isLoading,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                                    cursorColor = rosa,
                                    unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Usuário", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = username,
                                onValueChange = { if (it.length <= 30) username = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Insira o seu usuário", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                                singleLine = true,
                                enabled = !isLoading,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                                    cursorColor = rosa,
                                    unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = password,
                                onValueChange = { if (it.length <= 25) password = it.filter { c -> c != ' ' } },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Crie uma senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                                singleLine = true,
                                enabled = !isLoading,
                                visualTransformation = PasswordVisualTransformation(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                                    cursorColor = rosa,
                                    unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Código de acesso", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = adminCode,
                                onValueChange = { adminCode = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Opcional", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                                singleLine = true,
                                enabled = !isLoading,
                                visualTransformation = PasswordVisualTransformation(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                                    unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                                    cursorColor = rosa,
                                    unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        message?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) { Text(msg, color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C), fontSize = 15.sp) }
                            Spacer(Modifier.height(16.dp))
                        }

                        Button(
                            onClick = {
                        scope.launch {
                            isLoading = true
                            presenter.register(username, email, password, adminCode)
                            isLoading = false
                        }
                    },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = rosa),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Registrar", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        TextButton(
                            onClick = onBackToLogin,
                            colors = ButtonDefaults.textButtonColors(contentColor = rosa)
                        ) {
                            Text("Já tem conta? Faça o Login", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        } else {
            // Versao mobile com logo e formulario central
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 1f,
            )
            val formWidth = minOf(maxWidth - registerFormHorizontalMargin, registerFormMaxWidth).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier
                    .width(formWidth)
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onBack = onBackToLogin, backgroundColor = rosa, iconTint = Color.White)
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
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("E-mail", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.length <= 30) email = it.filter { c -> c != ' ' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Insira o seu e-mail", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                            unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                            cursorColor = rosa,
                            unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Usuário", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { if (it.length <= 30) username = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Insira o seu usuário", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                            unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                            cursorColor = rosa,
                            unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { if (it.length <= 25) password = it.filter { c -> c != ' ' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Crie uma senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                            unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                            cursorColor = rosa,
                            unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Código de acesso", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = adminCode,
                        onValueChange = { adminCode = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Opcional", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp, color = rosa),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                            unfocusedBorderColor = rosa, focusedBorderColor = rosa,
                            cursorColor = rosa,
                            unfocusedPlaceholderColor = rosa.copy(alpha = 0.8f), focusedPlaceholderColor = rosa.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) { Text(msg, color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C), fontSize = 15.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            presenter.register(username, email, password, adminCode)
                            isLoading = false
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = rosa),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Registrar", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBackToLogin,
                    colors = ButtonDefaults.textButtonColors(contentColor = rosa)
                ) {
                    Text("Já tem conta? Faça o Login", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
