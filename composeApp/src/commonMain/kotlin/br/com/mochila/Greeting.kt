package br.com.mochila

// Exemplo de uso multiplataforma
class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}