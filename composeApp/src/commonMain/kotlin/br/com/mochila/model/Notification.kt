package br.com.mochila.model

enum class NotificationType(val label: String) {
    ABSENCE_LIMIT("Limite de faltas atingido"),
    ABSENCE_WARNING("Aviso de faltas"),
    TASK_OVERDUE("Tarefa vencida"),
    TASK_HIGH_PRIORITY_NO_DATE("Tarefa sem prazo"),
}

data class Notification(
    val id: Int = 0,
    val userId: Int = 0,
    val type: NotificationType,
    val message: String,
    val isRead: Boolean = false,
    val referenceId: Int? = null,
    val createdAt: String = "",
)
