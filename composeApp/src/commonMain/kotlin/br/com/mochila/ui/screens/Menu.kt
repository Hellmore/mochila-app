package br.com.mochila.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.presenter.MenuPresenter
import br.com.mochila.presenter.MenuView
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MenuScreen(
    userId: Int,
    onCloseMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoClaro = Color(0xFF7F55CE)
    val RoxoEscuro = Color(0xFF5336CB)

    var userName by remember { mutableStateOf("Carregando...") }

    val presenter = remember {
        object : MenuView {
            override fun showUserName(name: String) { userName = name }
        }.let { view -> MenuPresenter(view) }
    }

    LaunchedEffect(userId) {
        presenter.loadUserName(userId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(onClick = onCloseMenu)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 400)
            ) + fadeIn(animationSpec = tween(400)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 400)
            ) + fadeOut(animationSpec = tween(400))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.20f)
                    .background(RoxoClaro)
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(RoxoEscuro)
                                .clickable {
                                    onCloseMenu()
                                    onNavigateToAccountSettings()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.user),
                                contentDescription = "Foto do usuário",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(80.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(userName, color = Color.White.copy(alpha = 0.9f), fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        MenuItem("Configurações da conta", Res.drawable.config) {
                            onCloseMenu()
                            onNavigateToAccountSettings()
                        }
                        MenuItem("Matérias") {
                            onCloseMenu()
                            onNavigateToHome()
                        }
                        MenuItem("Lista de Tarefas") {
                            onCloseMenu()
                            onNavigateToTasksList()
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        MenuItem("Sair da conta") {
                            onCloseMenu()
                            onLogout()
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        IconButton(
                            onClick = onCloseMenu,
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.esquerda),
                                contentDescription = "Fechar menu",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    iconRes: DrawableResource? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = text, color = Color.White, fontSize = 15.sp)

        if (iconRes != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}