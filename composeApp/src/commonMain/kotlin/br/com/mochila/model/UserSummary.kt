package br.com.mochila.model

// Resumo de usuario para listagens e painel admin
data class UserSummary(
    val id: Int,
    val name: String,
    val email: String,
    val isAdmin: Boolean,
    val emailVerified: Boolean,
    val createdAt: String,
)
