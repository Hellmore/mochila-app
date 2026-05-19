@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.UserSession
import br.com.mochila.model.Falta
import br.com.mochila.model.Subject
import br.com.mochila.presenter.FaltaRegisterPresenter
import br.com.mochila.presenter.FaltaRegisterView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.CalendarPicker
import br.com.mochila.ui.screens.components.ProfileAvatar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mochila_app.composeapp.generated.resources.Res
import mochila_app.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val faltaRegFundoTela = Color(0xFFF8F8F8)
private val faltaRegLaranja = Color(0xFFFFBA5E)
private val faltaRegRosa = Color(0xFFFF6694)

private val faltaStatusOptions = listOf("Registrada", "Justificada", "Não Justificada")

private val faltaMonthNamesPtReg = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private fun faltaRegFormatDatePt(date: LocalDate): String {
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    val mon = faltaMonthNamesPtReg[date.monthNumber - 1]
    val yy = (date.year % 100).toString().padStart(2, '0')
    return "$dd $mon $yy"
}

@Composable
fun FaltaRegisterScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToFaltasList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit = {},
    isEditing: Boolean = false,
    faltaId: Int? = null,
) {
    var selectedSubjectId by remember { mutableStateOf(0) }
    var selectedSubjectName by remember { mutableStateOf("Selecione uma matéria") }
    var faltaDate by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Registrada") }
    var loadedFaltaId by remember { mutableStateOf(0) }
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    val presenter = remember {
        object : FaltaRegisterView {
            override fun showFalta(falta: Falta) {
                loadedFaltaId = falta.id
                selectedSubjectId = falta.subjectId
                selectedSubjectName = falta.subjectName ?: "Selecione uma matéria"
                faltaDate = FaltaRepository.formatDateForDisplay(falta.date)
                selectedStatus = if (falta.status == "Nao Justificada") "Não Justificada" else falta.status
            }
            override fun showValidationError(msg: String) { message = msg; success = false }
            override fun showSaveSuccess(isEditing: Boolean) {
                message = if (isEditing) "Falta atualizada com sucesso!" else "Falta cadastrada com sucesso!"
                success = true
            }
            override fun showSaveError() { message = "Erro ao salvar falta."; success = false }
            override fun showDeleteSuccess() { message = "Falta excluída com sucesso!"; success = true }
            override fun showDeleteError() { message = "Erro ao excluir falta."; success = false }
            override fun navigateToFaltasList() { onNavigateToFaltasList() }
        }.let { FaltaRegisterPresenter(it) }
    }

    LaunchedEffect(userId) { subjects = SubjectRepository.listByUser(userId) }
    LaunchedEffect(faltaId, isEditing) {
        if (isEditing && faltaId != null) presenter.loadFalta(faltaId) else loadedFaltaId = 0
    }

    val user = UserSession.currentUser
    val displayName = user?.name.orEmpty()
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val dateLabel = remember(today) { faltaRegFormatDatePt(today) }

    fun buildFalta() = Falta(
        id = loadedFaltaId,
        userId = userId,
        subjectId = selectedSubjectId,
    )

    @Composable
    fun FormLabel(text: String) {
        Text(
            text = text,
            color = faltaRegRosa,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp),
        )
    }

    @Composable
    fun SubjectField() {
        var expanded by remember { mutableStateOf(false) }
        val options = subjects.map { it.name }
        Box(modifier = Modifier.fillMaxWidth()) {
            FaltaFieldRoxo(
                valor = selectedSubjectName,
                onChange = {},
                readOnly = true,
                trailing = {
                    IconButton(onClick = { expanded = true }) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(faltaRegLaranja),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.drop),
                                contentDescription = "Selecionar matéria",
                                modifier = Modifier.size(14.dp),
                                colorFilter = ColorFilter.tint(Color.White),
                            )
                        }
                    }
                },
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { name ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                color = if (name == selectedSubjectName) faltaRegRosa else Color(0xFF333333),
                            )
                        },
                        onClick = {
                            selectedSubjectName = name
                            selectedSubjectId = subjects.firstOrNull { it.name == name }?.id ?: 0
                            expanded = false
                        },
                    )
                }
            }
        }
    }

    @Composable
    fun StatusField() {
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            FaltaFieldRoxo(
                valor = selectedStatus,
                onChange = {},
                readOnly = true,
                trailing = {
                    IconButton(onClick = { expanded = true }) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(faltaRegLaranja),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.drop),
                                contentDescription = "Selecionar status",
                                modifier = Modifier.size(14.dp),
                                colorFilter = ColorFilter.tint(Color.White),
                            )
                        }
                    }
                },
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                faltaStatusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = if (option == selectedStatus) faltaRegRosa else Color(0xFF333333),
                            )
                        },
                        onClick = { selectedStatus = option; expanded = false },
                    )
                }
            }
        }
    }

    @Composable
    fun OrangeHeaderBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(faltaRegLaranja)
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
    fun FaltaRegisterBottomBar() {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .background(faltaRegRosa.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
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
                IconButton(onClick = onNavigateToHome) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
        }
    }

    @Composable
    fun FormBody(modifier: Modifier = Modifier, fieldsMaxWidth: Dp? = null) {
        val widthCap = fieldsMaxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = widthCap
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 36.dp, vertical = 20.dp),
            ) {
                Text(
                    text = if (isEditing) "Editar Falta" else "Nova Falta",
                    color = faltaRegLaranja,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(20.dp))

                FormLabel("Matéria:")
                SubjectField()

                Spacer(Modifier.height(14.dp))

                FormLabel("Data da falta:")
                CalendarPicker(
                    selectedDate = faltaDate,
                    onDateSelected = { faltaDate = it },
                    accentColor = faltaRegRosa,
                )

                Spacer(Modifier.height(14.dp))

                FormLabel("Status:")
                StatusField()

                Spacer(Modifier.height(20.dp))

                message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                RoundedCornerShape(12.dp),
                            )
                            .padding(16.dp),
                    ) {
                        Text(
                            text = msg,
                            color = if (success) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontSize = 15.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    onClick = { presenter.saveFalta(userId, buildFalta(), faltaDate, selectedStatus, isEditing) },
                    colors = ButtonDefaults.buttonColors(containerColor = faltaRegRosa, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    Text("Salvar", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }

                if (isEditing && loadedFaltaId > 0) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { presenter.deleteFalta(userId, loadedFaltaId) },
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    ) {
                        Text("Excluir", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(faltaRegFundoTela)) {
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
                Box(Modifier.weight(0.4f).fillMaxHeight().background(faltaRegRosa)) {
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
                            name = displayName,
                            photoPath = user?.photoPath,
                            size = 48.dp,
                            accentColor = Color.White,
                            onClick = onNavigateToAccountSettings,
                        )
                    }
                }
                Column(Modifier.weight(0.6f).fillMaxHeight()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BackButton(onBack = onBack, backgroundColor = faltaRegRosa.copy(alpha = 0.92f), iconTint = Color.White)
                        Spacer(Modifier.weight(1f))
                        Text(text = dateLabel, color = Color(0xFF333333), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(6.dp))
                        Image(
                            painter = painterResource(Res.drawable.menu_icon_today),
                            contentDescription = "Calendário",
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(faltaRegRosa),
                        )
                    }
                    FormBody(modifier = Modifier.weight(1f), fieldsMaxWidth = 600.dp)
                    FaltaRegisterBottomBar()
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                OrangeHeaderBar()
                FormBody(modifier = Modifier.weight(1f))
                FaltaRegisterBottomBar()
            }
        }
    }
}

@Composable
private fun FaltaFieldRoxo(
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 46.dp,
    readOnly: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        readOnly = readOnly,
        singleLine = true,
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = faltaRegRosa,
            unfocusedBorderColor = faltaRegRosa,
            focusedTextColor = faltaRegRosa,
            unfocusedTextColor = faltaRegRosa,
            cursorColor = faltaRegRosa,
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraLight,
            color = faltaRegRosa,
        ),
        shape = RoundedCornerShape(7.dp),
        modifier = modifier.heightIn(min = minHeight).fillMaxWidth(),
    )
}
