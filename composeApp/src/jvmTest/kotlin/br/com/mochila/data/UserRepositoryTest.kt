package br.com.mochila.data

import br.com.mochila.model.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Testa cadastro, autenticacao e verificacao de email de usuarios
class UserRepositoryTest : RepositoryTestBase() {

    private fun insertUser(
        name: String = "Ana",
        email: String = "ana@test.com",
        password: String = "Senha@123",
    ) = UserRepository.insert(User(name = name, email = email, password = password))

    // Insercao e busca por email
    @Test fun `insert retorna true e usuario pode ser encontrado por email`() {
        assertTrue(insertUser())
        assertTrue(UserRepository.emailExists("ana@test.com"))
    }

    @Test fun `insert com email duplicado retorna false`() {
        insertUser()
        assertFalse(insertUser())
    }

    @Test fun `emailExists retorna false para email inexistente`() {
        assertFalse(UserRepository.emailExists("naoexiste@x.com"))
    }

    @Test fun `findByEmail retorna usuario correto`() {
        insertUser()
        val user = UserRepository.findByEmail("ana@test.com")
        assertNotNull(user)
        assertEquals("Ana", user.name)
    }

    @Test fun `findByEmail retorna null para email inexistente`() {
        assertNull(UserRepository.findByEmail("naoexiste@x.com"))
    }

    // Busca por id
    @Test fun `findById retorna usuario correto`() {
        insertUser()
        val user = UserRepository.findByEmail("ana@test.com")
        assertNotNull(user)
        val byId = UserRepository.findById(user.id)
        assertNotNull(byId)
        assertEquals("Ana", byId.name)
    }

    @Test fun `findById retorna null para id inexistente`() {
        assertNull(UserRepository.findById(99999))
    }

    // Atualizacao e exclusao
    @Test fun `update altera nome e email`() {
        insertUser()
        val user = UserRepository.findByEmail("ana@test.com")!!
        val updated = UserRepository.update(user.id, "AnaLuiza", "nova@test.com", null)
        assertTrue(updated)
        val found = UserRepository.findByEmail("nova@test.com")
        assertNotNull(found)
        assertEquals("AnaLuiza", found.name)
    }

    @Test fun `delete remove usuario`() {
        insertUser()
        val user = UserRepository.findByEmail("ana@test.com")!!
        assertTrue(UserRepository.delete(user.id))
        assertFalse(UserRepository.emailExists("ana@test.com"))
    }

    // Verificacao de email
    @Test fun `verifyEmail marca email como verificado`() {
        insertUser()
        assertFalse(UserRepository.isEmailVerified("ana@test.com"))
        assertTrue(UserRepository.verifyEmail("ana@test.com"))
        assertTrue(UserRepository.isEmailVerified("ana@test.com"))
    }

    // Login
    @Test fun `validateLogin retorna userId para credenciais corretas`() {
        insertUser(password = "Senha@123")
        val userId = UserRepository.validateLogin("ana@test.com", "Senha@123")
        assertNotNull(userId)
    }

    @Test fun `validateLogin retorna null para senha incorreta`() {
        insertUser(password = "Senha@123")
        val userId = UserRepository.validateLogin("ana@test.com", "SenhaErrada")
        assertNull(userId)
    }
}
