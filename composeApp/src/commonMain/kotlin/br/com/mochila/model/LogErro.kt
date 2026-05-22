package br.com.mochila.model

data class LogErro(
    val id: Int,
    val userId: Int?,
    val userName: String?,
    val modulo: String,
    val mensagem: String,
    val criadoEm: String,
)
