package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.TaskRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.Subject
import br.com.mochila.model.Task
import br.com.mochila.ui.screens.components.AbsenceLimitWarningIcon
import br.com.mochila.ui.screens.components.ProfileAvatar
import br.com.mochila.util.AbsenceLimit
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val homeBg = Color(0xFFF8F8F8)
private val orange = Color(0xFFFFBA5E)
private val pink = Color(0xFFFF6694)

private val taskRowColors = listOf(
    Color(0xFFEE74C1).copy(alpha = 0.4f),
    Color(0xFFFFBA5E).copy(alpha = 0.4f),
    Color(0xFF65D145).copy(alpha = 0.4f),
)
private val taskTextColors = listOf(
    Color(0xFFFF6694),
    Color(0xFFEC9C30),
    Color(0xFF35841D).copy(alpha = 0.4f),
)
private val absenceCardColors = listOf(
    Color(0xFFFF6694).copy(alpha = 0.8f),
    Color(0xFFFFBA5E),
    Color(0xFF65D145).copy(alpha = 0.4f),
)

private val monthNamesPt = listOf(
    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
    "Jul", "Ago", "Set", "Out", "Nov", "Dez",
)

@Composable
fun HomeScreen(
    userId: Int,
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubjectsList: () -> Unit,
    onNavigateToSubject: (Int) -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val todayDayIndex = remember(today) { today.dayOfWeek.ordinal }

    var selectedDayIndex by remember { mutableStateOf(todayDayIndex) }
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var absencesBySubject by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

    LaunchedEffect(userId) {
        subjects = SubjectRepository.listByUser(userId)
        tasks = TaskRepository.listByUser(userId)
            .filter { it.status == "Pendente" || it.status == "Em andamento" }
        absencesBySubject = FaltaRepository.countByUser(userId)
    }

    val weekDays = remember(today) {
        val monday = today.minus(todayDayIndex, DateTimeUnit.DAY)
        (0..6).map { monday.plus(it, DateTimeUnit.DAY) }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(homeBg),
    ) {
        if (maxWidth >= 700.dp) {
            HomeDesktopLayout(
                today = today,
                weekDays = weekDays,
                todayDayIndex = todayDayIndex,
                selectedDayIndex = selectedDayIndex,
                onDaySelected = { selectedDayIndex = it },
                subjects = subjects,
                tasks = tasks,
                absencesBySubject = absencesBySubject,
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToSubjectsList = onNavigateToSubjectsList,
                onNavigateToSubject = onNavigateToSubject,
                onNavigateToTasksList = onNavigateToTasksList,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.50f,
            )
            HomeMobileLayout(
                today = today,
                weekDays = weekDays,
                todayDayIndex = todayDayIndex,
                selectedDayIndex = selectedDayIndex,
                onDaySelected = { selectedDayIndex = it },
                subjects = subjects,
                tasks = tasks,
                absencesBySubject = absencesBySubject,
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToSubjectsList = onNavigateToSubjectsList,
                onNavigateToSubject = onNavigateToSubject,
                onNavigateToTasksList = onNavigateToTasksList,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
            )
        }
    }
}

@Composable
private fun HomeMobileLayout(
    today: LocalDate,
    weekDays: List<LocalDate>,
    todayDayIndex: Int,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    subjects: List<Subject>,
    tasks: List<Task>,
    absencesBySubject: Map<Int, Int>,
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubjectsList: () -> Unit,
    onNavigateToSubject: (Int) -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        HomeMobileHeader(
            today = today,
            onNavigateToAccountSettings = onNavigateToAccountSettings,
        )

        HomeDashboardContent(
            weekDays = weekDays,
            todayDayIndex = todayDayIndex,
            selectedDayIndex = selectedDayIndex,
            onDaySelected = onDaySelected,
            subjects = subjects,
            tasks = tasks,
            absencesBySubject = absencesBySubject,
            onNavigateToSubjectsList = onNavigateToSubjectsList,
            onNavigateToSubject = onNavigateToSubject,
            onNavigateToTasksList = onNavigateToTasksList,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
        )

        HomeBottomBar(
            onOpenMenu = onOpenMenu,
            onNavigateToAdd = onNavigateToAdd,
        )
    }
}

@Composable
private fun HomeDesktopLayout(
    today: LocalDate,
    weekDays: List<LocalDate>,
    todayDayIndex: Int,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    subjects: List<Subject>,
    tasks: List<Task>,
    absencesBySubject: Map<Int, Int>,
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubjectsList: () -> Unit,
    onNavigateToSubject: (Int) -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()
    val dateLabel = "${today.dayOfMonth.toString().padStart(2, '0')} ${monthNamesPt[today.monthNumber - 1]} ${(today.year % 100).toString().padStart(2, '0')}"

    Row(Modifier.fillMaxSize()) {
        // Sidebar permanente
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(pink),
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Área de conteúdo
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .background(homeBg),
        ) {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.50f,
            )
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
            // Cabeçalho desktop: data + calendário
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = dateLabel,
                    color = Color(0xFF333333),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(Res.drawable.menu_icon_today),
                    contentDescription = "Calendário",
                    modifier = Modifier.size(22.dp),
                    colorFilter = ColorFilter.tint(orange),
                )
            }

            HomeDashboardContent(
                weekDays = weekDays,
                todayDayIndex = todayDayIndex,
                selectedDayIndex = selectedDayIndex,
                onDaySelected = onDaySelected,
                subjects = subjects,
                tasks = tasks,
                absencesBySubject = absencesBySubject,
                onNavigateToSubjectsList = onNavigateToSubjectsList,
                onNavigateToSubject = onNavigateToSubject,
                onNavigateToTasksList = onNavigateToTasksList,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
            )

            HomeBottomBar(
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
            )
        }
        }
    }
}

