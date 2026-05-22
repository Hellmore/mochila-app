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
import br.com.mochila.data.EventCategoryCache
import br.com.mochila.data.EventRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.Event
import br.com.mochila.model.EventCategory
import br.com.mochila.model.Subject
import br.com.mochila.presenter.EventListPresenter
import br.com.mochila.presenter.EventListView
import br.com.mochila.ui.screens.components.BackButton
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
private val desktopContentMaxWidth = 600.dp

private val monthNamesPt = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private fun rgbToColor(rgb: Int): Color {
    val r = ((rgb shr 16) and 0xFF) / 255f
    val g = ((rgb shr 8) and 0xFF) / 255f
    val b = (rgb and 0xFF) / 255f
    return Color(r, g, b)
}

private fun formatDatePt(date: LocalDate): String {
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    val mon = monthNamesPt[date.monthNumber - 1]
    val yy = (date.year % 100).toString().padStart(2, '0')
    return "$dd $mon $yy"
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
private fun EventListBottomBar(
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
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
private fun FilterDropdown(
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
            color = laranjaHeader,
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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
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
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun EventInfoTag(
    label: String,
    theme: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .background(theme.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier,
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = label,
            color = theme,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = rgbToColor(event.colorRgb)
    val headerLabel = event.subjectName?.takeIf { it.isNotBlank() } ?: "Sem matéria"
    val dateLabel = EventRepository.formatEventDateForDisplay(event.eventDate)
    val categoryRevision = EventCategoryCache.revision
    var categoryExpanded by remember { mutableStateOf(false) }
    val category = remember(event.id, categoryRevision) { EventCategoryCache.get(event.id) }

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
                text = headerLabel,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(Res.drawable.menu_icon_event),
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
                text = event.title,
                color = theme,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EventInfoTag(label = dateLabel, theme = theme)
                Box {
                    EventInfoTag(
                        label = category.label,
                        theme = theme,
                        onClick = { categoryExpanded = true },
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                    ) {
                        EventCategory.options.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        fontSize = 12.sp,
                                        color = if (option == category) theme else Color(0xFF333333),
                                        fontWeight = if (option == category) FontWeight.Bold else FontWeight.Normal,
                                    )
                                },
                                onClick = {
                                    EventCategoryCache.set(event.id, option)
                                    categoryExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventListScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onOpenMenu: () -> Unit,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEventEdit: (Int) -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var subjectFilter by remember { mutableStateOf("Todos") }
    var monthFilter by remember { mutableStateOf("Todos") }

    val subjectOptions = remember(subjects) {
        listOf("Todos") + subjects.map { it.name }.distinct()
    }
    val monthOptions = remember { listOf("Todos") + monthNamesPt }

    val filterPresenter = remember { EventListPresenter(object : EventListView {
        override fun showEvents(list: List<Event>) {}
        override fun showEmptyState() {}
        override fun navigateToEventEdit(eventId: Int) {}
    }) }

    val categoryRevision = EventCategoryCache.revision

    val filteredEvents = remember(events, subjectFilter, monthFilter, categoryRevision) {
        filterPresenter.filterEvents(events, subjectFilter, monthFilter, monthNamesPt)
    }

    val presenter = remember {
        object : EventListView {
            override fun showEvents(list: List<Event>) { events = list }
            override fun showEmptyState() { events = emptyList() }
            override fun navigateToEventEdit(eventId: Int) { onNavigateToEventEdit(eventId) }
        }.let { EventListPresenter(it) }
    }

    LaunchedEffect(userId) {
        presenter.loadEvents(userId)
        subjects = SubjectRepository.listByUser(userId)
    }

    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val dateLabel = remember(today) { formatDatePt(today) }

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
        val isWide = maxWidth >= 700.dp
        if (isWide) {
            EventListDesktopLayout(
                events = events,
                filteredEvents = filteredEvents,
                subjectFilter = subjectFilter,
                monthFilter = monthFilter,
                subjectOptions = subjectOptions,
                monthOptions = monthOptions,
                onSubjectFilterChange = { subjectFilter = it },
                onMonthFilterChange = { monthFilter = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onEventClick = { presenter.onEventClicked(it) },
            )
        } else {
            EventListMobileLayout(
                events = events,
                filteredEvents = filteredEvents,
                subjectFilter = subjectFilter,
                monthFilter = monthFilter,
                subjectOptions = subjectOptions,
                monthOptions = monthOptions,
                onSubjectFilterChange = { subjectFilter = it },
                onMonthFilterChange = { monthFilter = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onEventClick = { presenter.onEventClicked(it) },
            )
        }
    }
}

@Composable
private fun EventListMobileLayout(
    events: List<Event>,
    filteredEvents: List<Event>,
    subjectFilter: String,
    monthFilter: String,
    subjectOptions: List<String>,
    monthOptions: List<String>,
    onSubjectFilterChange: (String) -> Unit,
    onMonthFilterChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onEventClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()
    Column(Modifier.fillMaxSize()) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FilterDropdown(
                label = "Matérias",
                value = subjectFilter,
                options = subjectOptions,
                onSelect = onSubjectFilterChange,
                accentColor = laranjaHeader,
                modifier = Modifier.weight(1f),
            )
            FilterDropdown(
                label = "Mês",
                value = monthFilter,
                options = monthOptions,
                onSelect = onMonthFilterChange,
                accentColor = rosa,
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            text = "Eventos",
            color = laranjaHeader,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 36.dp, vertical = 4.dp),
        )

        EventListContent(
            events = events,
            filteredEvents = filteredEvents,
            onNavigateToAdd = onNavigateToAdd,
            onEventClick = onEventClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
        )

        EventListBottomBar(
            onOpenMenu = onOpenMenu,
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToHome = onNavigateToHome,
        )
    }
}

@Composable
private fun EventListDesktopLayout(
    events: List<Event>,
    filteredEvents: List<Event>,
    subjectFilter: String,
    monthFilter: String,
    subjectOptions: List<String>,
    monthOptions: List<String>,
    onSubjectFilterChange: (String) -> Unit,
    onMonthFilterChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onEventClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Row(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(0.4f)
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
                )
            }
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.width(8.dp))
                CalendarGlyph(tint = rosa, modifier = Modifier.size(22.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = desktopContentMaxWidth)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 36.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        FilterDropdown(
                            label = "Matérias",
                            value = subjectFilter,
                            options = subjectOptions,
                            onSelect = onSubjectFilterChange,
                            accentColor = laranjaHeader,
                            modifier = Modifier.weight(1f),
                        )
                        FilterDropdown(
                            label = "Mês",
                            value = monthFilter,
                            options = monthOptions,
                            onSelect = onMonthFilterChange,
                            accentColor = rosa,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Text(
                        text = "Eventos",
                        color = laranjaHeader,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )

                    EventListContent(
                        events = events,
                        filteredEvents = filteredEvents,
                        onNavigateToAdd = onNavigateToAdd,
                        onEventClick = onEventClick,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )
                }
            }

            EventListBottomBar(
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToHome = onNavigateToHome,
            )
        }
    }
}

@Composable
private fun EventListContent(
    events: List<Event>,
    filteredEvents: List<Event>,
    onNavigateToAdd: () -> Unit,
    onEventClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()
    val allEmpty = events.isEmpty()
    val noFilterResults = events.isNotEmpty() && filteredEvents.isEmpty()

    Column(
        modifier = modifier.verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AddEventPrompt(onClick = onNavigateToAdd)

        when {
            allEmpty -> {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Nenhum evento cadastrado",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            noFilterResults -> {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Nenhum evento encontrado para os filtros selecionados",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            else -> {
                filteredEvents.forEach { event ->
                    EventCard(event = event, onClick = { onEventClick(event.id) })
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AddEventPrompt(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 71.dp)
            .clip(RoundedCornerShape(7.dp))
            .border(1.dp, rosa.copy(alpha = 0.5f), RoundedCornerShape(7.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = "Adicionar evento",
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(rosa),
        )
    }
}
