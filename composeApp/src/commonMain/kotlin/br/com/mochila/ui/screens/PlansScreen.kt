package br.com.mochila.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.ui.screens.components.BackButton
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.background
import mochila_app.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

// Paleta da tela de planos
private val PlansRosa    = Color(0xFFFF6694)
private val PlansAmarelo = Color(0xFFFFC107)
private val PlansRoxo    = Color(0xFF7F55CE)
private val PlansBg      = Color(0xFFF8F8F8)
private val PlansLaranja = Color(0xFFFFBA5E)

// Apresentacao dos planos Free e Premium
@Composable
fun PlansScreen(onBack: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(PlansBg)
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .background(PlansRosa),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp),
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
                        Spacer(Modifier.height(24.dp))
                        Text("Mochila Hub", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Eleve sua experiência acadêmica",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        Text("✦ PLUS", color = PlansLaranja, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 40.dp, vertical = 32.dp),
                    ) {
                        BackButton(onBack = onBack, backgroundColor = PlansRosa, iconTint = Color.White)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "Escolha seu plano",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlansRosa,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Todos os planos incluem 7 dias de avaliação gratuita.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                        )
                        Spacer(Modifier.height(28.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            PlanCard(modifier = Modifier.weight(1f).fillMaxHeight(), plan = basicPlan,   fillHeight = true)
                            PlanCard(modifier = Modifier.weight(1f).fillMaxHeight(), plan = plusPlan,    fillHeight = true)
                            PlanCard(modifier = Modifier.weight(1f).fillMaxHeight(), plan = premiumPlan, fillHeight = true)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(Res.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                ) {
                    BackButton(onBack = onBack, backgroundColor = PlansRosa, iconTint = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Escolha seu plano",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlansRosa,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "7 dias de avaliação gratuita em todos os planos.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        PlanCard(modifier = Modifier.fillMaxWidth(), plan = basicPlan,   fillHeight = false)
                        PlanCard(modifier = Modifier.fillMaxWidth(), plan = plusPlan,    fillHeight = false)
                        PlanCard(modifier = Modifier.fillMaxWidth(), plan = premiumPlan, fillHeight = false)
                    }
                }
            }
        }
    }
}

private data class PlanData(
    val name: String,
    val badge: String?,
    val price: String,
    val period: String,
    val features: List<String>,
    val buttonLabel: String,
    val color: Color,
    val isHighlighted: Boolean = false,
)

private val basicPlan = PlanData(
    name = "Básico",
    badge = null,
    price = "R$ 10",
    period = "/mês",
    features = listOf(
        "Lembretes por e-mail",
        "Exportação de relatórios",
        "Número ilimitado de eventos",
        "Tarefas recorrentes",
    ),
    buttonLabel = "Assinar Básico",
    color = PlansAmarelo,
)

private val plusPlan = PlanData(
    name = "Plus",
    badge = null,
    price = "R$ 25",
    period = "/mês",
    features = listOf(
        "Tudo do Básico",
        "Backup e sincronização em nuvem",
        "Metas de estudo",
        "Dashboard de desempenho",
        "Notificações push",
    ),
    buttonLabel = "Assinar Plus",
    color = PlansRosa,
)

private val premiumPlan = PlanData(
    name = "Premium",
    badge = null,
    price = "R$ 40",
    period = "/mês",
    features = listOf(
        "Tudo do Plus",
        "Planejador de estudos com IA",
        "Compartilhamento de cronograma",
        "Integração com Google Agenda",
        "Acesso antecipado a novidades",
    ),
    buttonLabel = "Assinar Premium",
    color = PlansRoxo,
)

@Composable
private fun PlanCard(modifier: Modifier = Modifier, plan: PlanData, fillHeight: Boolean = false) {
    val borderColor = if (plan.isHighlighted) plan.color else Color(0xFFE0E0E0)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(plan.color, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (plan.badge != null) {
                    Text(
                        text = plan.badge,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Text(plan.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fillHeight) Modifier.weight(1f) else Modifier)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (fillHeight) Arrangement.SpaceBetween else Arrangement.Top,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(plan.price, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF666666))
                    Text(plan.period, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    plan.features.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text("•", color = plan.color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(feature, fontSize = 12.sp, color = Color(0xFF888888))
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!fillHeight) Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = plan.color),
                ) {
                    Text(plan.buttonLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(4.dp))
                Text("7 dias grátis", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
