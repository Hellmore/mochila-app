package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import br.com.mochila.data.UserSession
import br.com.mochila.model.Subject
import br.com.mochila.ui.screens.components.AbsenceLimitWarningIcon
import br.com.mochila.util.AbsenceLimit
import br.com.mochila.model.Task
import br.com.mochila.presenter.HomePresenter
import br.com.mochila.presenter.HomeView
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

private val monthNamesPt = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private fun subjectCardBackground(rgb: Int): Color {
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
private fun SubjectSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.White)
            .border(1.dp, rosa.copy(alpha = 0.6f), RoundedCornerShape(7.dp)),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = Color(0xFF333333)),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (query.isEmpty()) {
                    Text("Buscar matéria", color = Color.Gray, fontSize = 12.sp)
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun SubjectListBottomBar(
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
fun SubjectListScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onOpenMenu: () -> Unit,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubject: (Int) -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var absencesBySubject by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSubjects = remember(subjects, searchQuery) {
        if (searchQuery.isBlank()) subjects
        else subjects.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val presenter = remember {
        object : HomeView {
            override fun showSubjects(list: List<Subject>) {
                subjects = list
            }

            override fun showEmptyState() {
                subjects = emptyList()
            }

            override fun navigateToSubjectDetail(subjectId: Int) {
                onNavigateToSubject(subjectId)
            }

            override fun showPendingTasks(tasks: List<Task>) {
                /* não usado nesta tela */
            }
        }.let { view -> HomePresenter(view) }
    }

    LaunchedEffect(userId) {
        presenter.loadSubjects(userId)
        absencesBySubject = FaltaRepository.countByUser(userId)
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
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            SubjectListDesktopLayout(
                subjects = subjects,
                absencesBySubject = absencesBySubject,
                filteredSubjects = filteredSubjects,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onSubjectClick = { id -> presenter.onSubjectClicked(id) },
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.50f,
            )
            SubjectListMobileLayout(
                subjects = subjects,
                absencesBySubject = absencesBySubject,
                filteredSubjects = filteredSubjects,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                dateLabel = dateLabel,
                onBack = onBack,
                onNavigateToAdd = onNavigateToAdd,
                onOpenMenu = onOpenMenu,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccountSettings = onNavigateToAccountSettings,
                onSubjectClick = { id -> presenter.onSubjectClicked(id) },
            )
        }
    }
}

@Composable
private fun SubjectListMobileLayout(
    subjects: List<Subject>,
    absencesBySubject: Map<Int, Int>,
    filteredSubjects: List<Subject>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onSubjectClick: (Int) -> Unit,
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
                        lineHeight = 20.sp,
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
                        lineHeight = 20.sp,
                    )
                    CalendarGlyph(
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        SubjectSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .padding(top = 12.dp, bottom = 8.dp),
        )

        SubjectGrid(
            subjects = filteredSubjects,
            absencesBySubject = absencesBySubject,
            allSubjectsEmpty = subjects.isEmpty(),
            noSearchResults = subjects.isNotEmpty() && filteredSubjects.isEmpty() && searchQuery.isNotBlank(),
            columns = 2,
            onNavigateToAdd = onNavigateToAdd,
            onSubjectClick = onSubjectClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .padding(bottom = 16.dp),
        )

        SubjectListBottomBar(
            onOpenMenu = onOpenMenu,
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToHome = onNavigateToHome,
        )
    }
}

@Composable
private fun SubjectListDesktopLayout(
    subjects: List<Subject>,
    absencesBySubject: Map<Int, Int>,
    filteredSubjects: List<Subject>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    dateLabel: String,
    onBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onSubjectClick: (Int) -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
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
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .background(fundoTela),
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
                CalendarGlyph(
                    tint = rosa,
                    modifier = Modifier.size(22.dp),
                )
            }

            SubjectSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(bottom = 8.dp),
            )

            SubjectGrid(
                subjects = filteredSubjects,
                absencesBySubject = absencesBySubject,
                allSubjectsEmpty = subjects.isEmpty(),
                noSearchResults = subjects.isNotEmpty() && filteredSubjects.isEmpty() && searchQuery.isNotBlank(),
                columns = 3,
                onNavigateToAdd = onNavigateToAdd,
                onSubjectClick = onSubjectClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(bottom = 8.dp),
            )

            SubjectListBottomBar(
                onOpenMenu = onOpenMenu,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToHome = onNavigateToHome,
            )
        }
        }
    }
}

@Composable
private fun SubjectGrid(
    subjects: List<Subject>,
    absencesBySubject: Map<Int, Int>,
    allSubjectsEmpty: Boolean,
    noSearchResults: Boolean,
    columns: Int,
    onNavigateToAdd: () -> Unit,
    onSubjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (allSubjectsEmpty) {
        val gridSpacing = 16.dp
        BoxWithConstraints(modifier = modifier.verticalScroll(rememberScrollState())) {
            val cellWidth =
                ((maxWidth - gridSpacing * (columns - 1)) / columns).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(24.dp))
                AddSubjectCell(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .width(cellWidth)
                        .heightIn(min = 87.dp),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Nenhuma matéria cadastrada",
                    color = Color.Gray,
                    fontSize = 16.sp,
                )
            }
        }
        return
    }

    if (noSearchResults) {
        BoxWithConstraints(modifier = modifier.verticalScroll(rememberScrollState())) {
            val gridSpacing = 16.dp
            val cellWidth =
                ((maxWidth - gridSpacing * (columns - 1)) / columns).coerceAtLeast(0.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AddSubjectCell(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .width(cellWidth)
                        .heightIn(min = 87.dp),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Nenhuma matéria encontrada",
                    color = Color.Gray,
                    fontSize = 16.sp,
                )
            }
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AddSubjectCell(
                onClick = onNavigateToAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 87.dp),
            )
        }
        itemsIndexed(subjects, key = { _, s -> s.id }) { _, subject ->
            val faltaCount = absencesBySubject[subject.id] ?: 0
            val showWarning = AbsenceLimit.isAtOrOverLimit(faltaCount, subject)
            SubjectCard(
                subject = subject,
                backgroundColor = subjectCardBackground(subject.colorRgb),
                showAbsenceWarning = showWarning,
                onClick = { onSubjectClick(subject.id) },
            )
        }
    }
}

@Composable
private fun AddSubjectCell(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .border(2.dp, rosa, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = "Adicionar matéria",
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(rosa),
        )
    }
}

@Composable
private fun SubjectCard(
    subject: Subject,
    backgroundColor: Color,
    showAbsenceWarning: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 87.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = subject.name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
            )
            if (subject.teacher.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subject.teacher,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp,
                )
            }
        }
        if (showAbsenceWarning) {
            AbsenceLimitWarningIcon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                size = 16.dp,
            )
        }
    }
}
