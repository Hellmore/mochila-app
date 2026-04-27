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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserSession
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun ProfileAvatar(
    name: String,
    photoPath: String?,
    size: Dp = 100.dp,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f))
            .border(2.dp, accentColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val painter = remember(photoPath) {
            if (photoPath != null) runCatching {
                BitmapPainter(loadImageBitmap(File(photoPath).inputStream()))
            }.getOrNull() else null
        }

        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "Foto do Perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = accentColor,
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private val rosa = Color(0xFFFF6694)

@Composable
fun UserAvatarButton(
    size: Dp = 60.dp,
    onClick: () -> Unit
) {
    val user = UserSession.currentUser
    ProfileAvatar(
        name = user?.name ?: "",
        photoPath = user?.photoPath,
        size = size,
        accentColor = rosa,
        onClick = onClick
    )
}

fun pickImageFile(userId: Int): String? {
    val dialog = FileDialog(null as Frame?, "Selecionar foto de perfil", FileDialog.LOAD)
    dialog.setFilenameFilter { _, name ->
        name.lowercase().let { it.endsWith(".jpg") || it.endsWith(".jpeg") || it.endsWith(".png") }
    }
    dialog.isVisible = true
    val fileName = dialog.file ?: return null
    val dirName = dialog.directory ?: return null

    val selected = File(dirName, fileName)
    val destDir = File(System.getProperty("user.home"), ".mochila/avatars")
    destDir.mkdirs()
    val dest = File(destDir, "${userId}_${System.currentTimeMillis()}.${selected.extension}")
    selected.copyTo(dest, overwrite = true)
    return dest.absolutePath
}
