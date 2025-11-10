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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import mochila_app.composeapp.generated.resources.*

@Composable
fun MenuScreen(
    onCloseMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit, // ✅ Parâmetro para a tela de perfil
    onLogout: () -> Unit
) {
    val RoxoClaro = Color(0xFF7F55CE)
    val RoxoEscuro = Color(0xFF5336CB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onCloseMenu() }
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) + fadeIn(tween(400)),
            exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut(tween(400))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f)
                    .background(RoxoClaro)
                    .clickable(enabled = false) { }
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
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(RoxoEscuro),
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
                        Text("Nome usuário", color = Color.White.copy(alpha = 0.9f), fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // ✅ Item de menu com link para o perfil
                        MenuItem("Configurações da conta", Res.drawable.config) { 
                            onCloseMenu()
                            onNavigateToProfile()
                        }
                        MenuItem("Matérias", Res.drawable.home) {
                            onCloseMenu()
                            onNavigateToHome()
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
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (iconRes != null) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        } else {
            Spacer(modifier = Modifier.width(40.dp)) // Alinha com os outros itens
        }
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}
