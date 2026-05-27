package br.com.mochila.model

// Cor padrao dos eventos (RGB compactado)
const val DEFAULT_EVENT_COLOR_RGB = 0x65D145

// Evento de calendario com lembrete e disciplina opcional
data class Event(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val description: String? = null,
    val eventDate: String = "",
    val status: String = "Agendado",
    val category: EventCategory = EventCategory.default,
    val subjectId: Int? = null,
    val subjectName: String? = null,
    val colorRgb: Int = DEFAULT_EVENT_COLOR_RGB,
    val reminderMinutes: Int? = null,
    val reminderShown: Boolean = false,
)
