package br.com.mochila.model

// Contagens agregadas de um usuario (visao admin)
data class AdminUserStats(
    val taskCount: Int,
    val subjectCount: Int,
    val faltaCount: Int,
    val eventCount: Int,
)
