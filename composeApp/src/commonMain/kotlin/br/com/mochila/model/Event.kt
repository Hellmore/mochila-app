package br.com.mochila.model

/** RGB sem canal alpha; padrão verde do Figma. */
const val DEFAULT_EVENT_COLOR_RGB = 0x65D145

data class Event(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val description: String? = null,
    val eventDate: String = "",
    val status: String = "Agendado",
    val subjectId: Int? = null,
    val subjectName: String? = null,
    val colorRgb: Int = DEFAULT_EVENT_COLOR_RGB,
    val reminderMinutes: Int? = null,
    val reminderShown: Boolean = false,
)
