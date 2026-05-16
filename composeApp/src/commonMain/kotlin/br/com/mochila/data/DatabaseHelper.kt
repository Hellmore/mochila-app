package br.com.mochila.data

import java.sql.*

object DatabaseHelper {

    private const val DB_NAME = "mochila.db"

    // Conecta ao banco e inicializa se necessário
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

    // Cria as tabelas se o banco estiver vazio
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
                    // Executa o script SQL em lote, separando os comandos
                    script.split(';').forEach { sqlStatement ->
                        if (sqlStatement.trim().isNotEmpty()) {
                            stmt.addBatch(sqlStatement)
                        }
                    }
                    stmt.executeBatch()
                    stmt.close()
                    println("✅ Banco inicializado com sucesso!")
                } else {
                    println("⚠️ Arquivo db_init.sql não encontrado nos recursos.")
                }
            } else {
                println("📚 Banco já inicializado.")
            }

            rs.close()
            runMigrations(conn)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runMigrations(conn: Connection) {
        try {
            val stmt = conn.createStatement()

            stmt.execute(
                """CREATE TABLE IF NOT EXISTS token_recuperacao (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT NOT NULL,
                    token TEXT NOT NULL,
                    expira_em DATETIME NOT NULL,
                    usado INTEGER DEFAULT 0,
                    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
                )"""
            )

            // Adiciona email_verificado se ainda não existe; marca usuários antigos como verificados
            val columns = conn.createStatement()
                .executeQuery("PRAGMA table_info(usuario)")
            var hasEmailVerificado = false
            while (columns.next()) {
                if (columns.getString("name") == "email_verificado") {
                    hasEmailVerificado = true
                    break
                }
            }
            columns.close()

            if (!hasEmailVerificado) {
                stmt.execute("ALTER TABLE usuario ADD COLUMN email_verificado INTEGER DEFAULT 0")
                stmt.execute("UPDATE usuario SET email_verificado = 1")
            }

            val colsFoto = conn.createStatement().executeQuery("PRAGMA table_info(usuario)")
            var hasFotoPerfil = false
            while (colsFoto.next()) {
                if (colsFoto.getString("name") == "foto_perfil") { hasFotoPerfil = true; break }
            }
            colsFoto.close()
            if (!hasFotoPerfil) {
                stmt.execute("ALTER TABLE usuario ADD COLUMN foto_perfil TEXT")
            }

            val colsDisc = conn.createStatement().executeQuery("PRAGMA table_info(disciplina)")
            var hasCorRgb = false
            while (colsDisc.next()) {
                if (colsDisc.getString("name") == "cor_rgb") {
                    hasCorRgb = true
                    break
                }
            }
            colsDisc.close()
            if (!hasCorRgb) {
                stmt.execute("ALTER TABLE disciplina ADD COLUMN cor_rgb INTEGER")
                stmt.execute("UPDATE disciplina SET cor_rgb = 3710463 WHERE cor_rgb IS NULL")
            }

            val colsEvento = conn.createStatement().executeQuery("PRAGMA table_info(evento)")
            var hasIdDisciplina = false
            var hasEventCorRgb = false
            var hasLembreteMinutos = false
            var hasLembreteExibido = false
            while (colsEvento.next()) {
                when (colsEvento.getString("name")) {
                    "id_disciplina" -> hasIdDisciplina = true
                    "cor_rgb" -> hasEventCorRgb = true
                    "lembrete_minutos" -> hasLembreteMinutos = true
                    "lembrete_exibido" -> hasLembreteExibido = true
                }
            }
            colsEvento.close()
            if (!hasIdDisciplina) {
                stmt.execute("ALTER TABLE evento ADD COLUMN id_disciplina INTEGER REFERENCES disciplina(id_disciplina)")
            }
            if (!hasEventCorRgb) {
                stmt.execute("ALTER TABLE evento ADD COLUMN cor_rgb INTEGER")
                stmt.execute("UPDATE evento SET cor_rgb = 6643029 WHERE cor_rgb IS NULL")
            }
            if (!hasLembreteMinutos) {
                stmt.execute("ALTER TABLE evento ADD COLUMN lembrete_minutos INTEGER")
            }
            if (!hasLembreteExibido) {
                stmt.execute("ALTER TABLE evento ADD COLUMN lembrete_exibido INTEGER DEFAULT 0")
            }

            stmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ============================================================
    // Funções auxiliares
    // ============================================================

    /**
     * Executa consultas SELECT no banco (retorna ResultSet)
     *
     * @param sql comando SQL (ex: "SELECT * FROM disciplina WHERE id_usuario = ?")
     * @param params lista de parâmetros (opcional)
     */
    fun executeQuery(sql: String, params: List<Any> = emptyList()): List<Map<String, Any?>> {
        val conn = connect() ?: return emptyList()
        val results = mutableListOf<Map<String, Any?>>()

        try {
            val stmt = conn.prepareStatement(sql)
            params.forEachIndexed { index, param -> stmt.setObject(index + 1, param) }
            val rs = stmt.executeQuery()

            val columnCount = rs.metaData.columnCount

            while (rs.next()) {
                val row = mutableMapOf<String, Any?>()
                for (i in 1..columnCount) {
                    row[rs.metaData.getColumnName(i)] = rs.getObject(i)
                }
                results.add(row)
            }

            rs.close()
            stmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }

        return results
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

    // Fecha a conexão (SQLite fecha automaticamente ao sair de escopo)
    fun close() {
        // Nenhuma ação necessária para SQLite
    }
}
