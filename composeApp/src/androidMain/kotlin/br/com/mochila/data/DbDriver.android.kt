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

internal actual fun readDbInitScript(): String? {
    val assets = androidAssets ?: return null

    // Busca dinamicamente dentro de composeResources/ para não depender do nome gerado pelo plugin
    try {
        val dirs = assets.list("composeResources") ?: emptyArray()
        for (dir in dirs) {
            try {
                return assets.open("composeResources/$dir/files/db_init.sql")
                    .bufferedReader().readText()
            } catch (_: Exception) {}
        }
    } catch (_: Exception) {}

    // Fallbacks para paths alternativos
    for (path in listOf("files/db_init.sql", "db_init.sql")) {
        try {
            return assets.open(path).bufferedReader().readText()
        } catch (_: Exception) {}
    }

    println("⚠️ db_init.sql não encontrado nos assets do Android")
    return null
}
