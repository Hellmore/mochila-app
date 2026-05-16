package br.com.mochila.ui.screens.components

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

private val File.extension: String
    get() = name.substringAfterLast('.', "")

actual fun pickImageFile(userId: Int): String? {
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

actual fun decodeProfilePhotoPainter(photoPath: String): Painter? {
    return runCatching {
        BitmapPainter(loadImageBitmap(File(photoPath).inputStream()))
    }.getOrNull()
}
