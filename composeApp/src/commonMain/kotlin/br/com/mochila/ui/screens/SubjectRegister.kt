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
import br.com.mochila.model.Subject
import br.com.mochila.presenter.SubjectRegisterPresenter
import br.com.mochila.presenter.SubjectRegisterView
import br.com.mochila.ui.screens.components.BackButton
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

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

    // Campos do formulário
    var name by remember { mutableStateOf(subjectData?.name ?: "") }
    var teacher by remember { mutableStateOf(subjectData?.teacher ?: "") }
    var minFrequency by remember { mutableStateOf(if ((subjectData?.minFrequency ?: 0) > 0) "${subjectData!!.minFrequency}%" else "") }
    var startDate by remember { mutableStateOf(subjectData?.startDate ?: "") }
    var endDate by remember { mutableStateOf(subjectData?.endDate ?: "") }
    var classHours by remember { mutableStateOf(if ((subjectData?.classHours ?: 0) > 0) "${subjectData!!.classHours}h" else "") }
    var semester by remember { mutableStateOf(subjectData?.semester ?: "") }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    val presenter = remember {
        object : SubjectRegisterView {
            override fun showValidationError(msg: String) { message = msg; success = false }
            override fun showSaveSuccess(isEditing: Boolean) {
                message = if (isEditing) "Matéria atualizada com sucesso!" else "Matéria cadastrada com sucesso!"
                success = true
            }
            override fun showSaveError() { message = "Erro ao salvar matéria."; success = false }
            override fun navigateToHome() { onNavigateToHome() }
        }.let { view -> SubjectRegisterPresenter(view) }
    }

    @Composable
    fun FieldRoxo(valor: String, label: String, onChange: (String) -> Unit) {
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            label = { Text(text = label, color = RoxoClaro, fontSize = 14.sp) },
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
    fun FieldDate(valor: String, label: String, onChange: (String) -> Unit) {
        FieldRoxo(
            valor = valor,
            label = label,
            onChange = { input ->
                if (input.length < valor.length) { onChange(input); return@FieldRoxo }
                val digits = input.filter { it.isDigit() }.take(8)
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

    fun buildSubject(): Subject {
        val minFrequencyInt = minFrequency.filter { it.isDigit() }.toIntOrNull() ?: 0
        val classHoursInt = classHours.filter { it.isDigit() }.toIntOrNull() ?: 0
        return Subject(
            id = subjectData?.id ?: 0,
            name = name,
            teacher = teacher,
            minFrequency = minFrequencyInt,
            startDate = startDate,
            endDate = endDate,
            classHours = classHoursInt,
            semester = semester
        )
    }

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

            FieldRoxo(valor = name, label = "Nome da Matéria") {
                if (it.length <= 30) name = it
            }

            FieldRoxo(valor = teacher, label = "Professor") {
                if (it.length <= 30) teacher = it
            }

            // Frequência mínima com máscara
            FieldRoxo(valor = minFrequency, label = "Frequência mínima") { input ->
                if (input.length < minFrequency.length) {
                    minFrequency = input.filter { it.isDigit() }
                    return@FieldRoxo
                }
                val digits = input.filter { it.isDigit() }.take(2)
                minFrequency = if (digits.length == 2) "$digits%" else digits
            }

            // Horas por aula com máscara
            FieldRoxo(valor = classHours, label = "Horas por aula") { input ->
                if (input.length < classHours.length) {
                    classHours = input.replace("h", "")
                    return@FieldRoxo
                }
                val clean = input.replace("h", "")
                val filtered = buildString {
                    var colonAdded = false
                    clean.forEach {
                        if (it.isDigit()) append(it)
                        else if (it == ':' && !colonAdded) { append(':'); colonAdded = true }
                    }
                }
                if (filtered.length > 4) return@FieldRoxo
                classHours = if (filtered.isNotEmpty()) "${filtered}h" else filtered
            }

            // Semestre com máscara
            FieldRoxo(valor = semester, label = "Semestre") { input ->
                if (input.length < semester.length) {
                    semester = input.filter { it.isDigit() }
                    return@FieldRoxo
                }
                val digits = input.filter { it.isDigit() }.take(2)
                semester = if (digits.isNotEmpty()) "$digits°" else digits
            }

            FieldDate(valor = startDate, label = "Data de Início (DD/MM/AAAA)") { startDate = it }
            FieldDate(valor = endDate, label = "Data de Fim (DD/MM/AAAA)") { endDate = it }

            Spacer(modifier = Modifier.height(12.dp))

            // Mensagem de feedback
            message?.let { msg ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
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

            // Botão Salvar
            Button(
                onClick = { presenter.saveSubject(userId, buildSubject(), isEditing) },
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

            // Botão Excluir (apenas em modo edição)
            if (isEditing && subjectData != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { presenter.deleteSubject(userId, subjectData.id) },
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

        // Menu inferior fixo
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