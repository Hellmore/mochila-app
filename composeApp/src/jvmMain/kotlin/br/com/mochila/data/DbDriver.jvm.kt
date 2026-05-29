package br.com.mochila.data

internal actual fun jdbcConnectionUrl(dbPath: String): String =
    "jdbc:sqlite:mochila.db"

internal actual fun readDbInitScript(): String? =
    DatabaseHelper::class.java.getResource("/files/db_init.sql")?.readText()
