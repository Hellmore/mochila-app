package br.com.mochila.data

/**
 * Diretório base para dados locais fora do SQLite (ex.: migração de caches legados).
 * No Android, chame [init] em [androidMain] antes de conectar ao banco.
 */
object AppDataDir {
    private var appPath: String? = null
    private var testOverride: String? = null

    fun init(absolutePath: String) {
        appPath = absolutePath
    }

    internal fun setPathForTests(absolutePath: String) {
        testOverride = absolutePath
    }

    internal fun clearTestPath() {
        testOverride = null
    }

    internal fun resolve(): String {
        testOverride?.let { return it }
        appPath?.let { return it }
        return defaultAppDataDir()
    }
}

internal expect fun defaultAppDataDir(): String
