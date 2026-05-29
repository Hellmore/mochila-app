package br.com.mochila

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import br.com.mochila.data.AppDataDir
import br.com.mochila.data.initAndroidAssets
import java.io.File

// Ponto de entrada Android: inicializa dados e Compose
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val crashFile = File(filesDir, "last_crash.txt")

        // Captura excecoes nao tratadas e salva no disco
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            try {
                crashFile.writeText(
                    "Thread: ${thread.name}\n\n${exception.stackTraceToString()}"
                )
            } catch (_: Exception) {}
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        AppDataDir.init(filesDir.absolutePath)
        initAndroidAssets(assets)

        val lastCrash = if (crashFile.exists()) {
            crashFile.readText().also { crashFile.delete() }
        } else null

        setContent {
            var showCrash by remember { mutableStateOf(lastCrash != null) }

            if (showCrash && lastCrash != null) {
                AlertDialog(
                    onDismissRequest = { showCrash = false },
                    title = { Text("Crash anterior capturado") },
                    text = { Text(lastCrash.take(2000)) },
                    confirmButton = {
                        TextButton(onClick = { showCrash = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
