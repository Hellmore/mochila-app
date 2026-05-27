package br.com.mochila.model

// Tarefa academica com categoria, prioridade e prazo
data class Task(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val description: String = "",
    val status: String = "Pendente",
    val category: TaskCategory = TaskCategory.default,
    val priority: TaskPriority = TaskPriority.default,
    val blockers: String? = null,
    val dueDate: String? = null,
    val subjectId: Int? = null,
)
