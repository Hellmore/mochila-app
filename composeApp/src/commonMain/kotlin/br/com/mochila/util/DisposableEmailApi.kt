package br.com.mochila.util

// Consulta API de dominios descartaveis (implementacao por plataforma)
expect suspend fun checkDisposableApi(domain: String): Boolean
