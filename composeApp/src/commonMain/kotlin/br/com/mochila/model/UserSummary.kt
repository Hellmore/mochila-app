package br.com.mochila.model

data class UserSummary(
    val id: Int,
    val name: String,
    val email: String,
    val isAdmin: Boolean,
    val emailVerified: Boolean,
    val createdAt: String,
)
