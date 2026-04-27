package br.com.mochila

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import br.com.mochila.data.DatabaseHelper
import br.com.mochila.util.EmailService

fun main() = application {
    val conn = DatabaseHelper.connect()
    if (conn != null) {
        println("🚀 Banco conectado e inicializado!")
        DatabaseHelper.close()
    } else {
        println("❌ Erro ao conectar ao banco.")
    }

    if (EmailService.isConfigured) {
        println("📧 SendGrid configurado. Remetente: ${EmailService.senderEmail}")
    } else {
        println("⚠️ SendGrid NÃO configurado — verificação de e-mail desabilitada.")
    }
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Mochila Hub",
        state = windowState,
    ) {
        App()
    }
}
