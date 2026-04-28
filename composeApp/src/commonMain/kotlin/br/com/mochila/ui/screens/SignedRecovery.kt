package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserSession
import br.com.mochila.presenter.SignedRecoveryPresenter
import br.com.mochila.presenter.SignedRecoveryView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar

private val signedRecoveryFormMaxWidth = 360.dp
private val signedRecoveryFormHorizontalMargin = 48.dp

@Composable
fun SignedRecoveryScreen(
    userId: Int,
    onBack: () -> Unit,
    onPasswordChangeFinished: (message: String, success: Boolean) -> Unit,
) {
    val fundoTela = Color(0xFFF8F8F8)
    val rosa = Color(0xFFFF6694)

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val onFinished by rememberUpdatedState(onPasswordChangeFinished)

    val presenter = remember {
        object : SignedRecoveryView {
            override fun showValidationError(msg: String) { message = msg }
            override fun showChangeSuccess() { onFinished("Senha alterada com sucesso!", true) }
            override fun showChangeError() { onFinished("Não foi possível alterar a senha.", false) }
        }.let { view -> SignedRecoveryPresenter(view) }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela)
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Painel esquerdo — avatar
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
                        ProfileAvatar(
                            name = UserSession.currentUser?.name ?: "",
                            photoPath = UserSession.currentUser?.photoPath,
                            size = 120.dp,
                            accentColor = Color.White,
                            onClick = {}
                        )
                        Spacer(Modifier.height(16.dp))
                        UserSession.currentUser?.name?.takeIf { it.isNotBlank() }?.let { name ->
                            Text(name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }

                // Painel direito — formulário
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .background(fundoTela),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 420.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BackButton(onBack = onBack, backgroundColor = rosa, iconTint = Color.White)
                        }

                        Spacer(Modifier.height(16.dp))

                        Text("Alterar Senha", color = rosa, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Informe sua senha atual e escolha uma nova senha.",
                            color = rosa.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(20.dp))

                        PasswordFields(
                            currentPassword = currentPassword,
                            onCurrentPasswordChange = { if (it.length <= 25) currentPassword = it },
                            newPassword = newPassword,
                            onNewPasswordChange = { if (it.length <= 25) newPassword = it },
                            confirmPassword = confirmPassword,
                            onConfirmPasswordChange = { if (it.length <= 25) confirmPassword = it },
                            rosa = rosa
                        )

                        Spacer(Modifier.height(20.dp))

                        message?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) { Text(msg, color = Color(0xFFB71C1C), fontSize = 15.sp) }
                            Spacer(Modifier.height(16.dp))
                        }

                        Button(
                            onClick = { presenter.changePassword(userId, currentPassword, newPassword, confirmPassword) },
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

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        } else {
            val formWidth = minOf(maxWidth - signedRecoveryFormHorizontalMargin, signedRecoveryFormMaxWidth).coerceAtLeast(0.dp)
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

                Text("Alterar Senha", color = rosa, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)

                Spacer(modifier = Modifier.height(20.dp))

                ProfileAvatar(
                    name = UserSession.currentUser?.name ?: "",
                    photoPath = UserSession.currentUser?.photoPath,
                    size = 100.dp,
                    accentColor = rosa,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Informe sua senha atual e escolha uma nova senha.",
                    color = rosa.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                PasswordFields(
                    currentPassword = currentPassword,
                    onCurrentPasswordChange = { if (it.length <= 25) currentPassword = it },
                    newPassword = newPassword,
                    onNewPasswordChange = { if (it.length <= 25) newPassword = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { if (it.length <= 25) confirmPassword = it },
                    rosa = rosa
                )

                Spacer(modifier = Modifier.height(20.dp))

                message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) { Text(msg, color = Color(0xFFB71C1C), fontSize = 15.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { presenter.changePassword(userId, currentPassword, newPassword, confirmPassword) },
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

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PasswordFields(
    currentPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    rosa: Color
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Senha atual", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
        OutlinedTextField(
            value = currentPassword,
            onValueChange = onCurrentPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Insira a sua senha atual", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
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

    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Nova senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Crie uma nova senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
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

    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Confirmar nova senha", color = rosa, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Repita a nova senha", color = rosa.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 20.sp) },
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
}
