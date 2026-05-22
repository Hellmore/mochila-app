@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.model.LogAcao
import br.com.mochila.model.LogErro
import br.com.mochila.presenter.AdminLogsPresenter
import br.com.mochila.presenter.AdminLogsView
import br.com.mochila.ui.screens.components.BackButton
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val alRosa      = Color(0xFFFF6694)
private val alFundo     = Color(0xFFF8F8F8)
private val alErroColor = Color(0xFFD32F2F)
private val alLaranja   = Color(0xFFFFBA5E)

@Composable
fun AdminLogsScreen(onBack: () -> Unit) {
    var logsAcao   by remember { mutableStateOf<List<LogAcao>>(emptyList()) }
    var logsErro   by remember { mutableStateOf<List<LogErro>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val presenter = remember {
        object : AdminLogsView {
            override fun showLogsAcao(logs: List<LogAcao>) { logsAcao = logs }
            override fun showLogsErro(logs: List<LogErro>) { logsErro = logs }
        }.let { AdminLogsPresenter(it) }
    }

    LaunchedEffect(Unit) {
        presenter.loadLogsAcao()
        presenter.loadLogsErro()
    }

    @Composable
    fun Content() {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onBack = onBack, backgroundColor = alRosa, iconTint = Color.White)
            }

            Spacer(Modifier.height(16.dp))
            Text("Logs do Sistema", color = alRosa, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = alLaranja,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Ações (${logsAcao.size})", fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Erros (${logsErro.size})", fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) alErroColor else Color.Gray,
                        )
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (selectedTab == 0) {
                    if (logsAcao.isEmpty()) {
                        EmptyLogsMessage("Nenhuma ação registrada.")
                    } else {
                        logsAcao.forEach { log -> LogAcaoCard(log) }
                    }
                } else {
                    if (logsErro.isEmpty()) {
                        EmptyLogsMessage("Nenhum erro registrado.")
                    } else {
                        logsErro.forEach { log -> LogErroCard(log) }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(alFundo)) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(Modifier.fillMaxSize()) {
                AdminSidebar(modifier = Modifier.weight(0.4f).fillMaxHeight())
                Box(
                    modifier = Modifier.weight(0.6f).fillMaxHeight().background(alFundo),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 1f,
                    )
                    Content()
                }
            }
        } else {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 1f,
            )
            Content()
        }
    }
}

@Composable
private fun LogAcaoCard(log: LogAcao) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = log.acao,
                color = alRosa, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(log.criadoEm.take(16), color = Color.Gray, fontSize = 11.sp)
        }
        Spacer(Modifier.height(2.dp))
        Text(log.userName ?: "Usuário #${log.userId}", color = Color(0xFF333333), fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("tabela: ${log.tabelaAfetada}", color = Color.Gray, fontSize = 11.sp)
            log.idRegistroAfetado?.let { Text("id: $it", color = Color.Gray, fontSize = 11.sp) }
        }
        log.descricao?.let {
            Text(it, color = Color.Gray, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun LogErroCard(log: LogErro) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = log.modulo,
                color = alErroColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(log.criadoEm.take(16), color = Color.Gray, fontSize = 11.sp)
        }
        Spacer(Modifier.height(2.dp))
        Text(log.mensagem, color = Color(0xFF333333), fontSize = 12.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        log.userName?.let { Text("Usuário: $it", color = Color.Gray, fontSize = 11.sp) }
    }
}

@Composable
private fun EmptyLogsMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}
