package br.com.mochila.ui.screens.components

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

actual fun pickImageFile(userId: Int): String? = null

actual fun decodeProfilePhotoPainter(photoPath: String): Painter? {
    return runCatching {
        val bitmap = BitmapFactory.decodeFile(photoPath) ?: return null
        BitmapPainter(bitmap.asImageBitmap())
    }.getOrNull()
}
