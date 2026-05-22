package br.com.mochila.model

enum class TaskPriority(val label: String, val weight: Int) {
    ALTA("Alta", 3),
    MEDIA("Média", 2),
    BAIXA("Baixa", 1),
    ;

    companion object {
        val options = entries.toList()
    }
}
