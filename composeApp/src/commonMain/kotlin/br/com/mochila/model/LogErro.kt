package br.com.mochila.model

// Registro de erro da aplicacao
data class LogErro(
    val id: Int,
    val userId: Int?,
    val userName: String?,
    val modulo: String,
    val mensagem: String,
    val criadoEm: String,
)
