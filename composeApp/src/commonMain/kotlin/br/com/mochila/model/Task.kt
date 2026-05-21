package br.com.mochila.model

data class Task(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val description: String = "",
    val status: String = "Pendente",
    val blockers: String? = null,
    val dueDate: String? = null,
    val subjectId: Int? = null,
)