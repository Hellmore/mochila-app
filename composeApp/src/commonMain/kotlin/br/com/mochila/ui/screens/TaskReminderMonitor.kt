package br.com.mochila.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

private val taskReminderRosa = Color(0xFFFF6694)

private fun parseDdMmYyyy(date: String): LocalDate? = runCatching {
    val parts = date.split("/")
    if (parts.size != 3) return null
    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
}.getOrNull()

// Alerta em tela cheia quando prazo da tarefa e hoje ou amanha
@Composable
fun TaskReminderMonitor(userId: Int) {
    var shownIds by remember { mutableStateOf(setOf<Int>()) }
    var pendingTask by remember { mutableStateOf<Task?>(null) }
    var daysUntilDue by remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        while (true) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val tomorrow = today.plus(1, DateTimeUnit.DAY)

            val tasks = withContext(Dispatchers.IO) { TaskRepository.listByUser(userId) }
            val due = tasks.firstOrNull { task ->
                task.id !in shownIds &&
                    task.status in listOf("Pendente", "Em andamento") &&
                    task.dueDate != null &&
                    parseDdMmYyyy(task.dueDate).let { d -> d == today || d == tomorrow }
            }

            if (due != null && pendingTask?.id != due.id) {
                val parsed = parseDdMmYyyy(due.dueDate!!)
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                daysUntilDue = if (parsed == now) 0 else 1
                pendingTask = due
            }

            delay(60_000)
        }
    }

    pendingTask?.let { task ->
        val label = if (daysUntilDue == 0) "hoje" else "amanhã"
        AlertDialog(
            onDismissRequest = {
                shownIds = shownIds + task.id
                pendingTask = null
            },
            title = {
                Text(
                    text = "Prazo se aproximando",
                    color = taskReminderRosa,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            },
            text = {
                Text(
                    text = "A tarefa \"${task.title}\" vence $label.",
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        shownIds = shownIds + task.id
                        pendingTask = null
                    },
                ) {
                    Text("OK", color = taskReminderRosa, fontWeight = FontWeight.Medium)
                }
            },
        )
    }
}
