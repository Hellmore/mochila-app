package br.com.mochila.data

import java.io.File

// Diretorio padrao no desktop: ~/.mochila
internal actual fun defaultAppDataDir(): String {
    val dir = File(System.getProperty("user.home"), ".mochila")
    dir.mkdirs()
    return dir.absolutePath
}
