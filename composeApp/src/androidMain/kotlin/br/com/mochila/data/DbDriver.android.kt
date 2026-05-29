package br.com.mochila.data

import android.content.res.AssetManager

private var androidAssets: AssetManager? = null

internal fun initAndroidAssets(am: AssetManager) {
    androidAssets = am
}

private var sqlDroidRegistered = false

internal actual fun jdbcConnectionUrl(dbPath: String): String {
    if (!sqlDroidRegistered) {
        try {
            // Class.forName sozinho nao e suficiente no Android — registrar explicitamente
            val driver = Class.forName("org.sqldroid.SQLDroidDriver").getDeclaredConstructor().newInstance()
                    as java.sql.Driver
            java.sql.DriverManager.registerDriver(driver)
            sqlDroidRegistered = true
        } catch (e: Exception) {
            println("⚠️ SQLDroid registerDriver: ${e.message}")
        }
    }
    return "jdbc:sqldroid:$dbPath"
}

internal actual fun readDbInitScript(): String? =
    try {
        androidAssets
            ?.open("composeResources/mochila_app.composeapp.generated.resources/files/db_init.sql")
            ?.bufferedReader()
            ?.readText()
    } catch (e: Exception) {
        null
    }
