package br.com.mochila.data

import br.com.mochila.model.Falta
import br.com.mochila.model.Subject
import br.com.mochila.model.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FaltaRepositoryTest : RepositoryTestBase() {

    private val userId = 1
    private val subjectId = 1

    @org.junit.Before
    override fun setUpBase() {
        super.setUpBase()
        UserRepository.insert(User(name = "Teste", email = "t@t.com", password = "X"))
        SubjectRepository.insert(
            userId,
            Subject(
                name = "Cálculo", teacher = "Prof. A",
                minFrequency = 75, startDate = "01/03/2025", endDate = "30/06/2025",
                classHours = 4, semester = "2025.1",
            )
        )
    }

    private fun sampleFalta(date: String = "2025-06-10") = Falta(
        subjectId = subjectId,
        date = date,
        status = "Nao Justificada",
    )

    @Test fun `insert retorna true`() {
        assertTrue(FaltaRepository.insert(userId, sampleFalta()))
    }

    @Test fun `listByUser retorna faltas inseridas`() {
        FaltaRepository.insert(userId, sampleFalta("2025-06-10"))
        FaltaRepository.insert(userId, sampleFalta("2025-06-11"))
        assertEquals(2, FaltaRepository.listByUser(userId).size)
    }

    @Test fun `listByUser retorna vazio para usuario sem faltas`() {
        assertEquals(0, FaltaRepository.listByUser(userId).size)
    }

    @Test fun `delete remove falta`() {
        FaltaRepository.insert(userId, sampleFalta())
        val falta = FaltaRepository.listByUser(userId).first()
        assertTrue(FaltaRepository.delete(userId, falta.id))
        assertNull(FaltaRepository.findById(falta.id))
    }

    @Test fun `delete retorna false para id inexistente`() {
        assertFalse(FaltaRepository.delete(userId, 99999))
    }

    @Test fun `countByUser retorna contagem por disciplina`() {
        FaltaRepository.insert(userId, sampleFalta("2025-06-10"))
        FaltaRepository.insert(userId, sampleFalta("2025-06-11"))
        val counts = FaltaRepository.countByUser(userId)
        assertEquals(2, counts[subjectId])
    }

    @Test fun `countBySubject retorna contagem correta`() {
        FaltaRepository.insert(userId, sampleFalta())
        assertEquals(1, FaltaRepository.countBySubject(userId, subjectId))
    }

    @Test fun `formatDateForDisplay converte YYYY-MM-DD para DD-MM-YYYY`() {
        assertEquals("10/06/2025", FaltaRepository.formatDateForDisplay("2025-06-10"))
    }

    @Test fun `formatDateForDb converte DD-MM-YYYY para YYYY-MM-DD`() {
        assertEquals("2025-06-10", FaltaRepository.formatDateForDb("10/06/2025"))
    }
}
