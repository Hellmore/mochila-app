package br.com.mochila.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.MateriaRepository
import br.com.mochila.data.DatabaseHelper
import java.sql.PreparedStatement
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// 🔹 Representação da disciplina (matéria)
data class Subject(
    val id: Int = 0,
    val nome: String,
    val professor: String,
    val frequencia: String,
    val dataInicio: String,
    val dataFim: String,
    val horasAula: String,
    val semestre: String
)

@Composable
fun SubjectRegisterScreen(
    userId: Int, // ✅ ID do usuário logado
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isEditing: Boolean = false,
    subjectData: Subject? = null
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    var showMenu by remember { mutableStateOf(false) }

    // 🧾 Campos de entrada
    var nomeMateria by remember { mutableStateOf(subjectData?.nome ?: "") }
    var professor by remember { mutableStateOf(subjectData?.professor ?: "") }
    var frequenciaMin by remember { mutableStateOf(subjectData?.frequencia ?: "") }
    var dataInicio by remember { mutableStateOf(subjectData?.dataInicio ?: "") }
    var dataFim by remember { mutableStateOf(subjectData?.dataFim ?: "") }
    var horasPorAula by remember { mutableStateOf(subjectData?.horasAula ?: "") }
    var semestre by remember { mutableStateOf(subjectData?.semestre ?: "") }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    // 🔸 Salvar nova matéria ou atualizar existente
    fun salvarMateria() {
        val frequenciaMinInt = frequenciaMin.toIntOrNull()
        val horasPorAulaInt = horasPorAula.toIntOrNull()

        if (nomeMateria.isBlank() || frequenciaMinInt == null || horasPorAulaInt == null) {
            message = "Preencha todos os campos obrigatórios corretamente."
            success = false
            return
        }

        val operacaoBemSucedida = if (isEditing) {
            MateriaRepository.atualizarMateria(
                idUsuario = userId,
                idDisciplina = subjectData!!.id,
                nome = nomeMateria,
                frequenciaMinima = frequenciaMinInt,
                dataInicio = dataInicio,
                dataFim = dataFim,
                horaAula = horasPorAulaInt
            )
        } else {
            MateriaRepository.insertMateria(
                idUsuario = userId,
                nome = nomeMateria,
                frequenciaMinima = frequenciaMinInt,
                dataInicio = dataInicio,
                dataFim = dataFim,
                horaAula = horasPorAulaInt
            )
        }

        if (operacaoBemSucedida) {
            message = if (isEditing) "Matéria atualizada com sucesso!" else "Matéria cadastrada com sucesso!"
            success = true
            onNavigateToHome()
        } else {
            message = "Erro ao salvar matéria."
            success = false
        }
    }

    // 🔸 Excluir disciplina existente
    fun excluirMateria() {
        if (!isEditing || subjectData == null) return

        val operacaoBemSucedida = MateriaRepository.deletarMateria(userId, subjectData.id)

        if (operacaoBemSucedida) {
            message = "Matéria excluída com sucesso!"
            success = true
            onNavigateToHome()
        } else {
            message = "Erro ao excluir matéria."
            success = false
        }
    }

    // 🎨 Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fundo decorativo
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = "Fundo quadriculado",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(Res.drawable.pin),
            contentDescription = "Pin decorativo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.95f),
            contentScale = ContentScale.FillHeight
        )
        Image(
            painter = painterResource(Res.drawable.mochila),
            contentDescription = "Mochila decorativa",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.65f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabeçalho
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
                if (isEditing) "Editar Matéria" else "Nova Matéria",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✏️ Campos de formulário
            val campos = listOf(
                Pair("Nome da Matéria", nomeMateria) to { it: String -> nomeMateria = it },
                Pair("Professor", professor) to { it: String -> professor = it },
                Pair("Frequência mínima (%)", frequenciaMin) to { it: String -> frequenciaMin = it },
                Pair("Data de Início", dataInicio) to { it: String -> dataInicio = it },
                Pair("Data de Fim", dataFim) to { it: String -> dataFim = it },
                Pair("Horas por Aula", horasPorAula) to { it: String -> horasPorAula = it },
                Pair("Semestre", semestre) to { it: String -> semestre = it },
            )

            campos.forEach { (campo, setValue) ->
                val (placeholder, valor) = campo
                OutlinedTextField(
                    value = valor,
                    onValueChange = setValue,
                    placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = RoxoClaro,
                        unfocusedBorderColor = RoxoClaro
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            message?.let {
                Text(
                    it,
                    color = if (success) Color(0xFF00C853) else Color.Red,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 💾 Botão salvar
            Button(
                onClick = { salvarMateria() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text(
                    if (isEditing) "Salvar alterações" else "Salvar",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // 🗑️ Botão excluir (somente edição)
            if (isEditing) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { excluirMateria() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(0.9f)
                        .height(45.dp)
                ) {
                    Text("Excluir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // 🔹 Menu inferior fixo
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
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showMenu = true }) {
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu lateral",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (showMenu) {
            MenuScreen(
                onCloseMenu = { showMenu = false },
                onNavigateToHome = {
                    showMenu = false
                    onNavigateToHome()
                },
                onLogout = {
                    showMenu = false
                    onLogout()
                }
            )
        }
    }
}
