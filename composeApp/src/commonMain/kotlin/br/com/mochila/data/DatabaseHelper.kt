package br.com.mochila.data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object DatabaseHelper {

    private const val DB_NAME = "mochila.db"

    fun connect(): Connection? {
        return try {
            val conn = DriverManager.getConnection("jdbc:sqlite:$DB_NAME")
            initializeDatabase(conn)
            conn
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeDatabase(conn: Connection) {
        try {
            val meta = conn.metaData
            val rs: ResultSet = meta.getTables(null, null, "usuario", null)

            if (!rs.next()) {
                println("🧱 Criando estrutura inicial do banco...")

                val resourceUrl = this::class.java.getResource("/files/db_init.sql")
                println("🔍 Caminho do recurso: $resourceUrl")

                val script = resourceUrl?.readText()
                if (script != null) {
                    val stmt = conn.createStatement()
                    stmt.executeUpdate(script)
                    stmt.close()
                    println("✅ Banco inicializado com sucesso!")
                } else {
                    println("⚠️ Arquivo db_init.sql não encontrado nos recursos.")
                }
            } else {
                println("📚 Banco já inicializado.")
            }

            rs.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
        // SQLite fecha automaticamente ao encerrar a conexão
    }
}
