package br.com.mochila.ui.screens

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
import br.com.mochila.model.AdminUserStats
import br.com.mochila.model.UserSummary
import br.com.mochila.presenter.AdminUserDetailPresenter
import br.com.mochila.presenter.AdminUserDetailView
import br.com.mochila.ui.screens.components.BackButton
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.background
import org.jetbrains.compose.resources.painterResource

// Paleta do detalhe de usuario admin
private val audRosa  = Color(0xFFFF6694)
private val audFundo = Color(0xFFF8F8F8)
private val audRed   = Color(0xFFD9534F)

// Estatisticas e acoes sobre um usuario
@Composable
fun AdminUserDetailScreen(
    currentAdminId: Int,
    targetUserId: Int,
    onBack: () -> Unit,
) {
    var user             by remember { mutableStateOf<UserSummary?>(null) }
    var stats            by remember { mutableStateOf<AdminUserStats?>(null) }
    var feedbackMsg      by remember { mutableStateOf<String?>(null) }
    var feedbackSuccess  by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val presenter = remember {
        object : AdminUserDetailView {
            override fun showUser(u: UserSummary, s: AdminUserStats) { user = u; stats = s }
            override fun showNotFound()                              { onBack() }
            override fun showActionSuccess(message: String)         { feedbackMsg = message; feedbackSuccess = true }
            override fun showActionError(message: String)           { feedbackMsg = message; feedbackSuccess = false }
            override fun navigateBack()                             { onBack() }
        }.let { AdminUserDetailPresenter(it) }
    }

    LaunchedEffect(targetUserId) { presenter.loadUser(targetUserId) }

    @Composable
    fun FieldDisplay(value: String, label: String) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = audRosa, fontSize = 14.sp) },
            singleLine = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor      = audRosa,
                unfocusedBorderColor    = audRosa,
                focusedLabelColor       = audRosa,
                unfocusedLabelColor     = audRosa,
                focusedTextColor        = Color.Gray,
                unfocusedTextColor      = Color.Gray,
                cursorColor             = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp),
        )
    }

    @Composable
    fun Content() {
        val u = user ?: return
        val s = stats

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 36.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onBack = onBack, backgroundColor = audRosa, iconTint = Color.White)
            }

            Spacer(Modifier.height(8.dp))
            Text("Usuário", color = audRosa, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            feedbackMsg?.let { msg ->
                Box(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .background(
                            if (feedbackSuccess) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                            RoundedCornerShape(10.dp),
                        )
                        .padding(12.dp),
                ) {
                    Text(
                        msg,
                        color = if (feedbackSuccess) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                        fontSize = 13.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            FieldDisplay(value = u.name,  label = "Nome")
            FieldDisplay(value = u.email, label = "E-mail")
            FieldDisplay(value = if (u.isAdmin) "Sim" else "Não", label = "Administrador")
            FieldDisplay(value = if (u.emailVerified) "Verificado" else "Não verificado", label = "Status do e-mail")
            FieldDisplay(value = u.createdAt.take(10), label = "Membro desde")

            if (s != null) {
                Spacer(Modifier.height(4.dp))
                FieldDisplay(
                    value = "${s.subjectCount} matéria(s)   •   ${s.taskCount} tarefa(s)   •   ${s.faltaCount} falta(s)   •   ${s.eventCount} evento(s)",
                    label = "Resumo",
                )
            }

            Spacer(Modifier.height(20.dp))

            val isSelf = currentAdminId == u.id

            Button(
                onClick = { presenter.toggleAdmin(currentAdminId, u) },
                colors = ButtonDefaults.buttonColors(containerColor = audRosa),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(0.9f).height(45.dp),
            ) {
                Text(
                    if (u.isAdmin) "Remover status de admin" else "Tornar admin",
                    color = Color.White, fontWeight = FontWeight.Bold,
                )
            }

            if (!isSelf) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = audRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(0.9f).height(45.dp),
                ) {
                    Text("Excluir usuário", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = Color.White,
                    title = {
                        Text("Confirmar exclusão", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    },
                    text = {
                        Text("Tem certeza que deseja excluir o usuário \"${u.name}\"? Esta ação não pode ser desfeita.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            presenter.deleteUser(currentAdminId, u)
                        }) {
                            Text("Excluir", color = audRed, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    },
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(audFundo)) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(Modifier.fillMaxSize()) {
                AdminSidebar(modifier = Modifier.weight(0.4f).fillMaxHeight())
                Box(
                    modifier = Modifier.weight(0.6f).fillMaxHeight().background(audFundo),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 1f,
                    )
                    Content()
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
            Content()
        }
    }
}
