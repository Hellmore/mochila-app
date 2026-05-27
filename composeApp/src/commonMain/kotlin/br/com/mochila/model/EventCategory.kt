package br.com.mochila.model

// Tipos de evento exibidos na UI
enum class EventCategory(val label: String) {
    PROVA("Prova"),
    ATIVIDADE_AVALIATIVA("Atividade avaliativa"),
    ATIVIDADE_NAO_AVALIATIVA("Atividade não avaliativa"),
    SEMINARIO("Seminário"),
    ;

    companion object {
        val options = entries.toList()
        val default = PROVA

        // Resolve enum pelo nome persistido no banco
        fun fromNameOrNull(name: String): EventCategory? =
            entries.find { it.name == name }
    }
}
