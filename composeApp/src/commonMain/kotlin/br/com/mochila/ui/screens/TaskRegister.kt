@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.mochila.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.mochila.data.UserSession
import br.com.mochila.model.Task
import br.com.mochila.presenter.TaskRegisterPresenter
import br.com.mochila.presenter.TaskRegisterView
import br.com.mochila.ui.screens.components.BackButton
import br.com.mochila.ui.screens.components.ProfileAvatar
import kotlinx.datetime.Clock
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

private fun formatDatePt(date: kotlinx.datetime.LocalDate): String {
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
private fun TaskRegisterBottomBar(
    onOpenMenu: () -> Unit,
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
private fun DesktopProfileStrip(
    onNavigateToAccountSettings: () -> Unit,
) {
    val user = UserSession.currentUser
    val name = user?.name.orEmpty()
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(rosa),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
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
    }
}

@Composable
fun TaskRegisterScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit,
    onNavigateToTasksList: () -> Unit,
    onNavigateToAccountSettings: () -> Unit = {},
    isEditing: Boolean = false,
    taskId: Int? = null,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Pendente") }
    var blockers by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    var loadedTaskId by remember { mutableStateOf(0) }

    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    val statusOptions = listOf("Pendente", "Em andamento", "Cancelada", "Concluida")

    val presenter = remember {
        object : TaskRegisterView {
            override fun showTask(task: Task) {
                loadedTaskId = task.id
                title = task.title
                description = task.description
                status = task.status
                blockers = task.blockers ?: ""
                dueDate = task.dueDate ?: ""
            }

            override fun showValidationError(msg: String) {
                message = msg
                success = false
            }

            override fun showSaveSuccess(isEditing: Boolean) {
                message = if (isEditing) "Tarefa atualizada com sucesso!" else "Tarefa cadastrada com sucesso!"
                success = true
            }

            override fun showSaveError() {
                message = "Erro ao salvar tarefa."
                success = false
            }

            override fun showDeleteSuccess() {
                message = "Tarefa excluída com sucesso!"
                success = true
            }

            override fun showDeleteError() {
                message = "Erro ao excluir tarefa."
                success = false
            }

            override fun navigateToTasksList() {
                onNavigateToTasksList()
            }
        }.let { TaskRegisterPresenter(it) }
    }

    LaunchedEffect(taskId, isEditing) {
        if (isEditing && taskId != null) {
            presenter.loadTask(taskId)
        } else {
            loadedTaskId = 0
        }
    }

    val user = UserSession.currentUser
    val displayName = user?.name.orEmpty()
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val dateLabel = remember(today) { formatDatePt(today) }

    val headerEdgeInset = 22.dp

    @Composable
    fun FormLabel(text: String) {
        Text(
            text = text,
            color = rosa,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 6.dp),
        )
    }

    fun buildTask() = Task(
        id = loadedTaskId,
        userId = userId,
        title = title,
        description = description,
        status = status,
        blockers = blockers.ifBlank { null },
        dueDate = dueDate.ifBlank { null },
    )

    @Composable
    fun OrangeHeaderBar(includeCenterProfile: Boolean) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
                .background(laranjaHeader)
                .padding(vertical = 12.dp),
        ) {
            BackButton(
                onBack = onBack,
                backgroundColor = rosa.copy(alpha = 0.92f),
                iconTint = Color.White,
                buttonSize = 34.dp,
                iconSize = 18.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = headerEdgeInset),
            )
            if (includeCenterProfile) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ProfileAvatar(
                        name = displayName,
                        photoPath = user?.photoPath,
                        size = 40.dp,
                        accentColor = Color.White,
                        onClick = onNavigateToAccountSettings,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = displayName.ifBlank { " " },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 160.dp),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = headerEdgeInset),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = dateLabel,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.End,
                )
                Spacer(Modifier.width(6.dp))
                CalendarGlyph(
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }

    @Composable
    fun StatusField() {
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            FieldRoxo(
                valor = status,
                onChange = {},
                readOnly = true,
                trailing = {
                    IconButton(onClick = { expanded = true }) {
                        Image(
                            painter = painterResource(Res.drawable.drop),
                            contentDescription = "Selecionar status",
                            modifier = Modifier.size(18.dp),
                            colorFilter = ColorFilter.tint(rosa),
                        )
                    }
                },
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                statusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = if (status == option) rosa else Color(0xFF333333),
                                fontWeight = if (status == option) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            status = option
                            expanded = false
                        },
                    )
                }
            }
        }
    }

    @Composable
    fun FormBody(modifier: Modifier = Modifier, fieldsMaxWidth: Dp? = null) {
        val widthCap = fieldsMaxWidth?.let { Modifier.widthIn(max = it) } ?: Modifier
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = widthCap
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Text(
                    text = if (isEditing) "Editar Tarefa" else "Nova Tarefa",
                    color = laranjaHeader,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp,
                )

                Spacer(Modifier.height(20.dp))

                FormLabel("Título:")
                FieldRoxo(
                    valor = title,
                    onChange = { if (it.length <= 30) title = it },
                )

                Spacer(Modifier.height(14.dp))

                FormLabel("Descrição:")
                FieldRoxo(
                    valor = description,
                    onChange = { if (it.length <= 50) description = it },
                )

                Spacer(Modifier.height(14.dp))

                FormLabel("Blockers:")
                FieldRoxo(
                    valor = blockers,
                    onChange = { if (it.length <= 20) blockers = it },
                )

                Spacer(Modifier.height(14.dp))

                FormLabel("Data limite (DD/MM/AAAA)")
                FieldRoxo(
                    valor = dueDate,
                    onChange = { input ->
                        if (input.length < dueDate.length) {
                            dueDate = input
                            return@FieldRoxo
                        }
                        val digits = input.filter { it.isDigit() }.take(8)
                        dueDate = buildString {
                            for (i in digits.indices) {
                                append(digits[i])
                                if (i == 1 || i == 3) append('/')
                            }
                        }
                    },
                    minHeight = 42.dp,
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
                                color = if (success) Color(0xFFB9F6CA) else Color(0xFFFFCDD2),
                                shape = RoundedCornerShape(12.dp),
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
                    onClick = { presenter.saveTask(userId, buildTask(), isEditing) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rosa,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp,
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    Text(
                        text = "Salvar",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )
                }

                if (isEditing && loadedTaskId > 0) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { presenter.deleteTask(userId, loadedTaskId) },
                        border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.Start),
                    ) {
                        Text("Excluir", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoTela),
    ) {
        val wide = maxWidth >= 700.dp
        if (wide) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.weight(0.22f).fillMaxHeight()) {
                    DesktopProfileStrip(onNavigateToAccountSettings = onNavigateToAccountSettings)
                }
                Column(
                    modifier = Modifier
                        .weight(0.78f)
                        .fillMaxHeight(),
                ) {
                    OrangeHeaderBar(includeCenterProfile = false)
                    FormBody(modifier = Modifier.weight(1f), fieldsMaxWidth = 600.dp)
                    TaskRegisterBottomBar(
                        onOpenMenu = onOpenMenu,
                        onNavigateToHome = onNavigateToHome,
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                OrangeHeaderBar(includeCenterProfile = true)
                FormBody(modifier = Modifier.weight(1f))
                TaskRegisterBottomBar(
                    onOpenMenu = onOpenMenu,
                    onNavigateToHome = onNavigateToHome,
                )
            }
        }
    }
}

@Composable
private fun FieldRoxo(
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
            focusedBorderColor = rosa,
            unfocusedBorderColor = rosa,
            focusedTextColor = rosa,
            unfocusedTextColor = rosa,
            cursorColor = rosa,
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraLight,
            color = rosa,
        ),
        shape = RoundedCornerShape(7.dp),
        modifier = modifier
            .heightIn(min = minHeight)
            .fillMaxWidth(),
    )
}