@Composable
private fun HomeDashboardContent(
    weekDays: List<LocalDate>,
    todayDayIndex: Int,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    subjects: List<Subject>,
    tasks: List<Task>,
    absencesBySubject: Map<Int, Int>,
    onNavigateToSubjectsList: () -> Unit,
    onNavigateToSubject: (Int) -> Unit,
    onNavigateToTasksList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Atividades de Hoje",
                    color = orange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                AtividadesCard(
                    weekDays = weekDays,
                    selectedDay = selectedDayIndex,
                    onDaySelected = onDaySelected,
                    tasks = tasks.take(3),
                    onVerMais = onNavigateToTasksList,
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Controle de Faltas",
                    color = orange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                FaltasRow(
                    subjects = subjects,
                    absences = absencesBySubject,
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Matérias",
                        color = orange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "ver mais",
                        color = orange,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { onNavigateToSubjectsList() },
                    )
                }
                if (subjects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Nenhuma matéria cadastrada", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        subjects.forEach { subject ->
                            val faltaCount = absencesBySubject[subject.id] ?: 0
                            SubjectRowItem(
                                subject = subject,
                                showAbsenceWarning = AbsenceLimit.isAtOrOverLimit(faltaCount, subject),
                                onClick = { onNavigateToSubject(subject.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeMobileHeader(
    today: LocalDate,
    onNavigateToAccountSettings: () -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()
    val dateLabel = "${today.dayOfMonth.toString().padStart(2, '0')} ${monthNamesPt[today.monthNumber - 1]} ${(today.year % 100).toString().padStart(2, '0')}"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(orange)
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
                ProfileAvatar(
                    name = name,
                    photoPath = user?.photoPath,
                    size = 43.dp,
                    accentColor = Color.White,
                    onClick = onNavigateToAccountSettings,
                )
                Text(
                    text = name.ifBlank { " " },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 130.dp),
                )
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
                Image(
                    painter = painterResource(Res.drawable.menu_icon_today),
                    contentDescription = "Calendário",
                    modifier = Modifier.size(22.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
    }
}

@Composable
private fun AtividadesCard(
    weekDays: List<LocalDate>,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    tasks: List<Task>,
    onVerMais: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, orange, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(54.dp))
                .background(orange)
                .padding(vertical = 6.dp, horizontal = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                weekDays.forEachIndexed { index, day ->
                    val isSelected = index == selectedDay
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) pink else Color.White)
                            .clickable { onDaySelected(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = day.dayOfMonth.toString(),
                            color = if (isSelected) Color.White else pink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Sem atividades pendentes", color = orange, fontSize = 11.sp)
            }
        } else {
            tasks.forEachIndexed { index, task ->
                val statusPriorityLine = remember(task.id, task.status, task.priority) {
                    val status = when (task.status) {
                        "Concluida" -> "Concluída"
                        else -> task.status.ifBlank { "—" }
                    }
                    "$status | ${task.priority.label}"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(taskRowColors[index % taskRowColors.size])
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            color = taskTextColors[index % taskTextColors.size],
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = statusPriorityLine,
                            color = taskTextColors[index % taskTextColors.size].copy(alpha = 0.7f),
                            fontSize = 7.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(1.dp, taskTextColors[index % taskTextColors.size], CircleShape),
                    )
                }
            }
        }

        Text(
            text = "ver mais",
            color = orange,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onVerMais() }
                .padding(top = 4.dp),
        )
    }
}

@Composable
private fun FaltasRow(
    subjects: List<Subject>,
    absences: Map<Int, Int>,
) {
    if (subjects.isEmpty()) {
        Text("Sem matérias cadastradas", color = Color.Gray, fontSize = 12.sp)
        return
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        itemsIndexed(subjects) { index, subject ->
            val faltaCount = absences[subject.id] ?: 0
            val maxFaltas = AbsenceLimit.maxAllowedAbsences(subject)
            val pct = (faltaCount.toFloat() / maxFaltas.toFloat() * 100)
                .toInt().coerceIn(0, 100)
            val isWarning = AbsenceLimit.isAtOrOverLimit(faltaCount, subject)
            val cardColor = absenceCardColors[index % absenceCardColors.size]

            Column(
                modifier = Modifier
                    .width(117.dp)
                    .height(87.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(cardColor)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = subject.name,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp,
                    letterSpacing = 0.6.sp,
                )
                Text(
                    text = "$faltaCount/$maxFaltas",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$pct%",
                        color = Color.Black.copy(alpha = 0.72f),
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.6.sp,
                    )
                    if (isWarning) {
                        Spacer(Modifier.width(4.dp))
                        AbsenceLimitWarningIcon()
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectRowItem(
    subject: Subject,
    showAbsenceWarning: Boolean,
    onClick: () -> Unit,
) {
    val r = ((subject.colorRgb shr 16) and 0xFF) / 255f
    val g = ((subject.colorRgb shr 8) and 0xFF) / 255f
    val b = (subject.colorRgb and 0xFF) / 255f
    val cardColor = Color(r, g, b)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(cardColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = subject.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (showAbsenceWarning) {
                AbsenceLimitWarningIcon(size = 14.dp)
            }
            if (subject.semester.isNotBlank()) {
                Text(
                    text = subject.semester,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(38.dp))
                .border(0.5.dp, Color.LightGray, RoundedCornerShape(38.dp))
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onOpenMenu) {
                Image(
                    painter = painterResource(Res.drawable.menu),
                    contentDescription = "Menu lateral",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(pink),
                )
            }
            IconButton(onClick = onNavigateToAdd) {
                Image(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = "Adicionar",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(pink),
                )
            }
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(Res.drawable.home),
                    contentDescription = "Início",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(pink),
                )
            }
        }
    }
}
