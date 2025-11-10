package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.Materia
import br.com.mochila.data.MateriaRepository
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onOpenMenu: () -> Unit, // ✅ Parâmetro corrigido
    onNavigateToAdd: () -> Unit,
    onNavigateToSubject: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    val materias by remember(userId) { mutableStateOf(MateriaRepository.listarMaterias(userId)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Matérias", color = RoxoEscuro, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(RoxoClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuário",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }

            if (materias.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhuma matéria cadastrada",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(materias) { materia ->
                        MateriaItem(materia, onNavigateToSubject)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = RoxoEscuro.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenMenu) { // ✅ Lógica corrigida
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu lateral",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onNavigateToAdd) {
                    Image(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Adicionar",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Início",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MateriaItem(materia: Materia, onClick: () -> Unit) {
    val VerdeClaro = Color(0xFFE6F5B0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdeClaro, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(materia.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(">", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Gray)
    }
}
