package br.com.mochila.data

import io.mockk.*
import org.junit.After
import org.junit.Before
import java.sql.Connection
import java.sql.DriverManager

/**
 * Base class for repository integration tests using an in-memory SQLite database.
 *
 * Uses shared-cache URI mode so the in-memory DB stays alive across multiple
 * connections (repositories call DatabaseHelper.connect() which opens/closes
 * one connection per operation).
 */
abstract class RepositoryTestBase {

    private lateinit var holderConn: Connection

    companion object {
        private const val DB_URL = "jdbc:sqlite:file:testmochila?mode=memory&cache=shared"

        private val SCHEMA = """
            CREATE TABLE IF NOT EXISTS usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                senha TEXT NOT NULL,
                email_verificado INTEGER DEFAULT 0,
                foto_perfil TEXT,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS disciplina (
                id_disciplina INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                nome TEXT NOT NULL,
                professor TEXT NOT NULL,
                frequencia_minima INTEGER NOT NULL,
                data_inicio DATETIME NOT NULL,
                data_fim DATETIME NOT NULL,
                hora_aula INTEGER NOT NULL,
                semestre TEXT NOT NULL,
                cor_rgb INTEGER,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS tarefa (
                id_tarefa INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                titulo TEXT NOT NULL,
                descricao TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'Pendente',
                blockers TEXT,
                data_limite DATETIME,
                id_disciplina INTEGER REFERENCES disciplina(id_disciplina) ON DELETE SET NULL,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS evento (
                id_evento INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                titulo TEXT NOT NULL,
                descricao TEXT,
                data_evento DATETIME NOT NULL,
                status TEXT NOT NULL DEFAULT 'Agendado',
                id_disciplina INTEGER,
                cor_rgb INTEGER,
                lembrete_minutos INTEGER,
                lembrete_exibido INTEGER DEFAULT 0,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS falta (
                id_falta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                id_disciplina INTEGER NOT NULL,
                data_falta DATE NOT NULL,
                status TEXT NOT NULL DEFAULT 'Nao Justificada',
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
                atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS token_recuperacao (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL,
                token TEXT NOT NULL,
                expira_em DATETIME NOT NULL,
                usado INTEGER DEFAULT 0,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS administrador (
                id_adm INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL UNIQUE,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS log_acao (
                id_acao INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER,
                acao TEXT NOT NULL,
                descricao TEXT,
                tabela_afetada TEXT NOT NULL,
                id_registro_afetado INTEGER,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS log_erro (
                id_erro INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER,
                modulo TEXT NOT NULL,
                mensagem TEXT NOT NULL,
                criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()
    }

    @Before
    open fun setUpBase() {
        holderConn = DriverManager.getConnection(DB_URL)
        val stmt = holderConn.createStatement()
        SCHEMA.split(";").map { it.trim() }.filter { it.isNotEmpty() }.forEach { stmt.execute(it) }
        stmt.close()

        mockkObject(DatabaseHelper)
        every { DatabaseHelper.connect() } answers { DriverManager.getConnection(DB_URL) }
    }

    @After
    open fun tearDownBase() {
        if (::holderConn.isInitialized && !holderConn.isClosed) {
            val stmt = holderConn.createStatement()
            stmt.execute("DELETE FROM log_erro")
            stmt.execute("DELETE FROM log_acao")
            stmt.execute("DELETE FROM administrador")
            stmt.execute("DELETE FROM token_recuperacao")
            stmt.execute("DELETE FROM falta")
            stmt.execute("DELETE FROM evento")
            stmt.execute("DELETE FROM tarefa")
            stmt.execute("DELETE FROM disciplina")
            stmt.execute("DELETE FROM usuario")
            stmt.close()
            holderConn.close()
        }
        unmockkAll()
    }
}
