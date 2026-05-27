package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.model.UserSummary
import br.com.mochila.presenter.AdminUsersPresenter
import br.com.mochila.presenter.AdminUsersView
import br.com.mochila.ui.screens.components.BackButton
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Paleta da gestao de usuarios
private val auRosa    = Color(0xFFFF6694)
private val auFundo   = Color(0xFFF8F8F8)
private val auLaranja = Color(0xFFFFBA5E)

// Lista de usuarios para o admin
@Composable
fun AdminUsersScreen(
    currentUserId: Int,
    onBack: () -> Unit,
    onNavigateToUserDetail: (Int) -> Unit = {},
) {
    var users          by remember { mutableStateOf<List<UserSummary>>(emptyList()) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var feedbackSuccess by remember { mutableStateOf(true) }

    val presenter = remember {
        object : AdminUsersView {
            override fun showUsers(list: List<UserSummary>) { users = list }
            override fun showEmptyState() { users = emptyList() }
            override fun showActionSuccess(msg: String) { feedbackMessage = msg; feedbackSuccess = true }
            override fun showActionError(msg: String)   { feedbackMessage = msg; feedbackSuccess = false }
        }.let { AdminUsersPresenter(it) }
    }

    LaunchedEffect(Unit) { presenter.loadUsers() }

    @Composable
    fun Content() {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BackButton(onBack = onBack, backgroundColor = auRosa, iconTint = Color.White)
                Text("${users.size} usuários", color = Color.Gray, fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))
            Text("Usuários", color = auRosa, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            feedbackMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (users.isEmpty()) {
                    Text(
                        "Nenhum usuário encontrado.",
                        color = Color.Gray, fontSize = 14.sp,
                        modifier = Modifier.padding(top = 32.dp),
                    )
                } else {
                    users.forEach { user ->
                        UserCard(
                            user = user,
                            isSelf = user.id == currentUserId,
                            onClick = { onNavigateToUserDetail(user.id) },
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(auFundo)) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(Modifier.fillMaxSize()) {
                AdminSidebar(modifier = Modifier.weight(0.4f).fillMaxHeight())
                Box(
                    modifier = Modifier.weight(0.6f).fillMaxHeight().background(auFundo),
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

@Composable
private fun UserCard(
    user: UserSummary,
    isSelf: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, if (isSelf) auRosa.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(auRosa.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.user),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(auRosa),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    user.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = auLaranja, maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                if (isSelf) Badge(containerColor = Color(0xFF4CAF50)) { Text("Você", color = Color.White, fontSize = 9.sp) }
                if (user.isAdmin) Badge(containerColor = auRosa) { Text("Admin", color = Color.White, fontSize = 9.sp) }
            }
            Text(user.email, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!user.emailVerified) {
                Text("E-mail não verificado", fontSize = 11.sp, color = Color(0xFFFF9800))
            }
        }
        Image(
            painter = painterResource(Res.drawable.chevron),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            colorFilter = ColorFilter.tint(Color.LightGray),
        )
    }
}
