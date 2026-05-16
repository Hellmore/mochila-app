package br.com.mochila.model

/** RGB sem canal alpha (ex.: 0x38B6FF); exibição usa opacidade total. */
const val DEFAULT_SUBJECT_COLOR_RGB = 0x38B6FF

data class Subject(
    val id: Int = 0,
    val name: String = "",
    val teacher: String = "",
    val minFrequency: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val classHours: Int = 0,
    val semester: String = "",
    val colorRgb: Int = DEFAULT_SUBJECT_COLOR_RGB,
)