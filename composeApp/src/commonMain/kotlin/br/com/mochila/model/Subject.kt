package br.com.mochila.model

data class Subject(
    val id: Int = 0,
    val name: String = "",
    val teacher: String = "",
    val minFrequency: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val classHours: Int = 0,
    val semester: String = ""
)