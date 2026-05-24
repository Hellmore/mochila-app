package br.com.mochila.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

actual suspend fun checkDisposableApi(domain: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://api.mailcheck.ai/domain/$domain")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 3000
        conn.readTimeout = 3000
        conn.setRequestProperty("Accept", "application/json")
        val body = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        body.contains("\"disposable\":true")
    } catch (e: Exception) {
        false
    }
}
