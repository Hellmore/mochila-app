package br.com.mochila.data

import org.junit.Test
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdminRepositoryTest : RepositoryTestBase() {

    private val DB_URL = "jdbc:sqlite:file:testmochila?mode=memory&cache=shared"

    private fun insertUser(email: String = "user@test.com"): Int {
        val conn = DriverManager.getConnection(DB_URL)
        val stmt = conn.prepareStatement("INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)")
        stmt.setString(1, "Teste")
        stmt.setString(2, email)
        stmt.setString(3, "hash")
        stmt.executeUpdate()
        val rs = conn.createStatement().executeQuery("SELECT last_insert_rowid()")
        val id = if (rs.next()) rs.getInt(1) else -1
        rs.close(); stmt.close(); conn.close()
        return id
    }

    private fun insertTask(userId: Int) {
        val conn = DriverManager.getConnection(DB_URL)
        val stmt = conn.prepareStatement(
            "INSERT INTO tarefa (id_usuario, titulo, descricao) VALUES (?, ?, ?)"
        )
        stmt.setInt(1, userId)
        stmt.setString(2, "Tarefa")
        stmt.setString(3, "Desc")
        stmt.executeUpdate()
        stmt.close(); conn.close()
    }

    @Test fun `isAdmin retorna false para usuario sem registro admin`() {
        val uid = insertUser()
        assertFalse(AdminRepository.isAdmin(uid))
    }

    @Test fun `isAdmin retorna true apos promoteToAdmin`() {
        val uid = insertUser()
        AdminRepository.promoteToAdmin(uid)
        assertTrue(AdminRepository.isAdmin(uid))
    }

    @Test fun `promoteToAdmin retorna true para usuario valido`() {
        val uid = insertUser()
        assertTrue(AdminRepository.promoteToAdmin(uid))
    }

    @Test fun `promoteToAdmin duplo nao falha por causa do INSERT OR IGNORE`() {
        val uid = insertUser()
        AdminRepository.promoteToAdmin(uid)
        AdminRepository.promoteToAdmin(uid)
        assertTrue(AdminRepository.isAdmin(uid))
    }

    @Test fun `demoteFromAdmin retorna true e remove o status de admin`() {
        val uid = insertUser()
        AdminRepository.promoteToAdmin(uid)
        assertTrue(AdminRepository.demoteFromAdmin(uid))
        assertFalse(AdminRepository.isAdmin(uid))
    }

    @Test fun `getUserStats retorna zeros para usuario sem dados`() {
        val uid = insertUser()
        val stats = AdminRepository.getUserStats(uid)
        assertEquals(0, stats.taskCount)
        assertEquals(0, stats.subjectCount)
        assertEquals(0, stats.faltaCount)
        assertEquals(0, stats.eventCount)
    }

    @Test fun `getUserStats conta corretamente as tarefas do usuario`() {
        val uid = insertUser()
        insertTask(uid)
        insertTask(uid)
        val stats = AdminRepository.getUserStats(uid)
        assertEquals(2, stats.taskCount)
        assertEquals(0, stats.subjectCount)
    }
}
