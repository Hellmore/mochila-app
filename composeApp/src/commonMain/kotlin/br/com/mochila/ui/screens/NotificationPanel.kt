package br.com.mochila.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.model.Notification
import br.com.mochila.model.NotificationType

// Cores do painel lateral de notificacoes
private val PanelPink = Color(0xFFFF6694)
private val PanelBg = Color(0xFFFFF0F4)
private val UnreadBg = Color(0xFFFFE0EB)
private val Scrim = Color.Black.copy(alpha = 0.3f)

// Drawer com lista de notificacoes do usuario
@Composable
fun NotificationPanel(
    notifications: List<Notification>,
    onMarkAllAsRead: () -> Unit,
    onDismiss: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val panelWidth = (maxWidth * 0.85f).coerceAtMost(360.dp)
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) +
                fadeIn(tween(400)),
        ) {
            Row(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Scrim)
                        .clickable(onClick = onDismiss),
                )
                Box(
                    modifier = Modifier
                        .width(panelWidth)
                        .fillMaxHeight()
                        .shadow(4.dp, RectangleShape)
                        .background(PanelBg, RectangleShape),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Notificações",
                                color = PanelPink,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (notifications.any { !it.isRead }) {
                                TextButton(onClick = onMarkAllAsRead) {
                                    Text(
                                        text = "Marcar como lidas",
                                        color = Color(0xFFFF9800),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        if (notifications.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Nenhuma notificação", color = Color.Gray, fontSize = 14.sp)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(notifications) { notif ->
                                    NotificationItem(notification = notif)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification) {
    val accentColor = when (notification.type) {
        NotificationType.ABSENCE_LIMIT, NotificationType.TASK_OVERDUE -> Color(0xFFE53935)
        NotificationType.ABSENCE_WARNING, NotificationType.TASK_HIGH_PRIORITY_NO_DATE -> Color(0xFFFF9800)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (notification.isRead) Color.White else UnreadBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accentColor, RoundedCornerShape(4.dp)),
            )
            Text(
                text = notification.type.label,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = notification.message,
            color = Color(0xFF888888),
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}
