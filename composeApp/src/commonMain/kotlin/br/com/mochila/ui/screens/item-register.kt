package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun ItemRegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSubjectRegister: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit // ✅ Parâmetro adicionado
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    var selectedTabIndex by remember { mutableStateOf(0) }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackButton(onBack = onBack)
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

            Text(
                "Novo Lembrete",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = RoxoEscuro,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = RoxoEscuro
                    )
                }
            ) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("Evento", modifier = Modifier.padding(12.dp))
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("Tarefa", modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> EventoForm(onNavigateToHome)
                1 -> TarefaForm(onNavigateToHome)
            }

            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = onNavigateToSubjectRegister) {
                Text("Não encontrou a matéria? Cadastre uma nova", color = RoxoClaro)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
        
        // TODO: Adicionar um menu inferior que use onOpenMenu se necessário
    }
}

@Composable
fun EventoForm(onSave: () -> Unit) {
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Título do Evento") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoxoClaro, unfocusedBorderColor = RoxoClaro)
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Data do Evento") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoxoClaro, unfocusedBorderColor = RoxoClaro)
        )
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.fillMaxWidth(0.9f).height(45.dp)
        ) {
            Text("Salvar Evento", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TarefaForm(onSave: () -> Unit) {
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Título da Tarefa") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoxoClaro, unfocusedBorderColor = RoxoClaro)
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Data Limite") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoxoClaro, unfocusedBorderColor = RoxoClaro)
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Blockers") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoxoClaro, unfocusedBorderColor = RoxoClaro)
        )
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.fillMaxWidth(0.9f).height(45.dp)
        ) {
            Text("Salvar Tarefa", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
