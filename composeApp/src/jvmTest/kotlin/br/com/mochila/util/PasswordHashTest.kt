package br.com.mochila.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHashTest {

    @Test fun `hash retorna string nao vazia`() {
        val result = PasswordHash.hash("Senha@123")
        assertTrue(result.isNotEmpty())
    }

    @Test fun `hash contem separador salt e hash`() {
        val result = PasswordHash.hash("Senha@123")
        assertTrue(result.contains(":"), "Formato esperado: salt:hash")
    }

    @Test fun `verify retorna true para senha correta`() {
        val stored = PasswordHash.hash("Senha@123")
        assertTrue(PasswordHash.verify("Senha@123", stored))
    }

    @Test fun `verify retorna false para senha incorreta`() {
        val stored = PasswordHash.hash("Senha@123")
        assertFalse(PasswordHash.verify("SenhaErrada", stored))
    }

    @Test fun `dois hashes da mesma senha sao diferentes por causa do salt`() {
        val hash1 = PasswordHash.hash("Senha@123")
        val hash2 = PasswordHash.hash("Senha@123")
        assertNotEquals(hash1, hash2)
    }

    @Test fun `verify retorna false para stored invalido sem separador`() {
        assertFalse(PasswordHash.verify("qualquercoisa", "hashsemsepar"))
    }
}
