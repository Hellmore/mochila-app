@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.EventRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.Event
import br.com.mochila.presenter.EventDetailPresenter
import br.com.mochila.presenter.EventDetailView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val edFundo = Color(0xFFF8F8F8)
private val edLaranja = Color(0xFFFFBA5E)
private val edRosa = Color(0xFFFF6694)

private val edMonthNames = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private val edReminderLabels = mapOf(
    null to "Nenhum",
    5 to "5 Minutos antes",
    10 to "10 Minutos antes",
    30 to "30 Minutos antes",
    60 to "1 Hora antes",
)

private fun edFormatDate(date: LocalDate): String {
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    val mon = edMonthNames[date.monthNumber - 1]
    val yy = (date.year % 100).toString().padStart(2, '0')
    return "$dd $mon $yy"
}

private fun edRgbToColor(rgb: Int): Color {
    val r = ((rgb shr 16) and 0xFF) / 255f
    val g = ((rgb shr 8) and 0xFF) / 255f
    val b = (rgb and 0xFF) / 255f
    return Color(r, g, b)
}

// Tela de visualizacao dos dados de um evento
@Composable
fun EventDetailScreen(
    userId: Int,
    eventId: Int,
    onNavigateToEdit: (Event) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToEventsList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val presenter = remember {
        object : EventDetailView {
            override fun showEvent(e: Event) { event = e }
            override fun showEventNotFound() {}
            override fun showDeleteSuccess() {}
            override fun showDeleteError() {}
            override fun navigateToEventsList() { onNavigateToEventsList() }
            override fun navigateToEdit(e: Event) { onNavigateToEdit(e) }
            override fun navigateBack() { onBack() }
        }.let { EventDetailPresenter(it) }
    }

    LaunchedEffect(eventId) { presenter.loadEvent(eventId) }

    val user = UserSession.currentUser
    val displayName = user?.name.orEmpty()
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val dateLabel = remember(today) { edFormatDate(today) }

    @Composable
    fun FormLabel(text: String) {
        Text(
            text = text,
            color = edRosa,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 6.dp),
        )
    }

    @Composable
    fun FieldDisplay(value: String) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = edRosa,
                unfocusedBorderColor = edRosa,
                focusedTextColor = edRosa,
                unfocusedTextColor = edRosa,
                cursorColor = Color.Transparent,
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraLight,
                color = edRosa,
            ),
            shape = RoundedCornerShape(7.dp),
            modifier = Modifier.heightIn(min = 46.dp).fillMaxWidth(),
        )
    }

    @Composable
    fun OrangeHeaderBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(edLaranja)
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
                        name = displayName,
                        photoPath = user?.photoPath,
                        size = 40.dp,
                        accentColor = Color.White,
                        onClick = onNavigateToAccountSettings,
                    )
                    Text(
                        text = displayName.ifBlank { " " },
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
    }

    @Composable
    fun DesktopSidebar() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(edRosa),
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
                    name = displayName,
                    photoPath = user?.photoPath,
                    size = 48.dp,
                    accentColor = Color.White,
                    onClick = onNavigateToAccountSettings,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = displayName.ifBlank { " " },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    @Composable
    fun BottomBar() {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .background(edRosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { showMenu = true }) {
                    Image(
                        painter = painterResource(Res.drawable.menu),
                        contentDescription = "Menu",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
                IconButton(onClick = onNavigateToEventsList) {
                    Image(
                        painter = painterResource(Res.drawable.add),
                        contentDescription = "Lista de eventos",
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
    fun ContentBody(modifier: Modifier = Modifier, fieldsMaxWidth: Dp? = null) {
        val widthCap = fieldsMaxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            event?.let { e ->
                Column(
                    modifier = widthCap
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 36.dp, vertical = 20.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(edRgbToColor(e.colorRgb)),
                        )
                        Text(
                            text = "Detalhes do Evento",
                            color = edLaranja,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(20.dp))

                    FormLabel("Nome do evento:")
                    FieldDisplay(e.title)
                    Spacer(Modifier.height(14.dp))

                    FormLabel("Matéria:")
                    FieldDisplay(e.subjectName ?: "Nenhuma")
                    Spacer(Modifier.height(14.dp))

                    FormLabel("Categoria:")
                    FieldDisplay(e.category.label)
                    Spacer(Modifier.height(14.dp))

                    FormLabel("Data:")
                    FieldDisplay(EventRepository.formatEventDateForDisplay(e.eventDate))
                    Spacer(Modifier.height(14.dp))

                    FormLabel("Descrição:")
                    FieldDisplay(e.description.orEmpty().ifBlank { "Sem descrição" })
                    Spacer(Modifier.height(14.dp))

                    FormLabel("Lembrete:")
                    FieldDisplay(edReminderLabels[e.reminderMinutes] ?: "Nenhum")

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { presenter.onEditClicked(e) },
                        colors = ButtonDefaults.buttonColors(containerColor = edRosa, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                    ) {
                        Text("Editar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                    ) {
                        Text("Excluir", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        event?.let { e ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Excluir evento", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = { Text("Tem certeza que deseja excluir \"${e.title}\"? Esta ação não pode ser desfeita.", fontSize = 14.sp) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        presenter.onDeleteConfirmed(userId, e)
                    }) {
                        Text("Excluir", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = edRosa)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(edFundo),
    ) {
        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.50f,
        )
        val wide = maxWidth >= 700.dp
        if (wide) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.weight(0.4f).fillMaxHeight()) {
                    DesktopSidebar()
                }
                Column(Modifier.weight(0.6f).fillMaxHeight()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BackButton(onBack = onBack, backgroundColor = edRosa.copy(alpha = 0.92f), iconTint = Color.White)
                        Spacer(Modifier.weight(1f))
                        Text(text = dateLabel, color = Color(0xFF333333), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(6.dp))
                        Image(
                            painter = painterResource(Res.drawable.menu_icon_today),
                            contentDescription = "Calendário",
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(edRosa),
                        )
                    }
                    ContentBody(modifier = Modifier.weight(1f), fieldsMaxWidth = 600.dp)
                    BottomBar()
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                OrangeHeaderBar()
                ContentBody(modifier = Modifier.weight(1f))
                BottomBar()
            }
        }
    }

    if (showMenu) {
        MenuScreen(
            onCloseMenu = { showMenu = false },
            onNavigateToHome = { showMenu = false; onNavigateToHome() },
            onNavigateToTasksList = { showMenu = false },
            onNavigateToAccountSettings = { showMenu = false; onNavigateToAccountSettings() },
            onLogout = { showMenu = false; onLogout() },
        )
    }
}
