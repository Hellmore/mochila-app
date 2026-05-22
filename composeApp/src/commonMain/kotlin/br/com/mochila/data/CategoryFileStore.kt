package br.com.mochila.data

internal object CategoryFileStore {
    fun read(fileName: String): String? = readAppDataFile(fileName)

    fun write(fileName: String, content: String) {
        writeAppDataFile(fileName, content)
    }
}

internal expect fun readAppDataFile(fileName: String): String?

internal expect fun writeAppDataFile(fileName: String, content: String)
