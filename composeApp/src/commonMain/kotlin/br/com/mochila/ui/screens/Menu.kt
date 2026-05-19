package br.com.mochila.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Cor do painel do menu lateral ([Figma — menu lateral](https://www.figma.com/design/UlejWl0a4xUohJj42JbzuQ/App---Mochila?node-id=53-988)). */
private val MenuPink = Color(0xFFFF6694)

private val MenuScrim = Color.Black.copy(alpha = 0.3f)
private val MenuShadow = Color.Black.copy(alpha = 0.25f)

@Composable
fun MenuScreen(
    onCloseMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSubjectsList: () -> Unit = onNavigateToHome,
    onNavigateToPlanoFaltas: () -> Unit = {},
    onNavigateToEventos: () -> Unit = {},
    onNavigateToPlus: () -> Unit = {},
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val sidebarWidth = (maxWidth * 0.8174f).coerceAtMost(394.dp)
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(400)
            ) + fadeIn(tween(400)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(400)
            ) + fadeOut(tween(400))
        ) {
            Row(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight()
                        .shadow(
                            elevation = 4.dp,
                            shape = RectangleShape,
                            spotColor = MenuShadow,
                            ambientColor = MenuShadow
                        )
                        .background(MenuPink, RectangleShape)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        MenuSideItem(
                            text = "Configurações da conta",
                            icon = Res.drawable.menu_icon_settings,
                            onClick = {
                                onCloseMenu()
                                onNavigateToAccountSettings()
                            }
                        )
                        MenuSideItem(
                            text = "Plano de faltas",
                            icon = Res.drawable.menu_icon_today,
                            onClick = {
                                onCloseMenu()
                                onNavigateToPlanoFaltas()
                            }
                        )
                        MenuSideItem(
                            text = "Matérias",
                            icon = Res.drawable.menu_icon_mobile_check,
                            onClick = {
                                onCloseMenu()
                                onNavigateToSubjectsList()
                            }
                        )
                        MenuSideItem(
                            text = "Eventos",
                            icon = Res.drawable.menu_icon_event,
                            onClick = {
                                onCloseMenu()
                                onNavigateToEventos()
                            }
                        )
                        MenuSideItem(
                            text = "Lista de Tarefas",
                            icon = Res.drawable.menu_icon_folder,
                            onClick = {
                                onCloseMenu()
                                onNavigateToTasksList()
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        MenuSideItem(
                            text = "Assine o PLUS!",
                            icon = Res.drawable.menu_icon_star_plus,
                            onClick = {
                                onCloseMenu()
                                onNavigateToPlus()
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                        MenuSideItem(
                            text = "Sair da conta",
                            icon = null,
                            onClick = {
                                onCloseMenu()
                                onLogout()
                            }
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MenuScrim)
                        .clickable(onClick = onCloseMenu)
                ) {
                    IconButton(
                        onClick = onCloseMenu,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 20.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.menu_close),
                            contentDescription = "Fechar menu",
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSideItem(
    text: String,
    icon: DrawableResource?,
    onClick: () -> Unit
) {
    val textStyle = TextStyle(
        color = Color.White,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (icon != null) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Spacer(Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = textStyle)
    }
}
