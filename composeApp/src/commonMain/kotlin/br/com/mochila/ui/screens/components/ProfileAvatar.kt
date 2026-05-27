package br.com.mochila.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserSession

// Selecao e decodificacao de foto por plataforma
expect fun pickImageFile(userId: Int): String?


expect fun decodeProfilePhotoPainter(photoPath: String): Painter?

// Avatar circular com foto ou inicial do nome
@Composable
fun ProfileAvatar(
    name: String,
    photoPath: String?,
    size: Dp = 100.dp,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f))
            .border(2.dp, accentColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val painter = remember(photoPath) {
            if (photoPath != null) decodeProfilePhotoPainter(photoPath) else null
        }

        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "Foto do Perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = accentColor,
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private val rosa = Color(0xFFFF6694)

@Composable
fun UserAvatarButton(
    size: Dp = 60.dp,
    onClick: () -> Unit,
) {
    val user = UserSession.currentUser
    ProfileAvatar(
        name = user?.name ?: "",
        photoPath = user?.photoPath,
        size = size,
        accentColor = rosa,
        onClick = onClick,
    )
}
