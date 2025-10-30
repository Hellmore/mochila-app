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
import br.com.mochila.auth.AuthViewModel
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSubject: (String) -> Unit
) {
    val roxoEscuro = Color(0xFF5336CB)
    val roxoClaro = Color(0xFF7F55CE)

    var searchText by remember { mutableStateOf("") }
    var showSearchField by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selectedSemester by remember { mutableStateOf("5º semestre") }

    var showMenu by remember { mutableStateOf(false) }

    val subjects = listOf("Engenharia de Software", "Banco de Dados")
    val semesters = listOf("1º semestre", "2º semestre", "3º semestre", "4º semestre", "5º semestre")

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(roxoClaro)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Text(authViewModel.currentUser.value?.email ?: "Usuário", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Matérias",
                    color = roxoEscuro,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { showSearchField = !showSearchField },
                        colors = ButtonDefaults.buttonColors(containerColor = roxoClaro),
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

                    Box {
                        Button(
                            onClick = { expanded = !expanded },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, roxoClaro),
                            modifier = Modifier.width(140.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(selectedSemester, color = roxoClaro, fontSize = 13.sp)
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
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            subjects.filter {
                it.contains(searchText, ignoreCase = true)
            }.forEach { subject ->
                OutlinedButton(
                    onClick = { onNavigateToSubject(subject) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, roxoClaro),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(subject, color = roxoClaro, fontSize = 14.sp)
                        Image(
                            painter = painterResource(Res.drawable.direita),
                            contentDescription = "Abrir matéria",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

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
                        color = roxoEscuro.copy(alpha = 0.95f),
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

        if (showMenu) {
            MenuScreen(
                authViewModel = authViewModel,
                onCloseMenu = { showMenu = false },
                onNavigateToHome = {
                    showMenu = false
                    onNavigateToHome()
                }
            )
        }
    }
}