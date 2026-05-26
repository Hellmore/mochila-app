@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.DEFAULT_SUBJECT_COLOR_RGB
import br.com.mochila.model.Subject
import br.com.mochila.presenter.SubjectRegisterPresenter
import br.com.mochila.presenter.SubjectRegisterView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.CalendarPicker
import br.com.mochila.ui.screens.components.ProfileAvatar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val fundoTela = Color(0xFFF8F8F8)
private val laranjaHeader = Color(0xFFFFBA5E)
private val rosa = Color(0xFFFF6694)

private val monthNamesPt = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

/** Grade 4×6 — tons alinhados ao app e ao [Seletor de cores](https://www.figma.com/design/UlejWl0a4xUohJj42JbzuQ/App---Mochila?node-id=78-202). */
private val subjectColorPickerPalette: List<Color> = listOf(
    Color(0xFFFFDE59), Color(0xFFFF6694), Color(0xFFFFBA5E), Color(0xFF65D145), Color(0xFFCB6CE6), Color(0xFF38B6FF),
    Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFFFF5722), Color(0xFF4CAF50), Color(0xFF9C27B0), Color(0xFF2196F3),
    Color(0xFFFFC107), Color(0xFFC2185B), Color(0xFFFF7043), Color(0xFF8BC34A), Color(0xFF7B1FA2), Color(0xFF03A9F4),
    Color(0xFFFDD835), Color(0xFFAD1457), Color(0xFFFFAB91), Color(0xFF689F38), Color(0xFF6A1B9A), Color(0xFF0288D1),
)

private fun minutesToDisplay(totalMinutes: Int): String {
    if (totalMinutes <= 0) return ""
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return if (m == 0) "${h}h" else "$h:${m.toString().padStart(2, '0')}h"
}

private fun displayToMinutes(display: String): Int {
    val clean = display.replace("h", "").trim()
    return if (clean.contains(":")) {
        val parts = clean.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        h * 60 + m
    } else {
        (clean.toIntOrNull() ?: 0) * 60
    }
}

private fun formatDatePt(date: LocalDate): String {
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    val mon = monthNamesPt[date.monthNumber - 1]
    val yy = (date.year % 100).toString().padStart(2, '0')
    return "$dd $mon $yy"
}

private fun Color.toRgbInt(): Int {
    val r = (red.coerceIn(0f, 1f) * 255f).toInt() and 0xFF
    val g = (green.coerceIn(0f, 1f) * 255f).toInt() and 0xFF
    val b = (blue.coerceIn(0f, 1f) * 255f).toInt() and 0xFF
    return (r shl 16) or (g shl 8) or b
}

private fun rgbToColor(rgb: Int): Color {
    val r = ((rgb shr 16) and 0xFF) / 255f
    val g = ((rgb shr 8) and 0xFF) / 255f
    val b = (rgb and 0xFF) / 255f
    return Color(r, g, b)
}

@Composable
private fun CalendarGlyph(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.menu_icon_today),
        contentDescription = "Calendário",
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint),
    )
}

