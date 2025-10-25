package br.com.mochila

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform