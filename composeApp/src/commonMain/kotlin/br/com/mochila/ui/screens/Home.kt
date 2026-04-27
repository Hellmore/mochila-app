package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import br.com.mochila.model.Task
import br.com.mochila.presenter.HomePresenter
import br.com.mochila.presenter.HomeView
import br.com.mochila.ui.screens.components.UserAvatarButton
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
@Composable
fun HomeScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubject: (Int) -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    // Estado da View
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var pendingTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var selectedSemester by remember { mutableStateOf("Todos") }
    var searchText by remember { mutableStateOf("") }
    var semesterMenuExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Presenter — implementa HomeView inline e entrega ao Presenter
    val presenter = remember {
        object : HomeView {
            override fun showSubjects(list: List<Subject>) { subjects = list }
            override fun showEmptyState() { subjects = emptyList() }
            override fun navigateToSubjectDetail(subjectId: Int) { onNavigateToSubject(subjectId) }
            // ✅ NOVO: Presenter entrega as tarefas prontas, sem que a View precise buscar
            override fun showPendingTasks(tasks: List<Task>) { pendingTasks = tasks }
        }.let { view -> HomePresenter(view) }
    }

    LaunchedEffect(userId) {
        presenter.loadSubjects(userId)
        presenter.loadPendingTasks(userId) // ✅ chama o Presenter, não o Repository
    }

    val semesters = remember(subjects) { presenter.getSemesters(subjects) }

    val filteredSubjects = remember(subjects, selectedSemester, searchText) {
        presenter.filterSubjects(subjects, selectedSemester, searchText)
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Cabeçalho
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Matérias",
                    color = RoxoEscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                UserAvatarButton(
                    size = 60.dp,
                    onClick = onNavigateToAccountSettings
                )
            }

            // Botões de Filtro
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Pesquisa
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(RoxoClaro)
                            .clickable { isSearchExpanded = !isSearchExpanded }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Pesquisa", color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(Res.drawable.drop),
                                contentDescription = "Abrir filtro",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Botão Semestre
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(RoxoClaro)
                                .clickable { semesterMenuExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (selectedSemester == "Todos") "Semestre" else selectedSemester,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(Res.drawable.drop),
                                    contentDescription = "Selecionar semestre",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = semesterMenuExpanded,
                            onDismissRequest = { semesterMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos") },
                                onClick = {
                                    selectedSemester = "Todos"
                                    semesterMenuExpanded = false
                                }
                            )
                            semesters.forEach { semester ->
                                DropdownMenuItem(
                                    text = { Text(semester) },
                                    onClick = {
                                        selectedSemester = semester
                                        semesterMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (isSearchExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Buscar matéria pelo nome") },
                        singleLine = true
                    )
                }
            }

            // Lista filtrada
            if (filteredSubjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (subjects.isEmpty())
                            "Nenhuma matéria cadastrada"
                        else
                            "Nenhuma matéria encontrada com esses filtros",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredSubjects) { subject ->
                        SubjectItem(subject) { subjectId ->
                            presenter.onSubjectClicked(subjectId)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Menu inferior
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
                IconButton(onClick = onNavigateToAdd) {
                    Image(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Adicionar",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Início",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectItem(subject: Subject, onClick: (Int) -> Unit) {
    val VerdeClaro = Color(0xFFE6F5B0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdeClaro, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(20.dp)
            .clickable { onClick(subject.id) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(subject.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(">", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Gray)
    }
}