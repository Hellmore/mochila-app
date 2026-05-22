package br.com.mochila.data

import br.com.mochila.model.Subject
import br.com.mochila.model.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SubjectRepositoryTest : RepositoryTestBase() {

    private val userId = 1

    @org.junit.Before
    override fun setUpBase() {
        super.setUpBase()
        UserRepository.insert(User(name = "Teste", email = "t@t.com", password = "X"))
    }

    private fun sampleSubject(name: String = "Cálculo", semester: String = "2025.1") = Subject(
        name = name,
        teacher = "Prof. Silva",
        minFrequency = 75,
        startDate = "01/03/2025",
        endDate = "30/06/2025",
        classHours = 4,
        semester = semester,
    )

    @Test fun `insert retorna id gerado`() {
        val id = SubjectRepository.insert(userId, sampleSubject())
        assertNotNull(id)
        assertTrue(id > 0)
    }

    @Test fun `listByUser retorna disciplinas inseridas`() {
        SubjectRepository.insert(userId, sampleSubject("A"))
        SubjectRepository.insert(userId, sampleSubject("B"))
        assertEquals(2, SubjectRepository.listByUser(userId).size)
    }

    @Test fun `listByUser retorna vazio para usuario sem disciplinas`() {
        assertEquals(0, SubjectRepository.listByUser(userId).size)
    }

    @Test fun `findById retorna disciplina correta`() {
        SubjectRepository.insert(userId, sampleSubject("Física"))
        val all = SubjectRepository.listByUser(userId)
        val found = SubjectRepository.findById(all.first().id)
        assertNotNull(found)
        assertEquals("Física", found.name)
    }

    @Test fun `findById retorna null para id inexistente`() {
        assertNull(SubjectRepository.findById(99999))
    }

    @Test fun `update altera nome da disciplina`() {
        SubjectRepository.insert(userId, sampleSubject("Antes"))
        val subj = SubjectRepository.listByUser(userId).first()
        assertTrue(SubjectRepository.update(userId, subj.copy(name = "Depois")))
        assertEquals("Depois", SubjectRepository.findById(subj.id)?.name)
    }

    @Test fun `delete remove disciplina`() {
        SubjectRepository.insert(userId, sampleSubject())
        val subj = SubjectRepository.listByUser(userId).first()
        assertTrue(SubjectRepository.delete(userId, subj.id))
        assertNull(SubjectRepository.findById(subj.id))
    }

    @Test fun `delete retorna false para id inexistente`() {
        assertFalse(SubjectRepository.delete(userId, 99999))
    }

    @Test fun `listDistinctSemesters retorna semestres unicos ordenados`() {
        SubjectRepository.insert(userId, sampleSubject(semester = "2025.2"))
        SubjectRepository.insert(userId, sampleSubject(semester = "2025.1"))
        SubjectRepository.insert(userId, sampleSubject(semester = "2025.1"))
        val semesters = SubjectRepository.listDistinctSemesters(userId)
        assertEquals(listOf("2025.1", "2025.2"), semesters)
    }
}
