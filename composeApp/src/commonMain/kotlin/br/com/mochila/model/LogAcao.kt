package br.com.mochila.model

// Registro de acao do usuario no sistema
data class LogAcao(
    val id: Int,
    val userId: Int,
    val userName: String?,
    val acao: String,
    val descricao: String?,
    val tabelaAfetada: String,
    val idRegistroAfetado: Int?,
    val criadoEm: String,
)
