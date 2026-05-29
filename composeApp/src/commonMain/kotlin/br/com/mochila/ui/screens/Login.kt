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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.presenter.LoginPresenter
import br.com.mochila.presenter.LoginView
import kotlinx.coroutines.launch
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val loginFormMaxWidth = 360.dp
private val loginFormHorizontalMargin = 48.dp

// Tela de login com layouts responsivos
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onLoginSuccess: (userId: Int) -> Unit
) {
    // Paleta de cores da tela de login
    val fundoTela = Color(0xFFF8F8F8)
    val rosa = Color(0xFFFF6694)
    val laranja = Color(0xFFFFBA5E)
    val logoArea = Color(0xFFFF6694)

    // Campos do formulario e mensagem de erro
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Presenter conectado a interface LoginView
    val presenter = remember {
        object : LoginView {
            override fun showError(message: String) { errorMessage = message; isLoading = false }
            override fun navigateToHome(userId: Int) { isLoading = false; onLoginSuccess(userId) }
        }.let { view -> LoginPresenter(view) }
    }

    fun doLogin() {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                presenter.login(email, password)
            } catch (e: Throwable) {
                println("⚠️ Erro não tratado no login: ${e::class.simpleName}: ${e.message}")
                errorMessage = "Erro ao fazer login. Tente novamente."
                isLoading = false
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela)
    ) {
        val isWide = maxWidth >= 700.dp

        // Layout em duas colunas para telas largas
        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Coluna esquerda com logo e slogan
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

                // Coluna direita com formulario de login
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
                            .verticalScroll(rememberScrollState())
                            .onKeyEvent {
                                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                                    doLogin()
                                    true
                                } else false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(32.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("E-mail", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                            OutlinedTextField(
                                value = email,
                                onValueChange = { if (it.length <= 30) email = it.filter { c -> c != ' ' } },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Insira o seu e-mail", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                                singleLine = true,
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
                                placeholder = { Text("Insira a sua senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                                singleLine = true,
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

                        errorMessage?.let { msg ->
                            // Banner de erro de validacao ou credenciais
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) { Text(msg, color = Color(0xFFB71C1C), fontSize = 15.sp) }
                            Spacer(Modifier.height(16.dp))
                        }

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Botoes de login, recuperacao e cadastro
                            OutlinedButton(
                                onClick = { doLogin() },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, rosa),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = rosa),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                            ) { Text("Login", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                            Button(
                                onClick = onNavigateToRecovery,
                                colors = ButtonDefaults.buttonColors(containerColor = rosa),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                            ) { Text("Esqueci a senha", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                            Button(
                                onClick = onNavigateToRegister,
                                colors = ButtonDefaults.buttonColors(containerColor = laranja),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                            ) { Text("É novo(a) por aqui? Cadastre-se!", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        } else {
            // Layout compacto centralizado para mobile
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 1f,
            )
            val formWidth = minOf(maxWidth - loginFormHorizontalMargin, loginFormMaxWidth).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier
                    .width(formWidth)
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState())
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                            doLogin()
                            true
                        } else false
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

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
                        placeholder = { Text("Insira a sua senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
                        singleLine = true,
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

                errorMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) { Text(msg, color = Color(0xFFB71C1C), fontSize = 15.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { doLogin() },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, rosa),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = rosa),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) { Text("Login", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                    Button(
                        onClick = onNavigateToRecovery,
                        colors = ButtonDefaults.buttonColors(containerColor = rosa),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) { Text("Esqueci a senha", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                    Button(
                        onClick = onNavigateToRegister,
                        colors = ButtonDefaults.buttonColors(containerColor = laranja),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) { Text("É novo(a) por aqui? Cadastre-se!", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
