package br.com.mochila.model

// Cor padrao das disciplinas (RGB compactado)
const val DEFAULT_SUBJECT_COLOR_RGB = 0x38B6FF

// Disciplina com periodo, frequencia e cor
data class Subject(
    val id: Int = 0,
    val name: String = "",
    val teacher: String = "",
    val minFrequency: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val classHours: Int = 0,
    val weeklyClasses: Int = 1,
    val semester: String = "",
    val colorRgb: Int = DEFAULT_SUBJECT_COLOR_RGB,
)
