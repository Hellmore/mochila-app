package br.com.mochila.data

import java.sql.*

object DatabaseHelper {

    private const val DB_NAME = "mochila.db"

    // 🔹 Conecta ao banco e inicializa se necessário
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

    // 🔹 Cria as tabelas se o banco estiver vazio
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

    // ============================================================
    // 🔹 Funções auxiliares para uso direto nas telas
    // ============================================================

    /**
     * Executa consultas SELECT no banco (retorna ResultSet)
     *
     * @param sql comando SQL (ex: "SELECT * FROM disciplina WHERE id_usuario = ?")
     * @param params lista de parâmetros (opcional)
     */
    fun executeQuery(sql: String, params: List<Any> = emptyList()): ResultSet? {
        val conn = connect() ?: return null
        return try {
            val stmt = conn.prepareStatement(sql)
            params.forEachIndexed { index, param -> stmt.setObject(index + 1, param) }
            stmt.executeQuery()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Executa comandos INSERT, UPDATE ou DELETE
     *
     * @param sql comando SQL (ex: "INSERT INTO disciplina (...) VALUES (?, ?, ...)")
     * @param params lista de parâmetros (opcional)
     * @return true se o comando for executado com sucesso
     */
    fun executeUpdate(sql: String, params: List<Any> = emptyList()): Boolean {
        val conn = connect() ?: return false
        return try {
            val stmt = conn.prepareStatement(sql)
            params.forEachIndexed { index, param -> stmt.setObject(index + 1, param) }
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            close()
        }
    }

    // 🔹 Fecha a conexão (SQLite fecha automaticamente ao sair de escopo)
    fun close() {
        // Nenhuma ação necessária para SQLite
    }
}
