package br.com.mochila.data

import org.junit.Test
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Testa registro e listagem de logs de acao e erro
class LogRepositoryTest : RepositoryTestBase() {

    private val DB_URL = "jdbc:sqlite:file:testmochila?mode=memory&cache=shared"

    private fun insertUser(email: String = "logger@test.com"): Int {
        val conn = DriverManager.getConnection(DB_URL)
        val stmt = conn.prepareStatement("INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)")
        stmt.setString(1, "Logger")
        stmt.setString(2, email)
        stmt.setString(3, "hash")
        stmt.executeUpdate()
        val rs = conn.createStatement().executeQuery("SELECT last_insert_rowid()")
        val id = if (rs.next()) rs.getInt(1) else -1
        rs.close(); stmt.close(); conn.close()
        return id
    }

    // Logs de acao
    @Test fun `insertAcao e listAcoes retorna log com campos corretos`() {
        val uid = insertUser()
        LogRepository.insertAcao(uid, "CRIAR_TAREFA", "tarefa", 42)
        val logs = LogRepository.listAcoes()
        assertEquals(1, logs.size)
        assertEquals(uid, logs[0].userId)
        assertEquals("CRIAR_TAREFA", logs[0].acao)
        assertEquals("tarefa", logs[0].tabelaAfetada)
        assertEquals(42, logs[0].idRegistroAfetado)
    }

    @Test fun `insertAcao sem idRegistro salva com idRegistroAfetado nulo`() {
        val uid = insertUser()
        LogRepository.insertAcao(uid, "ACAO", "tabela")
        val logs = LogRepository.listAcoes()
        assertEquals(1, logs.size)
        assertNull(logs[0].idRegistroAfetado)
    }

    // Logs de erro
    @Test fun `insertErro e listErros retorna log com campos corretos`() {
        val uid = insertUser()
        LogRepository.insertErro("MeuModulo", "Mensagem de erro", uid)
        val erros = LogRepository.listErros()
        assertEquals(1, erros.size)
        assertEquals("MeuModulo", erros[0].modulo)
        assertEquals("Mensagem de erro", erros[0].mensagem)
        assertEquals(uid, erros[0].userId)
    }

    @Test fun `insertErro sem userId salva com userId nulo`() {
        LogRepository.insertErro("Modulo", "Erro sem usuario")
        val erros = LogRepository.listErros()
        assertEquals(1, erros.size)
        assertNull(erros[0].userId)
    }

    // Paginacao e listas vazias
    @Test fun `listAcoes respeita parametro limit`() {
        val uid = insertUser()
        repeat(5) { LogRepository.insertAcao(uid, "ACAO", "tabela") }
        val logs = LogRepository.listAcoes(limit = 3)
        assertEquals(3, logs.size)
    }

    @Test fun `listErros retorna lista vazia quando nenhum erro inserido`() {
        assertTrue(LogRepository.listErros().isEmpty())
    }

    @Test fun `listAcoes retorna lista vazia quando nenhuma acao inserida`() {
        assertTrue(LogRepository.listAcoes().isEmpty())
    }
}
