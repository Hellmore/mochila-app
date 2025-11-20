package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UsuarioRepository
import br.com.mochila.data.Usuario
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun AccountSettingsScreen(
    userId: Int,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(true) }
    var senhaPopup by remember { mutableStateOf("") }
    var senhaErrada by remember { mutableStateOf(false) }
    var permitirEdicao by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        val usuario: Usuario? = UsuarioRepository.getUsuarioById(userId)
        usuario?.let {
            nome = it.nome
            email = it.email
        }
    }

    fun salvarAlteracoes() {

        if (nome.length !in 3..30) {
            success = false
            message = "O nome deve ter entre 3 e 30 caracteres."
            return
        }

        if (!email.contains("@") || email.length > 30) {
            success = false
            message = "E-mail inválido. Deve conter '@' e ter no máximo 30 caracteres."
            return
        }

        if (senha.isNotBlank()) {
            val senhaValida = Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""")

            if (!senhaValida.matches(senha)) {
                success = false
                message = "A senha deve ter 8–25 caracteres, incluir letra maiúscula, número e símbolo."
                return
            }

            if (senha != confirmarSenha) {
                success = false
                message = "As senhas não coincidem."
                return
            }
        }

        val sucesso = UsuarioRepository.updateUsuario(
            userId,
            nome,
            email,
            if (senha.isBlank()) null else senha
        )

        success = sucesso
        message = if (sucesso) {
            "Perfil atualizado com sucesso!"
        } else {
            "Erro ao atualizar o perfil."
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
            painter = painterResource(Res.drawable.pin),
            contentDescription = "Pin decorativo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.95f),
            contentScale = ContentScale.FillHeight
        )
        Image(
            painter = painterResource(Res.drawable.mochila),
            contentDescription = "Mochila decorativa",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.65f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text("Confirmação de Segurança", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("Digite sua senha para acessar as configurações da conta.")
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = senhaPopup,
                            onValueChange = { input ->
                                if (input.length <= 20) {
                                    senhaPopup = input
                                }
                            },
                            label = { Text("Senha") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (senhaErrada) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Senha incorreta!",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val usuario = UsuarioRepository.getUsuarioById(userId)

                            if (usuario != null && usuario.senha == senhaPopup) {
                                permitirEdicao = true
                                showPasswordDialog = false
                            } else {
                                senhaErrada = true
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onBack()
                        }
                    ) {
                        Text("Cancelar")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White
            )
        }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onBack = onBack)
                }

                Text(
                    "Minha Conta",
                    color = RoxoEscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Foto do Perfil",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(RoxoClaro)
                            .border(2.dp, VerdeLima, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { input ->
                        if (input.length <= 30) nome = input
                    },
                    label = { Text("Nome de Usuário") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.4f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { input ->
                        if (input.length <= 30) email = input
                    },
                    label = { Text("E-mail") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.4f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { input ->
                        if (input.length <= 25) senha = input
                    },
                    label = { Text("Nova Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(0.4f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { input ->
                        if (input.length <= 25) confirmarSenha = input
                    },
                    label = { Text("Confirmar Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(0.4f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .background(
                                color = if (success)
                                    Color(0xFFB9F6CA) // verde claro
                                else
                                    Color(0xFFFFCDD2), // vermelho claro
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = msg,
                            color = if (success)
                                Color(0xFF1B5E20) // verde escuro
                            else
                                Color(0xFFB71C1C), // vermelho escuro
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }


                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        24.dp,
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { salvarAlteracoes() },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(180.dp).height(45.dp)
                    ) {
                        Text("Salvar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(180.dp).height(45.dp)
                    ) {
                        Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(180.dp).height(45.dp)
                    ) {
                        Text("Sair da Conta", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(180.dp).height(45.dp)
                ) {
                    Text("Excluir Conta", color = Color.White, fontWeight = FontWeight.Bold)
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = {
                            Text(
                                "Excluir Conta",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        text = {
                            Text("Tem certeza que deseja excluir sua conta? Essa ação não pode ser desfeita.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val deletado = UsuarioRepository.deleteUsuario(userId)
                                    showDeleteDialog = false

                                    if (deletado) {
                                        println("Conta excluída com sucesso.")
                                        onLogout() // volta pra tela de login
                                    } else {
                                        println("Erro ao excluir conta.")
                                    }
                                }
                            ) {
                                Text(
                                    "Excluir",
                                    color = Color(0xFFD9534F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        containerColor = Color.White
                    )
                }
            }
        }
    }
