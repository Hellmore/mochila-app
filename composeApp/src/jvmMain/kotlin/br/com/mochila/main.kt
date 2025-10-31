package br.com.mochila

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import br.com.mochila.data.DatabaseHelper

fun main() = application {
    val conn = DatabaseHelper.connect()
    if (conn != null) {
        println("🚀 Banco conectado e inicializado!")
        DatabaseHelper.close()
    } else {
        println("❌ Erro ao conectar ao banco.")
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
