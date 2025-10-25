package br.com.mochila

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import br.com.mochila.ui.screens.LoginScreen

@Composable
fun App() {
    MaterialTheme {
        Surface {
            LoginScreen()
        }
    }
}
