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
import br.com.mochila.data.EventRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.DEFAULT_EVENT_COLOR_RGB
import br.com.mochila.model.Event
import br.com.mochila.model.Subject
import br.com.mochila.presenter.EventRegisterPresenter
import br.com.mochila.presenter.EventRegisterView
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

private val eventColorPickerPalette: List<Color> = listOf(
    Color(0xFFFFDE59), Color(0xFFFF6694), Color(0xFFFFBA5E), Color(0xFF65D145), Color(0xFFCB6CE6), Color(0xFF38B6FF),
    Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFFFF5722), Color(0xFF4CAF50), Color(0xFF9C27B0), Color(0xFF2196F3),
    Color(0xFFFFC107), Color(0xFFC2185B), Color(0xFFFF7043), Color(0xFF8BC34A), Color(0xFF7B1FA2), Color(0xFF03A9F4),
    Color(0xFFFDD835), Color(0xFFAD1457), Color(0xFFFFAB91), Color(0xFF689F38), Color(0xFF6A1B9A), Color(0xFF0288D1),
)

private val reminderOptions = listOf(
    null to "nenhum",
    5 to "5 Minutos antes",
    10 to "10 Minutos antes",
    30 to "30 Minutos antes",
    60 to "1 Hora antes",
)

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
private fun CalendarGlyph(tint: Color, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.menu_icon_today),
        contentDescription = "Calendário",
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint),
    )
}

