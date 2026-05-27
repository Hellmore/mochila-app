package br.com.mochila.data

// Diretorio de dados do app (cache, arquivos auxiliares)
object AppDataDir {
    private var appPath: String? = null
    private var testOverride: String? = null

    // Define caminho absoluto na inicializacao da plataforma
    fun init(absolutePath: String) {
        appPath = absolutePath
    }

    // Sobrescreve caminho em testes automatizados
    internal fun setPathForTests(absolutePath: String) {
        testOverride = absolutePath
    }

    // Remove override de teste
    internal fun clearTestPath() {
        testOverride = null
    }

    // Retorna caminho efetivo: teste, init ou padrao da plataforma
    internal fun resolve(): String {
        testOverride?.let { return it }
        appPath?.let { return it }
        return defaultAppDataDir()
    }
}

// Implementacao especifica por plataforma (Android/JVM)
internal expect fun defaultAppDataDir(): String
