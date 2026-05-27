package br.com.mochila.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.NotificationRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.NotificationType
import br.com.mochila.model.TaskPriority
import br.com.mochila.util.AbsenceLimit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun parseDateForMonitor(date: String): LocalDate? = runCatching {
    val parts = date.split("/")
    if (parts.size != 3) return null
    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
}.getOrNull()

// Job em background que gera notificacoes de faltas e tarefas
@Composable
fun NotificationMonitor(userId: Int, onChecked: () -> Unit = {}) {
    LaunchedEffect(userId) {
        while (true) {
            println("🔔 [NotificationMonitor] Iniciando verificação para userId=$userId")
            withContext(Dispatchers.IO) {
                checkAbsences(userId)
                checkTasks(userId)
            }
            println("🔔 [NotificationMonitor] Verificação concluída, chamando onChecked()")
            onChecked()
            delay(5 * 60 * 1000L)
        }
    }
}

// Gera alertas de limite e aviso de faltas
private fun checkAbsences(userId: Int) {
    val subjects = SubjectRepository.listByUser(userId)
    val absenceCounts = FaltaRepository.countByUser(userId)
    subjects.forEach { subject ->
        val count = absenceCounts[subject.id] ?: 0
        val max = AbsenceLimit.maxAllowedAbsences(subject)
        when {
            AbsenceLimit.isAtOrOverLimit(count, subject) ->
                NotificationRepository.insertIfNotExists(
                    userId = userId,
                    type = NotificationType.ABSENCE_LIMIT,
                    message = "Você atingiu o limite de faltas em \"${subject.name}\" ($count/$max).",
                    referenceId = subject.id,
                )
            count == max - 1 && count > 0 ->
                NotificationRepository.insertIfNotExists(
                    userId = userId,
                    type = NotificationType.ABSENCE_WARNING,
                    message = "Você está a 1 falta do limite em \"${subject.name}\" ($count/$max).",
                    referenceId = subject.id,
                )
        }
    }
}

// Gera alertas de tarefas vencidas e alta prioridade sem prazo
private fun checkTasks(userId: Int) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    println("🔔 [checkTasks] hoje=$today")
    val tasks = TaskRepository.listByUser(userId)
    println("🔔 [checkTasks] total de tarefas do usuário: ${tasks.size}")
    val active = tasks.filter { it.status == "Pendente" || it.status == "Em andamento" }
    println("🔔 [checkTasks] tarefas ativas: ${active.size} → ${active.map { "${it.title}|status=${it.status}|dueDate=${it.dueDate}|priority=${it.priority}" }}")

    active.filter { task ->
        task.dueDate != null &&
            parseDateForMonitor(task.dueDate).let { d -> d != null && d < today }
    }.also { overdue ->
        println("🔔 [checkTasks] vencidas: ${overdue.size} → ${overdue.map { it.title }}")
    }.forEach { task ->
        val inserted = NotificationRepository.insertIfNotExists(
            userId = userId,
            type = NotificationType.TASK_OVERDUE,
            message = "A tarefa \"${task.title}\" está com prazo vencido.",
            referenceId = task.id,
        )
        println("🔔 [checkTasks] TASK_OVERDUE '${task.title}': inserted=$inserted")
    }

    active.filter { task ->
        task.priority == TaskPriority.ALTA && task.dueDate.isNullOrBlank()
    }.forEach { task ->
        val inserted = NotificationRepository.insertIfNotExists(
            userId = userId,
            type = NotificationType.TASK_HIGH_PRIORITY_NO_DATE,
            message = "A tarefa de alta prioridade \"${task.title}\" não tem prazo definido.",
            referenceId = task.id,
        )
        println("🔔 [checkTasks] TASK_HIGH_PRIORITY_NO_DATE '${task.title}': inserted=$inserted")
    }
}
