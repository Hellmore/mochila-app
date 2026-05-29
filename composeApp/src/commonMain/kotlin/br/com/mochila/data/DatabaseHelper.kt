package br.com.mochila.data

import br.com.mochila.model.EventCategory
import br.com.mochila.model.TaskCategory
import java.io.File
import java.sql.*

// Acesso centralizado ao SQLite: conexao, migracoes e execucao de SQL
object DatabaseHelper {

    private val dbPath get() = "${AppDataDir.resolve()}/mochila.db"

    // Abre conexao e garante schema inicializado
    fun connect(): Connection? {
        return try {
            val conn = DriverManager.getConnection(jdbcConnectionUrl(dbPath))
            initializeDatabase(conn)
            conn
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Cria tabelas na primeira execucao e aplica migracoes incrementais
    private fun initializeDatabase(conn: Connection) {
        try {
            val rs: ResultSet = conn.createStatement()
                .executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usuario'")
            val tableExists = rs.next()
            rs.close()

            if (!tableExists) {
                println("🧱 Criando estrutura inicial do banco...")

                val script = readDbInitScript()
                if (script != null) {
                    splitSqlStatements(script).forEach { sqlStatement ->
                        try {
                            conn.createStatement().use { it.execute(sqlStatement) }
                        } catch (e: Exception) {
                            println("⚠️ SQL init skipped: ${e.message?.take(80)}")
                        }
                    }
                    println("✅ Banco inicializado com sucesso!")
                } else {
                    println("⚠️ Arquivo db_init.sql não encontrado nos recursos.")
                }
            } else {
                println("📚 Banco já inicializado.")
            }

            runMigrations(conn)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Adiciona colunas e tabelas ausentes em bancos ja existentes
    private fun runMigrations(conn: Connection) {
        try {
            val stmt = conn.createStatement()

            // Tabela de tokens para recuperacao de senha
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

            // Coluna de e-mail verificado em usuario
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

            // Foto de perfil do usuario
            val colsFoto = conn.createStatement().executeQuery("PRAGMA table_info(usuario)")
            var hasFotoPerfil = false
            while (colsFoto.next()) {
                if (colsFoto.getString("name") == "foto_perfil") { hasFotoPerfil = true; break }
            }
            colsFoto.close()
            if (!hasFotoPerfil) {
                stmt.execute("ALTER TABLE usuario ADD COLUMN foto_perfil TEXT")
            }

            // Cor RGB da disciplina
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

            // Vinculo opcional de tarefa com disciplina
            val colsTarefa = conn.createStatement().executeQuery("PRAGMA table_info(tarefa)")
            var hasTarefaIdDisciplina = false
            while (colsTarefa.next()) {
                if (colsTarefa.getString("name") == "id_disciplina") { hasTarefaIdDisciplina = true; break }
            }
            colsTarefa.close()
            if (!hasTarefaIdDisciplina) {
                stmt.execute("ALTER TABLE tarefa ADD COLUMN id_disciplina INTEGER REFERENCES disciplina(id_disciplina) ON DELETE SET NULL")
            }

            // Campos extras de evento (disciplina, cor, lembrete)
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

            // Categoria do evento (prova, seminario, etc.)
            val colsEventoCat = conn.createStatement().executeQuery("PRAGMA table_info(evento)")
            var hasCategoriaEvento = false
            while (colsEventoCat.next()) {
                if (colsEventoCat.getString("name") == "categoria_evento") {
                    hasCategoriaEvento = true
                    break
                }
            }
            colsEventoCat.close()
            if (!hasCategoriaEvento) {
                stmt.execute(
                    "ALTER TABLE evento ADD COLUMN categoria_evento TEXT NOT NULL DEFAULT 'PROVA'"
                )
            }

            // Categoria e prioridade da tarefa
            val colsTarefaMeta = conn.createStatement().executeQuery("PRAGMA table_info(tarefa)")
            var hasCategoriaTarefa = false
            var hasPrioridade = false
            while (colsTarefaMeta.next()) {
                when (colsTarefaMeta.getString("name")) {
                    "categoria_tarefa" -> hasCategoriaTarefa = true
                    "prioridade" -> hasPrioridade = true
                }
            }
            colsTarefaMeta.close()
            if (!hasCategoriaTarefa) {
                stmt.execute(
                    "ALTER TABLE tarefa ADD COLUMN categoria_tarefa TEXT NOT NULL DEFAULT 'TAREFA_DE_CASA'"
                )
            }
            if (!hasPrioridade) {
                stmt.execute("ALTER TABLE tarefa ADD COLUMN prioridade TEXT NOT NULL DEFAULT 'MEDIA'")
            }

            // Aulas por semana na disciplina
            val colsDiscAula = conn.createStatement().executeQuery("PRAGMA table_info(disciplina)")
            var hasAulaSemana = false
            while (colsDiscAula.next()) {
                if (colsDiscAula.getString("name") == "aula_semana") {
                    hasAulaSemana = true
                    break
                }
            }
            colsDiscAula.close()
            if (!hasAulaSemana) {
                stmt.execute("ALTER TABLE disciplina ADD COLUMN aula_semana INTEGER NOT NULL DEFAULT 1")
            }

            // Converte hora_aula legada para minutos
            val colsDiscMin = conn.createStatement().executeQuery("PRAGMA table_info(disciplina)")
            var hasHoraAulaEmMinutos = false
            while (colsDiscMin.next()) {
                if (colsDiscMin.getString("name") == "hora_aula_em_minutos") {
                    hasHoraAulaEmMinutos = true; break
                }
            }
            colsDiscMin.close()
            if (!hasHoraAulaEmMinutos) {
                stmt.execute("UPDATE disciplina SET hora_aula = hora_aula * 60 WHERE hora_aula > 0 AND hora_aula < 60")
                stmt.execute("ALTER TABLE disciplina ADD COLUMN hora_aula_em_minutos INTEGER DEFAULT 1")
            }

            migrateLegacyCacheFiles(conn)
            // Tabelas de administracao, logs e notificacoes
            stmt.execute(
                """CREATE TABLE IF NOT EXISTS administrador (
                    id_adm INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_usuario INTEGER NOT NULL UNIQUE,
                    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
                        ON DELETE CASCADE ON UPDATE CASCADE
                )"""
            )

            stmt.execute(
                """CREATE TABLE IF NOT EXISTS log_acao (
                    id_acao INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_usuario INTEGER NOT NULL,
                    acao TEXT NOT NULL,
                    descricao TEXT,
                    ip_origem TEXT,
                    tabela_afetada TEXT NOT NULL,
                    id_registro_afetado INTEGER,
                    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
                        ON DELETE CASCADE ON UPDATE CASCADE
                )"""
            )

            stmt.execute(
                """CREATE TABLE IF NOT EXISTS log_erro (
                    id_erro INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_usuario INTEGER,
                    modulo TEXT NOT NULL,
                    mensagem TEXT NOT NULL,
                    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
                        ON DELETE CASCADE ON UPDATE CASCADE
                )"""
            )

            stmt.execute(
                """CREATE TABLE IF NOT EXISTS notificacao (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_usuario INTEGER NOT NULL,
                    tipo TEXT NOT NULL,
                    mensagem TEXT NOT NULL,
                    lida INTEGER NOT NULL DEFAULT 0,
                    referencia_id INTEGER,
                    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
                        ON DELETE CASCADE ON UPDATE CASCADE
                )"""
            )

            // Recria notificacao se schema antigo nao tiver coluna tipo
            val colsNotif = conn.createStatement().executeQuery("PRAGMA table_info(notificacao)")
            var hasNotifTipo = false
            while (colsNotif.next()) {
                if (colsNotif.getString("name") == "tipo") { hasNotifTipo = true; break }
            }
            colsNotif.close()
            if (!hasNotifTipo) {
                stmt.execute("DROP TABLE IF EXISTS notificacao")
                stmt.execute(
                    """CREATE TABLE notificacao (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_usuario INTEGER NOT NULL,
                        tipo TEXT NOT NULL,
                        mensagem TEXT NOT NULL,
                        lida INTEGER NOT NULL DEFAULT 0,
                        referencia_id INTEGER,
                        criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
                            ON DELETE CASCADE ON UPDATE CASCADE
                    )"""
                )
            }

            stmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Importa arquivos de cache legados para o banco e remove os arquivos
    private fun migrateLegacyCacheFiles(conn: Connection) {
        try {
            migrateEnumCacheFile(
                conn = conn,
                fileName = "event_categories.txt",
                table = "evento",
                idColumn = "id_evento",
                valueColumn = "categoria_evento",
            ) { raw ->
                EventCategory.fromNameOrNull(raw)?.name
            }
            migrateEnumCacheFile(
                conn = conn,
                fileName = "task_categories.txt",
                table = "tarefa",
                idColumn = "id_tarefa",
                valueColumn = "categoria_tarefa",
            ) { raw ->
                TaskCategory.fromNameOrNull(raw)?.name
            }
            migrateIntCacheFile(
                conn = conn,
                fileName = "subject_weekly_frequency.txt",
                table = "disciplina",
                idColumn = "id_disciplina",
                valueColumn = "aula_semana",
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Atualiza coluna enum a partir de arquivo id=valor
    private fun migrateEnumCacheFile(
        conn: Connection,
        fileName: String,
        table: String,
        idColumn: String,
        valueColumn: String,
        toDbValue: (String) -> String?,
    ) {
        val file = File(AppDataDir.resolve(), fileName)
        if (!file.exists()) return
        val content = runCatching { file.readText() }.getOrNull() ?: return
        val update = conn.prepareStatement(
            "UPDATE $table SET $valueColumn = ? WHERE $idColumn = ?"
        )
        content.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEach
            val sep = trimmed.indexOf('=')
            if (sep <= 0) return@forEach
            val id = trimmed.substring(0, sep).toIntOrNull() ?: return@forEach
            val dbValue = toDbValue(trimmed.substring(sep + 1).trim()) ?: return@forEach
            update.setString(1, dbValue)
            update.setInt(2, id)
            update.executeUpdate()
        }
        update.close()
        file.delete()
    }

    // Atualiza coluna inteira a partir de arquivo id=valor
    private fun migrateIntCacheFile(
        conn: Connection,
        fileName: String,
        table: String,
        idColumn: String,
        valueColumn: String,
    ) {
        val file = File(AppDataDir.resolve(), fileName)
        if (!file.exists()) return
        val content = runCatching { file.readText() }.getOrNull() ?: return
        val update = conn.prepareStatement(
            "UPDATE $table SET $valueColumn = ? WHERE $idColumn = ?"
        )
        content.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEach
            val sep = trimmed.indexOf('=')
            if (sep <= 0) return@forEach
            val id = trimmed.substring(0, sep).toIntOrNull() ?: return@forEach
            val count = trimmed.substring(sep + 1).trim().toIntOrNull()?.takeIf { it > 0 } ?: return@forEach
            update.setInt(1, count)
            update.setInt(2, id)
            update.executeUpdate()
        }
        update.close()
        file.delete()
    }

    // Divide script SQL em statements individuais respeitando blocos BEGIN...END (triggers)
    private fun splitSqlStatements(script: String): List<String> {
        val statements = mutableListOf<String>()
        var depth = 0
        val current = StringBuilder()

        for (line in script.lines()) {
            val upper = line.trim().uppercase().trimEnd(';')
            when (upper) {
                "BEGIN" -> depth++
                "END" -> if (depth > 0) depth--
            }
            current.appendLine(line)
            if (depth == 0 && line.trim().endsWith(";")) {
                val stmt = current.toString().trim().trimEnd(';').trim()
                if (stmt.isNotEmpty() && !stmt.startsWith("--")) statements.add(stmt)
                current.clear()
            }
        }
        val last = current.toString().trim().trimEnd(';').trim()
        if (last.isNotEmpty() && !last.startsWith("--")) statements.add(last)
        return statements
    }

    // Executa SELECT parametrizado e retorna linhas como mapas
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

    // Executa INSERT/UPDATE/DELETE parametrizado
    fun executeUpdate(sql: String, params: List<Any?> = emptyList()): Boolean {
        val conn = connect() ?: return false
        return try {
            val stmt = conn.prepareStatement(sql)
            params.forEachIndexed { index, param ->
                if (param == null) stmt.setNull(index + 1, java.sql.Types.NULL)
                else stmt.setObject(index + 1, param)
            }
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try { conn.close() } catch (_: Exception) {}
        }
    }

    // Encerra conexao (reservado para uso futuro)
    fun close() {
        
    }
}
