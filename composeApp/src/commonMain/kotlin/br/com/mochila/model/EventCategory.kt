package br.com.mochila.model

enum class EventCategory(val label: String) {
    PROVA("Prova"),
    ATIVIDADE_AVALIATIVA("Atividade avaliativa"),
    ATIVIDADE_NAO_AVALIATIVA("Atividade não avaliativa"),
    SEMINARIO("Seminário"),
    ;

    companion object {
        val options = entries.toList()
        val default = PROVA

        fun fromNameOrNull(name: String): EventCategory? =
            entries.find { it.name == name }
    }
}
