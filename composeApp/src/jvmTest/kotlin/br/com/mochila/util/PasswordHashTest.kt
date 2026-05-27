package br.com.mochila.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

// Testa hash e verificacao de senhas com PBKDF2
class PasswordHashTest {

    // Hash gerado nao pode ser vazio
    @Test fun `hash retorna string nao vazia`() {
        val result = PasswordHash.hash("Senha@123")
        assertTrue(result.isNotEmpty())
    }

    // Formato armazenado deve ser salt:hash
    @Test fun `hash contem separador salt e hash`() {
        val result = PasswordHash.hash("Senha@123")
        assertTrue(result.contains(":"), "Formato esperado: salt:hash")
    }

    // Senha correta deve ser aceita na verificacao
    @Test fun `verify retorna true para senha correta`() {
        val stored = PasswordHash.hash("Senha@123")
        assertTrue(PasswordHash.verify("Senha@123", stored))
    }

    // Senha incorreta deve ser rejeitada
    @Test fun `verify retorna false para senha incorreta`() {
        val stored = PasswordHash.hash("Senha@123")
        assertFalse(PasswordHash.verify("SenhaErrada", stored))
    }

    // Salt aleatorio gera hashes diferentes para a mesma senha
    @Test fun `dois hashes da mesma senha sao diferentes por causa do salt`() {
        val hash1 = PasswordHash.hash("Senha@123")
        val hash2 = PasswordHash.hash("Senha@123")
        assertNotEquals(hash1, hash2)
    }

    // Valor armazenado invalido deve falhar na verificacao
    @Test fun `verify retorna false para stored invalido sem separador`() {
        assertFalse(PasswordHash.verify("qualquercoisa", "hashsemsepar"))
    }
}
