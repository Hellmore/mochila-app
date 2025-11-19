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
import androidx.compose.material3.OutlinedTextField
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
import br.com.mochila.data.Tarefa
import br.com.mochila.data.TarefaRepository
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun TaskListScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    // Estado do filtro
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Carrega tarefas
    val tarefas by remember(userId) {
        mutableStateOf(TarefaRepository.listarTarefas(userId))
    }

    // Lista filtrada
    val filteredTarefas by remember(tarefas, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) {
                tarefas
            } else {
                tarefas.filter {
                    it.titulo.contains(searchQuery, ignoreCase = true)
                }
            }
        )
    }

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

            // ---- HEADER ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Tarefas",
                    color = RoxoEscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

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
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // ---- BOTÃO DE FILTRO ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(RoxoClaro)
                        .clickable {
                            isSearchExpanded = !isSearchExpanded
                            // ao fechar o filtro, limpa a busca
                            if (!isSearchExpanded) {
                                searchQuery = ""
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Filtro",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.drop),
                            contentDescription = "Abrir filtro",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ---- CAMPO DE BUSCA (EXPANDIDO) ----
            if (isSearchExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Buscar tarefa por título") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ---- LISTA ----
            when {
                tarefas.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma tarefa cadastrada.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }

                filteredTarefas.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma tarefa encontrada para esse filtro.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(filteredTarefas) { tarefa ->
                            TarefaItem(tarefa) { tarefaId ->
                                onNavigateToTaskDetail(tarefaId)
                            }
                        }
                    }
                }
            }
        }

        // Voltar
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Image(
                    painter = painterResource(Res.drawable.esquerda),
                    contentDescription = "Voltar",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Menu inferior
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
                IconButton(onClick = onOpenMenu) {
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
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TarefaItem(
    tarefa: Tarefa,
    onClick: (Int) -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val VerdeClaro = Color(0xFFE6F5B0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(tarefa.id_tarefa) }
            .background(VerdeClaro, RoundedCornerShape(16.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = tarefa.titulo,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = RoxoEscuro
        )

        tarefa.descricao?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}
