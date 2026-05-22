package br.com.mochila.ui.screens.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val warningRed = Color(0xFFD61E1E)

@Composable
fun AbsenceLimitWarningIcon(
    modifier: Modifier = Modifier,
    size: Dp = 13.dp,
    contentDescription: String = "Limite de faltas atingido",
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, warningRed, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "!",
            color = warningRed,
            fontSize = (size.value * 0.62f).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
