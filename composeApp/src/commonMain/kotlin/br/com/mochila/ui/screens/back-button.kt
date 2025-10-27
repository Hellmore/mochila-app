package br.com.mochila.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.esquerda
import org.jetbrains.compose.resources.painterResource

@Composable
fun BackButton(
    onBack: () -> Unit,
    backgroundColor: Color = Color(0xFF7F55CE), // RoxoClaro
    iconTint: Color = Color.White,
) {
    IconButton(
        onClick = onBack,
        modifier = Modifier
            .size(45.dp)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(Res.drawable.esquerda),
            contentDescription = "Voltar",
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(iconTint)
        )
    }
}
