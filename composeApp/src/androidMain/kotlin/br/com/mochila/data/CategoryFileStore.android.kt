package br.com.mochila.data

import java.io.File

internal actual fun readAppDataFile(fileName: String): String? {
    val file = File(AppDataDir.resolve(), fileName)
    if (!file.exists()) return null
    return runCatching { file.readText() }.getOrNull()
}

internal actual fun writeAppDataFile(fileName: String, content: String) {
    val dir = File(AppDataDir.resolve())
    dir.mkdirs()
    File(dir, fileName).writeText(content)
}
