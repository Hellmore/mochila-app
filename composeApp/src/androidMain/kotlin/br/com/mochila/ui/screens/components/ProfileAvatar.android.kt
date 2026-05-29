package br.com.mochila.ui.screens.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import java.io.File

// Selecao de imagem ainda nao implementada no Android (usado apenas no desktop)
actual fun pickImageFile(userId: Int): String? = null

// Carrega bitmap do caminho salvo no disco
actual fun decodeProfilePhotoPainter(photoPath: String): Painter? {
    return runCatching {
        val bitmap = BitmapFactory.decodeFile(photoPath) ?: return null
        BitmapPainter(bitmap.asImageBitmap())
    }.getOrNull()
}

// Abre galeria do Android e copia a imagem para o armazenamento interno do app
@Composable
actual fun rememberImagePickerLauncher(userId: Int, onPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val path = copyUriToInternalStorage(context, uri, userId)
        if (path != null) onPicked(path)
    }
    return { launcher.launch("image/*") }
}

private fun copyUriToInternalStorage(context: Context, uri: Uri, userId: Int): String? {
    return runCatching {
        val dir = File(context.filesDir, "avatars")
        dir.mkdirs()
        val dest = File(dir, "${userId}_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        }
        dest.absolutePath
    }.getOrNull()
}
