package br.com.mochila

// Abstracao da plataforma (Android ou desktop)
interface Platform {
    val name: String
}

// Implementacao especifica por target
expect fun getPlatform(): Platform