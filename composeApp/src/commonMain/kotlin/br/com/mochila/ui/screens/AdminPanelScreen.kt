package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserSession
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val adminRosa   = Color(0xFFFF6694)
private val adminFundo  = Color(0xFFF8F8F8)
private val adminLaranja = Color(0xFFFFBA5E)

@Composable
fun AdminPanelScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onBack: () -> Unit,
) {
    @Composable
    fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onBack = onBack, backgroundColor = adminRosa, iconTint = Color.White)
            }
            Text("Painel Admin", color = adminRosa, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                "Gerenciamento do sistema",
                color = Color.Gray, fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
            )
            AdminCard(
                title = "Gerenciar Usuários",
                description = "Ver, excluir e gerenciar permissões de usuários",
                icon = {
                    Image(
                        painterResource(Res.drawable.user), null,
                        Modifier.size(28.dp), colorFilter = ColorFilter.tint(Color.White),
                    )
                },
                onClick = onNavigateToUsers,
            )
            Spacer(Modifier.height(16.dp))
            AdminCard(
                title = "Logs do Sistema",
                description = "Visualizar ações e erros registrados",
                icon = {
                    Image(
                        painterResource(Res.drawable.notebook), null,
                        Modifier.size(28.dp), colorFilter = ColorFilter.tint(Color.White),
                    )
                },
                onClick = onNavigateToLogs,
            )
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(adminFundo)) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(Modifier.fillMaxSize()) {
                AdminSidebar(modifier = Modifier.weight(0.4f).fillMaxHeight())
                Box(
                    modifier = Modifier.weight(0.6f).fillMaxHeight().background(adminFundo),
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
internal fun AdminSidebar(modifier: Modifier = Modifier) {
    val user = UserSession.currentUser
    Box(
        modifier = modifier.background(adminRosa),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp),
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
            Text("Painel Admin", color = Color.White.copy(alpha = 0.85f), fontSize = 15.sp)
            if (user != null) {
                Spacer(Modifier.height(32.dp))
                ProfileAvatar(
                    name = user.name,
                    photoPath = user.photoPath,
                    size = 64.dp,
                    accentColor = Color.White,
                    onClick = {},
                )
                Spacer(Modifier.height(10.dp))
                Text(user.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun AdminCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(adminRosa),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = adminLaranja, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        Image(
            painter = painterResource(Res.drawable.chevron),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(Color.Gray),
        )
    }
}
