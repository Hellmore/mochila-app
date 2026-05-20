package br.com.mochila.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateValidatorTest {

    // ── Formato ──────────────────────────────────────────────────────────

    @Test fun `formato invalido sem barras retorna false`() {
        assertFalse(DateValidator.isValid("31122025"))
    }

    @Test fun `formato com separador errado retorna false`() {
        assertFalse(DateValidator.isValid("31-12-2025"))
    }

    @Test fun `formato correto com data valida retorna true`() {
        assertTrue(DateValidator.isValid("15/06/2025"))
    }

    // ── Meses ────────────────────────────────────────────────────────────

    @Test fun `mes zero retorna false`() {
        assertFalse(DateValidator.isValid("01/00/2025"))
    }

    @Test fun `mes 13 retorna false`() {
        assertFalse(DateValidator.isValid("01/13/2025"))
    }

    @Test fun `mes 12 com dia valido retorna true`() {
        assertTrue(DateValidator.isValid("31/12/2025"))
    }

    // ── Dias ─────────────────────────────────────────────────────────────

    @Test fun `dia zero retorna false`() {
        assertFalse(DateValidator.isValid("00/01/2025"))
    }

    @Test fun `dia 32 em mes com 31 dias retorna false`() {
        assertFalse(DateValidator.isValid("32/01/2025"))
    }

    @Test fun `dia 31 em mes com 30 dias retorna false`() {
        assertFalse(DateValidator.isValid("31/04/2025"))
    }

    @Test fun `dia 30 em mes com 30 dias retorna true`() {
        assertTrue(DateValidator.isValid("30/04/2025"))
    }

    // ── Fevereiro e bissexto ─────────────────────────────────────────────

    @Test fun `fevereiro dia 29 em ano nao bissexto retorna false`() {
        assertFalse(DateValidator.isValid("29/02/2025"))
    }

    @Test fun `fevereiro dia 29 em ano bissexto retorna true`() {
        assertTrue(DateValidator.isValid("29/02/2024"))
    }

    @Test fun `fevereiro dia 28 em ano nao bissexto retorna true`() {
        assertTrue(DateValidator.isValid("28/02/2025"))
    }

    @Test fun `ano divisivel por 100 mas nao por 400 nao e bissexto`() {
        assertFalse(DateValidator.isValid("29/02/1900"))
    }

    @Test fun `ano divisivel por 400 e bissexto`() {
        assertTrue(DateValidator.isValid("29/02/2000"))
    }
}
