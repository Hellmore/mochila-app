package br.com.mochila.model

data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val photoPath: String? = null,
    val isAdmin: Boolean = false,
)