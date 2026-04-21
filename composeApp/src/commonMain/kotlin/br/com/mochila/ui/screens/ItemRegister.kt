package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.presenter.ItemRegisterPresenter
import br.com.mochila.presenter.ItemRegisterView
import br.com.mochila.ui.screens.components.BackButton
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun ItemRegisterScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToSubjectRegister: () -> Unit,
    onNavigateToTaskRegister: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val RoxoClaro = Color(0xFF7F55CE)
    val RoxoEscuro = Color(0xFF5336CB)

    var showMenu by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Carregando...") }

    val presenter = remember {
        object : ItemRegisterView {
            override fun showUserName(name: String) { userName = name }
        }.let { view -> ItemRegisterPresenter(view) }
    }

    LaunchedEffect(userId) {
        presenter.loadUserName(userId)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onBack = onBack)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(RoxoEscuro)
                        .clickable { onNavigateToAccountSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuário",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp).clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(userName, color = Color.Gray, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Registros",
                color = RoxoEscuro,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            val buttons = listOf(
                "Nova Matéria" to onNavigateToSubjectRegister,
                "Nova Tarefa" to onNavigateToTaskRegister,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                buttons.forEach { (title, action) ->
                    Button(
                        onClick = action,
                        colors = ButtonDefaults.buttonColors(containerColor = RoxoClaro),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .widthIn(max = 800.dp)
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp)
                            .height(50.dp)
                    ) {
                        Text(title, color = Color.White, fontSize = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
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
                        color = RoxoClaro.copy(alpha = 0.95f),
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
                onCloseMenu = { showMenu = false },
                onNavigateToHome = { showMenu = false; onNavigateToHome() },
                onNavigateToTasksList = { showMenu = false; onNavigateToTasksList() },
                onNavigateToAccountSettings = { showMenu = false; onNavigateToAccountSettings() },
                onLogout = { showMenu = false; onLogout() }
            )
        }
    }
}