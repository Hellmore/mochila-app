package br.com.mochila.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.mochila.data.EventRepository
import br.com.mochila.model.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val rosa = Color(0xFFFF6694)

@Composable
fun EventReminderMonitor(userId: Int) {
    var pendingReminder by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(userId) {
        while (true) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val due = withContext(Dispatchers.IO) {
                EventRepository.findDueReminder(userId, now)
            }
            if (due != null && pendingReminder?.id != due.id) {
                pendingReminder = due
            }
            delay(30_000)
        }
    }

    pendingReminder?.let { event ->
        val minutes = event.reminderMinutes ?: 0
        val whenLabel = when (minutes) {
            5 -> "5 minutos"
            10 -> "10 minutos"
            30 -> "30 minutos"
            60 -> "1 hora"
            else -> "$minutes minutos"
        }
        AlertDialog(
            onDismissRequest = {
                EventRepository.markReminderShown(event.id)
                pendingReminder = null
            },
            title = {
                Text(
                    text = "Lembrete",
                    color = rosa,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            },
            text = {
                Text(
                    text = "O evento \"${event.title}\" começa em $whenLabel.",
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        EventRepository.markReminderShown(event.id)
                        pendingReminder = null
                    },
                ) {
                    Text("OK", color = rosa, fontWeight = FontWeight.Medium)
                }
            },
        )
    }
}
