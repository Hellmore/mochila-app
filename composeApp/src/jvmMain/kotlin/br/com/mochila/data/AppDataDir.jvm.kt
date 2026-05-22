package br.com.mochila.data

import java.io.File

internal actual fun defaultAppDataDir(): String {
    val dir = File(System.getProperty("user.home"), ".mochila")
    dir.mkdirs()
    return dir.absolutePath
}
