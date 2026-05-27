package br.com.mochila.data

import java.io.File

// Diretorio padrao no Android: pasta temporaria do sistema
internal actual fun defaultAppDataDir(): String {
    val dir = File(System.getProperty("java.io.tmpdir"), "mochila")
    dir.mkdirs()
    return dir.absolutePath
}
