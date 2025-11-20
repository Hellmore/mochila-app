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
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
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
    userId: Int,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit,
    isEditing: Boolean = false,
    subjectData: Subject? = null
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)
    val VerdeLima = Color(0xFFC5E300)

    @Composable
    fun CampoRoxo(
        valor: String,
        label: String,
        onChange: (String) -> Unit,
    ) {
        val RoxoClaro = Color(0xFF7F55CE)

        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            label = {
                Text(
                    text = label,
                    color = RoxoClaro,
                    fontSize = 14.sp
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = RoxoClaro,
                unfocusedBorderColor = RoxoClaro,
                focusedLabelColor = RoxoClaro,
                cursorColor = RoxoClaro
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp)
        )
    }

    @Composable
    fun CampoRoxoData(
        valor: String,
        label: String,
        onChange: (String) -> Unit
    ) {
        CampoRoxo(
            valor = valor,
            label = label,
            onChange = { entrada ->

                // Permite apagar sempre
                if (entrada.length < valor.length) {
                    onChange(entrada)
                    return@CampoRoxo
                }

                // Apenas números
                val digits = entrada.filter { it.isDigit() }.take(8)

                // Mascara DD/MM/AAAA
                val formatted = buildString {
                    for (i in digits.indices) {
                        append(digits[i])
                        if (i == 1 || i == 3) append('/')
                    }
                }

                onChange(formatted)
            }
        )
    }

    @Composable
    fun CampoRoxoHorasAula(
        valor: String,
        label: String,
        onChange: (String) -> Unit
    ) {
        CampoRoxo(
            valor = valor,
            label = label,
            onChange = { entrada ->

                // Permitir apagar de forma natural
                if (entrada.length < valor.length) {
                    onChange(entrada.replace("h", ""))
                    return@CampoRoxo
                }

                // Remover h para trabalhar somente com a parte numérica
                val clean = entrada.replace("h", "")

                // Manter somente números e 1 dois-pontos
                val filtered = buildString {
                    var colonAdded = false
                    clean.forEach {
                        if (it.isDigit()) append(it)
                        else if (it == ':' && !colonAdded) {
                            append(':')
                            colonAdded = true
                        }
                    }
                }

                // Limite máximo: "999:99"
                if (filtered.length > 4) return@CampoRoxo

                // Adicionar "h" automaticamente ao final SE tiver pelo menos um número
                onChange(
                    if (filtered.isNotEmpty()) filtered + "h" else filtered
                )
            }
        )
    }

    fun dataValida(data: String): Boolean {
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(data)) return false

        val partes = data.split("/")
        val dia = partes[0].toIntOrNull() ?: return false
        val mes = partes[1].toIntOrNull() ?: return false
        val ano = partes[2].toIntOrNull() ?: return false

        if (mes !in 1..12) return false

        val diasNoMes = when (mes) {
            1,3,5,7,8,10,12 -> 31
            4,6,9,11 -> 30
            2 -> if ((ano % 4 == 0 && ano % 100 != 0) || ano % 400 == 0) 29 else 28
            else -> return false
        }

        return dia in 1..diasNoMes
    }

    var nomeMateria by remember { mutableStateOf(subjectData?.nome ?: "") }
    var professor by remember { mutableStateOf(subjectData?.professor ?: "") }
    var frequenciaMin by remember { mutableStateOf(subjectData?.frequencia ?: "") }
    var dataInicio by remember { mutableStateOf(subjectData?.dataInicio ?: "") }
    var dataFim by remember { mutableStateOf(subjectData?.dataFim ?: "") }
    var horasPorAula by remember { mutableStateOf(subjectData?.horasAula ?: "") }
    var semestre by remember { mutableStateOf(subjectData?.semestre ?: "") }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    fun salvarMateria() {

        // ➤ 1. Verificar campos vazios
        if (
            nomeMateria.isBlank() ||
            professor.isBlank() ||
            frequenciaMin.isBlank() ||
            dataInicio.isBlank() ||
            dataFim.isBlank() ||
            horasPorAula.isBlank() ||
            semestre.isBlank()
        ) {
            message = "Nenhum campo pode estar vazio."
            success = false
            return
        }

        // ➤ 2. Validar datas
        if (!dataValida(dataInicio)) {
            message = "Data de início inválida."
            success = false
            return
        }

        if (!dataValida(dataFim)) {
            message = "Data de término inválida."
            success = false
            return
        }

        // ➤ 3. Converter números
        val frequenciaMinInt = frequenciaMin.filter { it.isDigit() }.toIntOrNull()
        val horasPorAulaInt = horasPorAula.filter { it.isDigit() }.toIntOrNull()

        if (frequenciaMinInt == null || horasPorAulaInt == null) {
            message = "Verifique os campos numéricos."
            success = false
            return
        }

        // ➤ 4. Salvar (se todas validações passaram)
        val operacaoBemSucedida = if (isEditing) {
            MateriaRepository.atualizarMateria(
                idUsuario = userId,
                idDisciplina = subjectData!!.id,
                nome = nomeMateria,
                professor = professor,
                frequenciaMinima = frequenciaMinInt,
                dataInicio = dataInicio,
                dataFim = dataFim,
                horaAula = horasPorAulaInt,
                semestre = semestre
            )
        } else {
            MateriaRepository.insertMateria(
                idUsuario = userId,
                nome = nomeMateria,
                professor = professor,
                frequenciaMinima = frequenciaMinInt,
                dataInicio = dataInicio,
                dataFim = dataFim,
                horaAula = horasPorAulaInt,
                semestre = semestre
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
            painter = painterResource(Res.drawable.star),
            contentDescription = "Decoração estrela",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 600.dp, y = (-150).dp)
                .size(600.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(Res.drawable.chevron),
            contentDescription = "Decoração chevron",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-100).dp, y = 260.dp)
                .size(600.dp),
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
                text = if (isEditing) "Editar Matéria" else "Nova Matéria",
                color = RoxoEscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoRoxo(
                valor = nomeMateria,
                label = "Nome da Matéria",
                onChange = {
                    if (it.length <= 30) nomeMateria = it
                }
            )

            CampoRoxo(
                valor = professor,
                label = "Professor",
                onChange = {
                    if (it.length <= 30) professor = it
                }
            )

            CampoRoxo(
                valor = frequenciaMin,
                label = "Frequência mínima",
                onChange = { entrada ->

                    // Permitir apagar
                    if (entrada.length < frequenciaMin.length) {
                        frequenciaMin = entrada.filter { it.isDigit() }
                        return@CampoRoxo
                    }

                    val digits = entrada.filter { it.isDigit() }.take(2)

                    frequenciaMin = if (digits.length == 2) {
                        "$digits%"
                    } else digits
                }
            )

            CampoRoxoHorasAula(
                valor = horasPorAula,
                label = "Horas por aula",
                onChange = { horasPorAula = it }
            )

            CampoRoxo(
                valor = semestre,
                label = "Semestre",
                onChange = { entrada ->

                    // Permitir apagar
                    if (entrada.length < semestre.length) {
                        semestre = entrada.filter { it.isDigit() }
                        return@CampoRoxo
                    }

                    val digits = entrada.filter { it.isDigit() }.take(2)

                    semestre = if (digits.isNotEmpty()) {
                        digits + "°"
                    } else digits
                }
            )

            CampoRoxoData(
                valor = dataInicio,
                label = "Data de Início (DD/MM/AAAA)",
                onChange = { dataInicio = it }
            )

            CampoRoxoData(
                valor = dataFim,
                label = "Data de Fim (DD/MM/AAAA)",
                onChange = { dataFim = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            message?.let { msg ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth(0.9f)
                            .background(
                                color = if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = msg,
                            color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Button(
                onClick = { salvarMateria() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeLima),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
            ) {
                Text(
                    if (isEditing) "Salvar alterações" else "Cadastrar Matéria",
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
                IconButton(onClick = onOpenMenu) {
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
    }
}
