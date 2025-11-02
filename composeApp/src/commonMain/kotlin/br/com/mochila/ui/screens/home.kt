package br.com.mochila.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import br.com.mochila.data.DatabaseHelper
import java.sql.ResultSet

@Composable
fun HomeScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubject: (String) -> Unit,
    onLogout: () -> Unit
) {
    val RoxoEscuro = Color(0xFF5336CB)
    val RoxoClaro = Color(0xFF7F55CE)

    var searchText by remember { mutableStateOf("") }
    var showSearchField by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedSemester by remember { mutableStateOf("Todos") }
    var showMenu by remember { mutableStateOf(false) }

    // Lista de matérias e semestres existentes no banco
    var subjects by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var semesters by remember { mutableStateOf<List<String>>(emptyList()) }

    // 🔹 Carregar matérias do banco
    LaunchedEffect(selectedSemester) {
        val conn = DatabaseHelper.connect()
        if (conn != null) {
            try {
                val stmt = conn.createStatement()

                // Carrega lista de semestres únicos
                val rsSem = stmt.executeQuery("SELECT DISTINCT semestre FROM disciplina WHERE semestre IS NOT NULL AND semestre <> '' ORDER BY semestre")
                val semList = mutableListOf<String>()
                while (rsSem.next()) {
                    semList.add(rsSem.getString("semestre"))
                }
                semesters = semList
                rsSem.close()

                // Monta a query com filtro por semestre (se necessário)
                val sql = if (selectedSemester == "Todos") {
                    "SELECT id_disciplina, nome, professor, semestre FROM disciplina ORDER BY semestre, nome"
                } else {
                    "SELECT id_disciplina, nome, professor, semestre FROM disciplina WHERE semestre = '${selectedSemester}' ORDER BY nome"
                }

                val rs: ResultSet = stmt.executeQuery(sql)
                val lista = mutableListOf<Map<String, String>>()
                while (rs.next()) {
                    lista.add(
                        mapOf(
                            "id" to rs.getInt("id_disciplina").toString(),
                            "nome" to (rs.getString("nome") ?: ""),
                            "professor" to (rs.getString("professor") ?: ""),
                            "semestre" to (rs.getString("semestre") ?: "")
                        )
                    )
                }
                subjects = lista

                rs.close()
                stmt.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                DatabaseHelper.close()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fundo quadriculado
        Image(
            painter = painterResource(Res.drawable.fundo_quadriculado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Pin e mochila decorativos
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
                Text(
                    text = "Mochila Hub",
                    color = RoxoEscuro,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
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

            Spacer(modifier = Modifier.height(16.dp))

            // 🏷️ Título e filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Matérias",
                    color = RoxoEscuro,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 🔍 Botão para mostrar/ocultar campo de busca
                    Button(
                        onClick = { showSearchField = !showSearchField },
                        colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Filtro", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(Res.drawable.drop),
                            contentDescription = "Expandir filtro",
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dropdown semestre
                    Box {
                        Button(
                            onClick = { expanded = !expanded },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, RoxoClaro),
                            modifier = Modifier.width(140.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(selectedSemester, color = RoxoClaro, fontSize = 13.sp)
                                Image(
                                    painter = painterResource(Res.drawable.drop),
                                    contentDescription = "Abrir menu",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos") },
                                onClick = {
                                    selectedSemester = "Todos"
                                    expanded = false
                                }
                            )
                            semesters.forEach { semester ->
                                DropdownMenuItem(
                                    text = { Text(semester) },
                                    onClick = {
                                        selectedSemester = semester
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 🔍 Campo de busca
            if (showSearchField) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Digite o nome da matéria") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = RoxoClaro,
                        unfocusedBorderColor = RoxoClaro
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📚 Lista de matérias cadastradas
            val filteredSubjects = subjects.filter {
                it["nome"]!!.contains(searchText, ignoreCase = true)
            }

            if (filteredSubjects.isEmpty()) {
                Text(
                    "Nenhuma matéria encontrada.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                filteredSubjects.forEach { subject ->
                    OutlinedButton(
                        onClick = { onNavigateToSubject(subject["id"] ?: "") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, RoxoClaro),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                subject["nome"] ?: "",
                                color = RoxoClaro,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!subject["professor"].isNullOrBlank())
                                Text(
                                    "Professor: ${subject["professor"]}",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            if (!subject["semestre"].isNullOrBlank())
                                Text(
                                    "Semestre: ${subject["semestre"]}",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
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
                        contentDescription = "Home",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Overlay do menu lateral
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
