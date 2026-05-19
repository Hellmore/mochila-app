package br.com.mochila.model

data class Falta(
    val id: Int = 0,
    val userId: Int = 0,
    val subjectId: Int = 0,
    val subjectName: String? = null,
    val subjectColorRgb: Int = DEFAULT_SUBJECT_COLOR_RGB,
    val date: String = "",
    val status: String = "Nao Justificada",
)
