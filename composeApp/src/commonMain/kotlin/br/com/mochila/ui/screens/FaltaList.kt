@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.Falta
import br.com.mochila.model.Subject
import br.com.mochila.presenter.FaltaListPresenter
import br.com.mochila.presenter.FaltaListView
import br.com.mochila.ui.screens.components.AbsenceLimitWarningIcon
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import br.com.mochila.util.AbsenceLimit
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Paleta da lista de faltas
private val faltaFundoTela = Color(0xFFF8F8F8)
private val faltaLaranja = Color(0xFFFFBA5E)
private val faltaRosa = Color(0xFFFF6694)
private val faltaDesktopMaxWidth = 600.dp

private val statusDisplayOptions = listOf("Todos", "Registrada", "Justificada", "Não Justificada")

private val statusColors = mapOf(
    "Registrada" to Color(0xFFFFBA5E),
    "Justificada" to Color(0xFF4CAF50),
    "Nao Justificada" to Color(0xFFD32F2F),
)

private fun faltaRgbToColor(rgb: Int): Color {
    val r = ((rgb shr 16) and 0xFF) / 255f
    val g = ((rgb shr 8) and 0xFF) / 255f
    val b = (rgb and 0xFF) / 255f
    return Color(r, g, b)
}

private val faltaMonthNamesPt = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private fun faltaFormatDatePt(date: LocalDate): String {
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    val mon = faltaMonthNamesPt[date.monthNumber - 1]
    val yy = (date.year % 100).toString().padStart(2, '0')
    return "$dd $mon $yy"
}

private fun statusDisplayName(dbStatus: String) = if (dbStatus == "Nao Justificada") "Não Justificada" else dbStatus

@Composable
private fun FaltaListBottomBar(
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .background(faltaRosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
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
            IconButton(onClick = onNavigateToAdd) {
                Image(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = "Adicionar",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            IconButton(onClick = onNavigateToHome) {
                Image(
                    painter = painterResource(Res.drawable.home),
                    contentDescription = "Início",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
    }
}

@Composable
private fun FaltaFilterDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = label,
            color = faltaLaranja,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.White)
                    .border(1.dp, accentColor, RoundedCornerShape(7.dp))
                    .clickable { expanded = true }
                    .padding(start = 10.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor)
                        .clickable { expanded = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.drop),
                        contentDescription = "Abrir",
                        modifier = Modifier.size(14.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = if (option == value) accentColor else Color(0xFF333333),
                                fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = { onSelect(option); expanded = false },
                    )
                }
            }
        }
    }
}

