package br.com.mochila.model

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

        fun fromNameOrNull(name: String): TaskCategory? =
            entries.find { it.name == name }
    }
}
