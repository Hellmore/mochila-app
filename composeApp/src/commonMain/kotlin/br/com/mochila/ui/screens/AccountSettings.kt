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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.User
import br.com.mochila.presenter.AccountSettingsPresenter
import br.com.mochila.presenter.AccountSettingsView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import br.com.mochila.ui.screens.components.pickImageFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val accountFormMaxWidth = 360.dp
private val accountFormHorizontalMargin = 48.dp

@Composable
fun AccountSettingsScreen(
    userId: Int,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    passwordFlowMessage: Pair<String, Boolean>? = null,
    onPasswordFlowMessageConsumed: () -> Unit = {},
) {
    val fundoTela = Color(0xFFF8F8F8)
    val rosa    = Color(0xFFFF6694)
    val laranja = Color(0xFFFFBA5E)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    var showDeleteDialog  by remember { mutableStateOf(false) }
    var showRedeemDialog  by remember { mutableStateOf(false) }
    var redeemCode        by remember { mutableStateOf("") }
    var redeemMessage     by remember { mutableStateOf<String?>(null) }
    var redeemSuccess     by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val presenter = remember {
        object : AccountSettingsView {
            override fun showUser(user: User) {
                name = user.name
                email = user.email
                photoPath = user.photoPath
            }
            override fun showValidationError(msg: String) { message = msg; success = false }
            override fun showSaveSuccess() { message = "Perfil atualizado com sucesso!"; success = true }
            override fun showSaveError() { message = "Erro ao atualizar o perfil."; success = false }
            override fun showDeleteSuccess() {}
            override fun showDeleteError() { message = "Erro ao excluir conta."; success = false }
            override fun showAdminCodeSuccess() {
                coroutineScope.launch {
                    val updated = withContext(Dispatchers.IO) { UserRepository.findById(userId) }
                    if (updated != null) UserSession.set(updated)
                }
                redeemMessage = null
                redeemCode = ""
                showRedeemDialog = false
                message = "Você agora é administrador! Acesse o menu lateral para ver o Painel Admin."
                success = true
            }
            override fun showAdminCodeError(msg: String) { redeemMessage = msg; redeemSuccess = false }
            override fun navigateBack() { onBack() }
            override fun navigateToLogin() { onLogout() }
        }.let { view -> AccountSettingsPresenter(view) }
    }

    LaunchedEffect(userId) { presenter.loadUser(userId) }

    LaunchedEffect(passwordFlowMessage) {
        val feedback = passwordFlowMessage ?: return@LaunchedEffect
        message = feedback.first
        success = feedback.second
        onPasswordFlowMessageConsumed()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela)
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .background(rosa),
                    contentAlignment = Alignment.Center
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
                        ProfileAvatar(
                            name = name,
                            photoPath = photoPath,
                            size = 72.dp,
                            accentColor = Color.White,
                            onClick = {}
                        )
                        Spacer(Modifier.height(12.dp))
                        if (name.isNotBlank()) {
                            Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    val path = pickImageFile(userId) ?: return@launch
                                    withContext(Dispatchers.IO) { UserRepository.updatePhoto(userId, path) }
                                    photoPath = path
                                    UserSession.updatePhoto(path)
                                }
                            }) {
                                Text("Alterar foto", color = Color.White, fontSize = 12.sp)
                            }
                            if (photoPath != null) {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) { UserRepository.removePhoto(userId) }
                                        photoPath = null
                                        UserSession.updatePhoto(null)
                                    }
                                }) {
                                    Text("Remover", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                }
                            }
                        }
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
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackButton(onBack = onBack, backgroundColor = rosa, iconTint = Color.White)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Minha Conta", color = rosa, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)

                    Spacer(Modifier.height(24.dp))

                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Nome de Usuário", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { if (it.length <= 30) name = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Insira o seu usuário", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
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

                    Spacer(Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onNavigateToChangePassword,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, rosa),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = rosa)
                    ) { Text("Alterar Senha", fontWeight = FontWeight.Medium, fontSize = 16.sp) }

                    if (UserSession.currentUser?.isAdmin != true) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { showRedeemDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, laranja),
                            colors = ButtonDefaults.buttonColors(containerColor = laranja, contentColor = Color.White)
                        ) { Text("Resgatar código de acesso", fontWeight = FontWeight.Medium, fontSize = 16.sp) }
                    }

                    Spacer(Modifier.height(20.dp))

                    message?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) { Text(msg, color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C), fontSize = 15.sp) }
                        Spacer(Modifier.height(16.dp))
                    }

                    Button(
                        onClick = { presenter.saveChanges(userId, name, email) },
                        colors = ButtonDefaults.buttonColors(containerColor = rosa),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                    ) { Text("Salvar", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, rosa),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = rosa)
                    ) { Text("Cancelar", fontWeight = FontWeight.Medium, fontSize = 16.sp) }

                    Spacer(Modifier.height(16.dp))

                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = rosa)
                    ) { Text("Sair da Conta", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                    Spacer(Modifier.height(4.dp))

                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Excluir Conta", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

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
            val formWidth = minOf(maxWidth - accountFormHorizontalMargin, accountFormMaxWidth).coerceAtLeast(0.dp)
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
                    BackButton(onBack = onBack, backgroundColor = rosa, iconTint = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Minha Conta", color = rosa, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)

                Spacer(modifier = Modifier.height(20.dp))

                ProfileAvatar(name = name, photoPath = photoPath, size = 100.dp, accentColor = rosa, onClick = {})
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            val path = pickImageFile(userId) ?: return@launch
                            withContext(Dispatchers.IO) { UserRepository.updatePhoto(userId, path) }
                            photoPath = path
                            UserSession.updatePhoto(path)
                        }
                    }) { Text("Alterar foto", color = rosa, fontSize = 12.sp) }
                    if (photoPath != null) {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) { UserRepository.removePhoto(userId) }
                                photoPath = null
                                UserSession.updatePhoto(null)
                            }
                        }) { Text("Remover foto", color = rosa.copy(alpha = 0.6f), fontSize = 12.sp) }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nome de Usuário", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 30) name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Insira o seu usuário", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
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

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onNavigateToChangePassword,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, rosa),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = rosa)
                ) { Text("Alterar Senha", fontWeight = FontWeight.Medium, fontSize = 16.sp) }

                if (UserSession.currentUser?.isAdmin != true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showRedeemDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, laranja),
                        colors = ButtonDefaults.buttonColors(containerColor = laranja, contentColor = rosa)
                    ) { Text("Resgatar código de acesso", fontWeight = FontWeight.Medium, fontSize = 16.sp) }
                }

                Spacer(modifier = Modifier.height(20.dp))

                message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) { Text(msg, color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C), fontSize = 15.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { presenter.saveChanges(userId, name, email) },
                    colors = ButtonDefaults.buttonColors(containerColor = rosa),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                ) { Text("Salvar", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, rosa),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = rosa)
                ) { Text("Cancelar", fontWeight = FontWeight.Medium, fontSize = 16.sp) }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = rosa)
                ) { Text("Sair da Conta", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Excluir Conta", fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp) }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir Conta", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = { Text("Tem certeza que deseja excluir sua conta? Essa ação não pode ser desfeita.") },
                confirmButton = {
                    TextButton(onClick = { showDeleteDialog = false; presenter.deleteAccount(userId) }) {
                        Text("Excluir", color = Color(0xFFD9534F), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White
            )
        }

        if (showRedeemDialog) {
            AlertDialog(
                onDismissRequest = { showRedeemDialog = false; redeemCode = ""; redeemMessage = null },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                title = { Text("Resgatar código de acesso", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = rosa) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Digite o código para elevar sua conta a administrador.", color = Color.Gray, fontSize = 13.sp)
                        OutlinedTextField(
                            value = redeemCode,
                            onValueChange = { redeemCode = it; redeemMessage = null },
                            placeholder = { Text("Código de acesso", fontSize = 14.sp, color = Color.Gray) },
                            singleLine = true,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = laranja,
                                unfocusedBorderColor = laranja,
                                cursorColor = laranja,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        redeemMessage?.let { msg ->
                            Text(msg, color = if (redeemSuccess) Color(0xFF1B5E20) else Color(0xFFB71C1C), fontSize = 13.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { presenter.redeemAdminCode(userId, redeemCode) }) {
                        Text("Resgatar", color = rosa, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRedeemDialog = false; redeemCode = ""; redeemMessage = null }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
            )
        }
    }
}