@Composable
private fun SubjectRegisterBottomBar(
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .background(rosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onOpenMenu) {
                Image(
                    painter = painterResource(Res.drawable.menu),
                    contentDescription = "Menu lateral",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            IconButton(onClick = onNavigateToHome) {
                Image(
                    painter = painterResource(Res.drawable.home),
                    contentDescription = "Home",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
    }
}

@Composable
private fun ColorPickerOverlay(
    visible: Boolean,
    selectedRgb: Int,
    onDismiss: () -> Unit,
    onPick: (Int) -> Unit,
) {
    if (!visible) return
    val scrimInteraction = remember { MutableInteractionSource() }
    val cardInteraction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)
            .background(Color.Black.copy(alpha = 0.32f))
            .clickable(
                interactionSource = scrimInteraction,
                indication = null,
                onClick = onDismiss,
            ),
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(276.dp)
                .clickable(
                    interactionSource = cardInteraction,
                    indication = null,
                    onClick = {},
                ),
            shape = RoundedCornerShape(23.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.menu_close),
                        contentDescription = "Fechar",
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFB0B0B0)),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    subjectColorPickerPalette.chunked(6).forEach { rowColors ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp),
                        ) {
                            rowColors.forEach { c ->
                                val rgb = c.toRgbInt()
                                val selected = rgb == selectedRgb
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .then(
                                            if (selected) {
                                                Modifier.border(2.dp, rosa, CircleShape)
                                            } else {
                                                Modifier.border(1.dp, Color(0x1A000000), CircleShape)
                                            },
                                        )
                                        .clickable {
                                            onPick(rgb)
                                            onDismiss()
                                        },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopProfileStrip(
    onNavigateToAccountSettings: () -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(rosa),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo Mochila Hub",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Mochila Hub", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text("Organize sua vida acadêmica", color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(
                name = name,
                photoPath = user?.photoPath,
                size = 48.dp,
                accentColor = Color.White,
                onClick = onNavigateToAccountSettings,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = name.ifBlank { " " },
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun SubjectRegisterScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToAccountSettings: () -> Unit = {},
    isEditing: Boolean = false,
    subjectId: Int? = null,
) {
    var name by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var minFrequency by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var weeklyFrequency by remember { mutableStateOf("") }
    var classHours by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var subjectColorRgb by remember { mutableStateOf(DEFAULT_SUBJECT_COLOR_RGB) }
    var showColorPicker by remember { mutableStateOf(false) }

    var loadedSubjectId by remember { mutableStateOf(0) }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var semesterOptions by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(userId) {
        semesterOptions = SubjectRepository.listDistinctSemesters(userId)
    }

    fun updateSemesterInput(input: String) {
        if (input.length < semester.length) {
            semester = input.filter { it.isDigit() }
            return
        }
        val digits = input.filter { it.isDigit() }.take(2)
        semester = if (digits.isNotEmpty()) "$digits°" else digits
    }

    val presenter = remember {
        object : SubjectRegisterView {
            override fun showValidationError(msg: String) {
                message = msg
                success = false
            }

            override fun showSaveSuccess(isEditing: Boolean) {
                message = if (isEditing) "Matéria atualizada com sucesso!" else "Matéria cadastrada com sucesso!"
                success = true
            }

            override fun showSaveError() {
                message = "Erro ao salvar matéria."
                success = false
            }

            override fun navigateToHome() {
                onNavigateToHome()
            }
        }.let { SubjectRegisterPresenter(it) }
    }

    LaunchedEffect(subjectId, isEditing) {
        if (isEditing && subjectId != null) {
            val subject = presenter.loadSubjectForEdit(subjectId)
            subject?.let { s ->
                loadedSubjectId = s.id
                name = s.name
                teacher = s.teacher
                minFrequency = if (s.minFrequency > 0) "${s.minFrequency}%" else ""
                startDate = s.startDate
                endDate = s.endDate
                weeklyFrequency = s.weeklyClasses.toString()
                classHours = minutesToDisplay(s.classHours)
                semester = s.semester
                subjectColorRgb = s.colorRgb
            }
        } else {
            loadedSubjectId = 0
            subjectColorRgb = DEFAULT_SUBJECT_COLOR_RGB
        }
    }

    val user = UserSession.currentUser
    val displayName = user?.name.orEmpty()
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val dateLabel = remember(today) { formatDatePt(today) }

    val selectedSwatchColor = remember(subjectColorRgb) { rgbToColor(subjectColorRgb) }

    @Composable
    fun FormLabel(text: String) {
        Text(
            text = text,
            color = rosa,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 6.dp),
        )
    }

    fun buildSubject(): Subject {
        val minFrequencyInt = minFrequency.filter { it.isDigit() }.toIntOrNull() ?: 0
        val classHoursInt = displayToMinutes(classHours)
        val weeklyClassesInt = weeklyFrequency.filter { it.isDigit() }.toIntOrNull() ?: 0
        return Subject(
            id = loadedSubjectId,
            name = name,
            teacher = teacher,
            minFrequency = minFrequencyInt,
            startDate = startDate,
            endDate = endDate,
            weeklyClasses = weeklyClassesInt,
            classHours = classHoursInt,
            semester = semester,
            colorRgb = subjectColorRgb,
        )
    }

    @Composable
    fun OrangeHeaderBar(includeCenterProfile: Boolean) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(laranjaHeader)
                .padding(vertical = 20.dp, horizontal = 22.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BackButton(
                        onBack = onBack,
                        backgroundColor = Color.Transparent,
                        iconTint = Color.White,
                        buttonSize = 40.dp,
                        iconSize = 22.dp,
                    )
                    if (includeCenterProfile) {
                        ProfileAvatar(
                            name = displayName,
                            photoPath = user?.photoPath,
                            size = 40.dp,
                            accentColor = Color.White,
                            onClick = onNavigateToAccountSettings,
                        )
                        Text(
                            text = displayName.ifBlank { " " },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 120.dp),
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = dateLabel,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                    )
                    CalendarGlyph(
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }

    @Composable
    fun SemesterField() {
        var pickerExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FieldRoxo(
                valor = semester,
                onChange = ::updateSemesterInput,
                modifier = Modifier.weight(1f),
            )
            if (semesterOptions.isNotEmpty()) {
                Box {
                    IconButton(onClick = { pickerExpanded = true }) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(laranjaHeader),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.drop),
                                contentDescription = "Selecionar semestre",
                                modifier = Modifier.size(14.dp),
                                colorFilter = ColorFilter.tint(Color.White),
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = pickerExpanded,
                        onDismissRequest = { pickerExpanded = false },
                    ) {
                        semesterOptions.forEach { choice ->
                            DropdownMenuItem(
                                text = { Text(choice) },
                                onClick = {
                                    semester = choice
                                    pickerExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun FormBody(modifier: Modifier = Modifier, fieldsMaxWidth: Dp? = null) {
        val widthCap = fieldsMaxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = widthCap
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 36.dp, vertical = 20.dp),
            ) {
            Text(
                text = if (isEditing) "Editar Matéria" else "Nova Matéria",
                color = laranjaHeader,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(20.dp))

            FormLabel("Nome da matéria:")
            FieldRoxo(
                valor = name,
                onChange = { if (it.length <= 60) name = it },
                trailing = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(selectedSwatchColor)
                            .border(1.dp, rosa.copy(alpha = 0.5f), CircleShape)
                            .clickable { showColorPicker = true },
                    )
                },
            )

            Spacer(Modifier.height(14.dp))

            FormLabel("Professor:")
            FieldRoxo(valor = teacher, onChange = { if (it.length <= 30) teacher = it })

            Spacer(Modifier.height(14.dp))

            FormLabel("Semestre:")
            SemesterField()

            Spacer(Modifier.height(14.dp))

            FormLabel("Frequência Mínima (Em porcentagem)")
            FieldRoxo(
                valor = minFrequency,
                onChange = { input ->
                    if (input.length < minFrequency.length) {
                        minFrequency = input.filter { it.isDigit() }
                        return@FieldRoxo
                    }
                    val digits = input.filter { it.isDigit() }.take(2)
                    minFrequency = if (digits.length == 2) "$digits%" else digits
                },
                minHeight = 42.dp,
            )

            Spacer(Modifier.height(14.dp))

            FormLabel("Data de Início")
            CalendarPicker(
                selectedDate = startDate,
                onDateSelected = { startDate = it },
                accentColor = rosa,
            )

            Spacer(Modifier.height(14.dp))

            FormLabel("Data de Fim")
            CalendarPicker(
                selectedDate = endDate,
                onDateSelected = { endDate = it },
                accentColor = rosa,
            )

            Spacer(Modifier.height(14.dp))

            FormLabel("Frequência (aulas por semana)")
            FieldRoxo(
                valor = weeklyFrequency,
                onChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(2)
                    weeklyFrequency = digits
                },
                minHeight = 42.dp,
            )

            Spacer(Modifier.height(14.dp))

            FormLabel("Horas por Aula")
            FieldRoxo(
                valor = classHours,
                onChange = { input ->
                    if (input.length < classHours.length) {
                        classHours = input.replace("h", "")
                        return@FieldRoxo
                    }
                    val clean = input.replace("h", "")
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
                    if (filtered.length > 4) return@FieldRoxo
                    classHours = if (filtered.isNotEmpty()) "${filtered}h" else filtered
                },
                minHeight = 42.dp,
            )

            Spacer(Modifier.height(20.dp))

            message?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        text = msg,
                        color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                        fontSize = 15.sp,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    presenter.saveSubject(userId, buildSubject(), isEditing)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = rosa,
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
            ) {
                Text(
                    text = "Salvar",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
            }

            if (isEditing && loadedSubjectId > 0) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                ) {
                    Text("Excluir", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text("Excluir matéria", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text("Tem certeza que deseja excluir esta matéria? Todas as tarefas, faltas e eventos vinculados também serão removidos.", fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        presenter.deleteSubject(userId, loadedSubjectId)
                    },
                ) {
                    Text("Excluir", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar", color = rosa)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White,
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.50f,
        )
        val wide = maxWidth >= 700.dp
        if (wide) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.weight(0.4f).fillMaxHeight()) {
                    DesktopProfileStrip(onNavigateToAccountSettings = onNavigateToAccountSettings)
                }
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BackButton(
                            onBack = onBack,
                            backgroundColor = rosa.copy(alpha = 0.92f),
                            iconTint = Color.White,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = dateLabel,
                            color = Color(0xFF333333),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(6.dp))
                        CalendarGlyph(
                            tint = rosa,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    FormBody(modifier = Modifier.weight(1f), fieldsMaxWidth = 600.dp)
                    SubjectRegisterBottomBar(
                        onOpenMenu = onOpenMenu,
                        onNavigateToHome = onNavigateToHome,
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                OrangeHeaderBar(includeCenterProfile = true)
                FormBody(modifier = Modifier.weight(1f))
                SubjectRegisterBottomBar(
                    onOpenMenu = onOpenMenu,
                    onNavigateToHome = onNavigateToHome,
                )
            }
        }

        ColorPickerOverlay(
            visible = showColorPicker,
            selectedRgb = subjectColorRgb,
            onDismiss = { showColorPicker = false },
            onPick = { subjectColorRgb = it },
        )
    }
}

@Composable
private fun FieldRoxo(
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 46.dp,
    trailing: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        singleLine = true,
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = rosa,
            unfocusedBorderColor = rosa,
            focusedTextColor = rosa,
            unfocusedTextColor = rosa,
            cursorColor = rosa,
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraLight,
            color = rosa,
        ),
        shape = RoundedCornerShape(7.dp),
        modifier = modifier
            .heightIn(min = minHeight)
            .fillMaxWidth(),
    )
}
