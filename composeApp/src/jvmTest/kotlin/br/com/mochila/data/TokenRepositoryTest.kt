package br.com.mochila.data

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Testa salvamento e validacao de tokens de recuperacao
class TokenRepositoryTest : RepositoryTestBase() {

    // Salvamento de token
    @Test fun `saveToken retorna true`() {
        assertTrue(TokenRepository.saveToken("a@b.com", "123456"))
    }

    // Validacao e consumo de token
    @Test fun `validateAndConsumeToken retorna true para token valido`() {
        TokenRepository.saveToken("a@b.com", "654321")
        assertTrue(TokenRepository.validateAndConsumeToken("a@b.com", "654321"))
    }

    @Test fun `validateAndConsumeToken retorna false para token errado`() {
        TokenRepository.saveToken("a@b.com", "111111")
        assertFalse(TokenRepository.validateAndConsumeToken("a@b.com", "999999"))
    }

    @Test fun `validateAndConsumeToken retorna false para token ja consumido`() {
        TokenRepository.saveToken("a@b.com", "222222")
        TokenRepository.validateAndConsumeToken("a@b.com", "222222")
        assertFalse(TokenRepository.validateAndConsumeToken("a@b.com", "222222"))
    }

    @Test fun `validateAndConsumeToken retorna false para email diferente`() {
        TokenRepository.saveToken("a@b.com", "333333")
        assertFalse(TokenRepository.validateAndConsumeToken("outro@b.com", "333333"))
    }
}