@Composable
private fun EventRegisterBottomBar(
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
            .clickable(interactionSource = scrimInteraction, indication = null, onClick = onDismiss),
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(276.dp)
                .clickable(interactionSource = cardInteraction, indication = null, onClick = {}),
            shape = RoundedCornerShape(23.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).size(36.dp),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.menu_close),
                        contentDescription = "Fechar",
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFB0B0B0)),
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, end = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    eventColorPickerPalette.chunked(6).forEach { rowColors ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 6.dp),
                        ) {
                            rowColors.forEach { c ->
                                val rgb = c.toRgbInt()
                                val selected = rgb == selectedRgb
                                Box(
                                    modifier = Modifier
                                        .size(if (selected) 28.dp else 24.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(
                                            width = if (selected) 2.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape,
                                        )
                                        .clickable { onPick(rgb) },
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
private fun ReminderPickerOverlay(
    visible: Boolean,
    selectedMinutes: Int?,
    onDismiss: () -> Unit,
    onPick: (Int?) -> Unit,
) {
    if (!visible) return
    val scrimInteraction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(11f)
            .background(laranjaHeader.copy(alpha = 0.45f))
            .clickable(interactionSource = scrimInteraction, indication = null, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                reminderOptions.forEach { (minutes, label) ->
                    Text(
                        text = label,
                        color = rosa,
                        fontSize = 14.sp,
                        fontWeight = if (minutes == selectedMinutes) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPick(minutes)
                                onDismiss()
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun EventRegisterScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToEventsList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit = {},
    isEditing: Boolean = false,
    eventId: Int? = null,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var selectedSubjectName by remember { mutableStateOf("Nenhuma") }
    var eventColorRgb by remember { mutableStateOf(DEFAULT_EVENT_COLOR_RGB) }
    var reminderMinutes by remember { mutableStateOf<Int?>(null) }
    var reminderShown by remember { mutableStateOf(false) }
    var loadedEventId by remember { mutableStateOf(0) }
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    val selectedSwatchColor = rgbToColor(eventColorRgb)
    val reminderLabel = reminderOptions.firstOrNull { it.first == reminderMinutes }?.second ?: "nenhum"

    val presenter = remember {
        object : EventRegisterView {
            override fun showEvent(event: Event) {
                loadedEventId = event.id
                title = event.title
                description = event.description.orEmpty()
                eventDate = EventRepository.formatEventDateForDisplay(event.eventDate)
                selectedSubjectId = event.subjectId
                selectedSubjectName = event.subjectName ?: "Nenhuma"
                eventColorRgb = event.colorRgb
                reminderMinutes = event.reminderMinutes
                reminderShown = event.reminderShown
            }

            override fun showValidationError(msg: String) {
                message = msg
                success = false
            }

            override fun showSaveSuccess(isEditing: Boolean) {
                message = if (isEditing) "Evento atualizado com sucesso!" else "Evento cadastrado com sucesso!"
                success = true
            }

            override fun showSaveError() {
                message = "Erro ao salvar evento."
                success = false
            }

            override fun showDeleteSuccess() {
                message = "Evento excluído com sucesso!"
                success = true
            }

            override fun showDeleteError() {
                message = "Erro ao excluir evento."
                success = false
            }

            override fun navigateToEventsList() {
                onNavigateToEventsList()
            }
        }.let { EventRegisterPresenter(it) }
    }

    LaunchedEffect(userId) {
        subjects = SubjectRepository.listByUser(userId)
    }

    LaunchedEffect(eventId, isEditing) {
        if (isEditing && eventId != null) presenter.loadEvent(eventId) else loadedEventId = 0
    }

    val user = UserSession.currentUser
    val displayName = user?.name.orEmpty()
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val dateLabel = remember(today) { formatDatePt(today) }
    fun buildEvent() = Event(
        id = loadedEventId,
        userId = userId,
        title = title,
        description = description.ifBlank { null },
        eventDate = eventDate,
        status = "Agendado",
        subjectId = selectedSubjectId,
        colorRgb = eventColorRgb,
        reminderMinutes = reminderMinutes,
        reminderShown = reminderShown,
    )

    @Composable
    fun FormLabel(text: String) {
        Text(
            text = text,
            color = rosa,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp),
        )
    }

    @Composable
    fun SubjectField() {
        var expanded by remember { mutableStateOf(false) }
        val options = listOf("Nenhuma") + subjects.map { it.name }
        Box(modifier = Modifier.fillMaxWidth()) {
            FieldRoxo(
                valor = selectedSubjectName,
                onChange = {},
                readOnly = true,
                trailing = {
                    IconButton(onClick = { expanded = true }) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(laranjaHeader),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.drop),
                                contentDescription = "Selecionar matéria",
                                modifier = Modifier.size(14.dp),
                                colorFilter = ColorFilter.tint(Color.White),
                            )
                        }
                    }
                },
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { name ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                color = if (name == selectedSubjectName) rosa else Color(0xFF333333),
                            )
                        },
                        onClick = {
                            selectedSubjectName = name
                            selectedSubjectId = if (name == "Nenhuma") null
                            else subjects.firstOrNull { it.name == name }?.id
                            expanded = false
                        },
                    )
                }
            }
        }
    }

    @Composable
    fun ReminderField() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 46.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White)
                .border(1.dp, laranjaHeader, RoundedCornerShape(7.dp))
                .clickable { showReminderPicker = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Lembrete",
                color = laranjaHeader,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "< $reminderLabel >",
                color = rosa,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
            )
        }
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
                    )
                    CalendarGlyph(tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }

    @Composable
    fun FormBody(modifier: Modifier = Modifier, fieldsMaxWidth: Dp? = null) {
        val widthCap = fieldsMaxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = widthCap
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 36.dp, vertical = 20.dp),
            ) {
                Text(
                    text = if (isEditing) "Editar Evento" else "Novo Evento",
                    color = laranjaHeader,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(20.dp))

                FormLabel("Nome do evento:")
                FieldRoxo(
                    valor = title,
                    onChange = { if (it.length <= 50) title = it },
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

                FormLabel("Matéria:")
                SubjectField()

                Spacer(Modifier.height(14.dp))

                FormLabel("Data:")
                CalendarPicker(
                    selectedDate = eventDate,
                    onDateSelected = { eventDate = it },
                    accentColor = laranjaHeader,
                )

                Spacer(Modifier.height(14.dp))

                FormLabel("Descrição:")
                FieldLaranja(
                    valor = description,
                    onChange = { if (it.length <= 200) description = it },
                    minHeight = 100.dp,
                )

                Spacer(Modifier.height(14.dp))

                FormLabel("Lembrete:")
                ReminderField()

                Spacer(Modifier.height(20.dp))

                message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                RoundedCornerShape(12.dp),
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
                    onClick = { presenter.saveEvent(userId, buildEvent(), isEditing) },
                    colors = ButtonDefaults.buttonColors(containerColor = rosa, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    Text("Salvar", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }

                if (isEditing && loadedEventId > 0) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { presenter.deleteEvent(userId, loadedEventId) },
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    ) {
                        Text("Excluir", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(fundoTela),
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
                Box(Modifier.weight(0.4f).fillMaxHeight().background(rosa)) {
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
                            name = displayName,
                            photoPath = user?.photoPath,
                            size = 48.dp,
                            accentColor = Color.White,
                            onClick = onNavigateToAccountSettings,
                        )
                    }
                }
                Column(Modifier.weight(0.6f).fillMaxHeight()) {
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
                        CalendarGlyph(tint = rosa, modifier = Modifier.size(22.dp))
                    }
                    FormBody(modifier = Modifier.weight(1f), fieldsMaxWidth = 600.dp)
                    EventRegisterBottomBar(onOpenMenu = onOpenMenu, onNavigateToHome = onNavigateToHome)
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                OrangeHeaderBar(includeCenterProfile = true)
                FormBody(modifier = Modifier.weight(1f))
                EventRegisterBottomBar(onOpenMenu = onOpenMenu, onNavigateToHome = onNavigateToHome)
            }
        }
    }

    ColorPickerOverlay(
        visible = showColorPicker,
        selectedRgb = eventColorRgb,
        onDismiss = { showColorPicker = false },
        onPick = { eventColorRgb = it },
    )

    ReminderPickerOverlay(
        visible = showReminderPicker,
        selectedMinutes = reminderMinutes,
        onDismiss = { showReminderPicker = false },
        onPick = { minutes ->
            reminderMinutes = minutes
            reminderShown = false
        },
    )
}

@Composable
private fun FieldRoxo(
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 46.dp,
    readOnly: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        readOnly = readOnly,
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
        modifier = modifier.heightIn(min = minHeight).fillMaxWidth(),
    )
}

@Composable
private fun FieldLaranja(
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 100.dp,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = laranjaHeader,
            unfocusedBorderColor = laranjaHeader,
            focusedTextColor = Color(0xFF333333),
            unfocusedTextColor = Color(0xFF333333),
            cursorColor = laranjaHeader,
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color(0xFF333333)),
        shape = RoundedCornerShape(7.dp),
        modifier = modifier.heightIn(min = minHeight).fillMaxWidth(),
    )
}