// Card de falta com aviso de limite na disciplina
@Composable
private fun FaltaCard(
    falta: Falta,
    showAbsenceWarning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = faltaRgbToColor(falta.subjectColorRgb)
    val subjectLabel = falta.subjectName?.takeIf { it.isNotBlank() } ?: "Sem matéria"
    val dateLabel = FaltaRepository.formatDateForDisplay(falta.date)
    val statusLabel = statusDisplayName(falta.status)
    val statusChipColor = statusColors[falta.status] ?: faltaRosa

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(7.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(theme)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = subjectLabel,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (showAbsenceWarning) {
                AbsenceLimitWarningIcon(size = 14.dp)
                Spacer(Modifier.width(6.dp))
            }
            Image(
                painter = painterResource(Res.drawable.menu_icon_today),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(Color.White),
            )
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(Res.drawable.chevron),
                contentDescription = "Abrir",
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(Color.White),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 45.dp)
                .background(Color.White)
                .border(1.dp, theme, RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateLabel,
                color = theme,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .background(statusChipColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = statusLabel,
                    color = statusChipColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// Tela principal do registro de faltas
@Composable
fun FaltaListScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onOpenMenu: () -> Unit,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToFaltaDetail: (Int) -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit,
    initialSubjectFilter: String = "Todos",
) {
    var faltas by remember { mutableStateOf<List<Falta>>(emptyList()) }
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var absencesBySubject by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var subjectFilter by remember { mutableStateOf(initialSubjectFilter) }
    var statusFilter by remember { mutableStateOf("Todos") }

    val subjectOptions = remember(subjects) {
        listOf("Todos") + subjects.map { it.name }.distinct()
    }

    val filterPresenter = remember {
        FaltaListPresenter(object : FaltaListView {
            override fun showFaltas(faltas: List<Falta>) {}
            override fun showEmptyState() {}
            override fun navigateToFaltaDetail(faltaId: Int) {}
        })
    }

    val filteredFaltas = remember(faltas, subjectFilter, statusFilter) {
        filterPresenter.filterFaltas(faltas, subjectFilter, statusFilter)
    }

    val presenter = remember {
        object : FaltaListView {
            override fun showFaltas(list: List<Falta>) { faltas = list }
            override fun showEmptyState() { faltas = emptyList() }
            override fun navigateToFaltaDetail(faltaId: Int) { onNavigateToFaltaDetail(faltaId) }
        }.let { FaltaListPresenter(it) }
    }

    val subjectsAtLimit = remember(subjects, absencesBySubject) {
        subjects.filter { subject ->
            val count = absencesBySubject[subject.id] ?: 0
            AbsenceLimit.isAtOrOverLimit(count, subject)
        }.map { it.id }.toSet()
    }

    LaunchedEffect(userId) {
        presenter.loadFaltas(userId)
        subjects = SubjectRepository.listByUser(userId)
        absencesBySubject = FaltaRepository.countByUser(userId)
    }

    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val dateLabel = remember(today) { faltaFormatDatePt(today) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(faltaFundoTela),
    ) {
        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.50f,
        )
        val isWide = maxWidth >= 700.dp
        if (isWide) {
            FaltaListDesktopLayout(
                faltas = faltas,
                subjectsAtLimit = subjectsAtLimit,
                filteredFaltas = filteredFaltas,
                subjectFilter = subjectFilter,
                statusFilter = statusFilter,
                subjectOptions = subjectOptions,
                onSubjectFilterChange = { subjectFilter = it },
                onStatusFilterChange = { statusFilter = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onFaltaClick = { presenter.onFaltaClicked(it) },
            )
        } else {
            FaltaListMobileLayout(
                faltas = faltas,
                subjectsAtLimit = subjectsAtLimit,
                filteredFaltas = filteredFaltas,
                subjectFilter = subjectFilter,
                statusFilter = statusFilter,
                subjectOptions = subjectOptions,
                onSubjectFilterChange = { subjectFilter = it },
                onStatusFilterChange = { statusFilter = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onFaltaClick = { presenter.onFaltaClicked(it) },
            )
        }
    }
}

@Composable
private fun FaltaListMobileLayout(
    faltas: List<Falta>,
    subjectsAtLimit: Set<Int>,
    filteredFaltas: List<Falta>,
    subjectFilter: String,
    statusFilter: String,
    subjectOptions: List<String>,
    onSubjectFilterChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onFaltaClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(faltaLaranja)
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
                    ProfileAvatar(
                        name = name,
                        photoPath = user?.photoPath,
                        size = 40.dp,
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
                        modifier = Modifier.widthIn(max = 120.dp),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(text = dateLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Image(
                        painter = painterResource(Res.drawable.menu_icon_today),
                        contentDescription = "Calendário",
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FaltaFilterDropdown(
                label = "Matérias",
                value = subjectFilter,
                options = subjectOptions,
                onSelect = onSubjectFilterChange,
                accentColor = faltaLaranja,
                modifier = Modifier.weight(1f),
            )
            FaltaFilterDropdown(
                label = "Status",
                value = statusFilter,
                options = statusDisplayOptions,
                onSelect = onStatusFilterChange,
                accentColor = faltaRosa,
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            text = "Faltas",
            color = faltaLaranja,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 36.dp, vertical = 4.dp),
        )

        FaltaListContent(
            faltas = faltas,
            subjectsAtLimit = subjectsAtLimit,
            filteredFaltas = filteredFaltas,
            onNavigateToAdd = onNavigateToAdd,
            onFaltaClick = onFaltaClick,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 36.dp),
        )

        FaltaListBottomBar(
            onOpenMenu = onOpenMenu,
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToHome = onNavigateToHome,
        )
    }
}

@Composable
private fun FaltaListDesktopLayout(
    faltas: List<Falta>,
    subjectsAtLimit: Set<Int>,
    filteredFaltas: List<Falta>,
    subjectFilter: String,
    statusFilter: String,
    subjectOptions: List<String>,
    onSubjectFilterChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onFaltaClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Row(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(0.4f).fillMaxHeight().background(faltaRosa),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
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
                Text(
                    "Organize sua vida acadêmica",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
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
                )
            }
        }

        Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onBack = onBack, backgroundColor = faltaRosa.copy(alpha = 0.92f), iconTint = Color.White)
                Spacer(Modifier.weight(1f))
                Text(text = dateLabel, color = Color(0xFF333333), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(Res.drawable.menu_icon_today),
                    contentDescription = "Calendário",
                    modifier = Modifier.size(22.dp),
                    colorFilter = ColorFilter.tint(faltaRosa),
                )
            }

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = faltaDesktopMaxWidth)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 36.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        FaltaFilterDropdown(
                            label = "Matérias",
                            value = subjectFilter,
                            options = subjectOptions,
                            onSelect = onSubjectFilterChange,
                            accentColor = faltaLaranja,
                            modifier = Modifier.weight(1f),
                        )
                        FaltaFilterDropdown(
                            label = "Status",
                            value = statusFilter,
                            options = statusDisplayOptions,
                            onSelect = onStatusFilterChange,
                            accentColor = faltaRosa,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Text(
                        text = "Faltas",
                        color = faltaLaranja,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )

                    FaltaListContent(
                        faltas = faltas,
                        subjectsAtLimit = subjectsAtLimit,
                        filteredFaltas = filteredFaltas,
                        onNavigateToAdd = onNavigateToAdd,
                        onFaltaClick = onFaltaClick,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
            }

            FaltaListBottomBar(
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToHome = onNavigateToHome,
            )
        }
    }
}

@Composable
private fun FaltaListContent(
    faltas: List<Falta>,
    subjectsAtLimit: Set<Int>,
    filteredFaltas: List<Falta>,
    onNavigateToAdd: () -> Unit,
    onFaltaClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()
    val allEmpty = faltas.isEmpty()
    val noFilterResults = faltas.isNotEmpty() && filteredFaltas.isEmpty()

    Column(
        modifier = modifier.verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AddFaltaPrompt(onClick = onNavigateToAdd)

        when {
            allEmpty -> {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Nenhuma falta cadastrada",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            noFilterResults -> {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Nenhuma falta encontrada para os filtros selecionados",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            else -> {
                filteredFaltas.forEach { falta ->
                    FaltaCard(
                        falta = falta,
                        showAbsenceWarning = falta.subjectId in subjectsAtLimit,
                        onClick = { onFaltaClick(falta.id) },
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AddFaltaPrompt(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 71.dp)
            .clip(RoundedCornerShape(7.dp))
            .border(1.dp, faltaRosa.copy(alpha = 0.5f), RoundedCornerShape(7.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = "Adicionar falta",
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(faltaRosa),
        )
    }
}
