package br.com.mochila.model

// Tipos de tarefa exibidos na UI
enum class TaskCategory(val label: String) {
    TAREFA_DE_CASA("Tarefa de casa"),
    ATIVIDADE_AVALIATIVA("Atividade avaliativa"),
    ATIVIDADE_NAO_AVALIATIVA("Atividade não avaliativa"),
    ENTREGA_DE_DOCUMENTOS("Entrega de documentos"),
    FICHAMENTO("Fichamento"),
    ;

    companion object {
        val options = entries.toList()
        val default = TAREFA_DE_CASA

        // Resolve enum pelo nome persistido no banco
        fun fromNameOrNull(name: String): TaskCategory? =
            entries.find { it.name == name }
    }
}
