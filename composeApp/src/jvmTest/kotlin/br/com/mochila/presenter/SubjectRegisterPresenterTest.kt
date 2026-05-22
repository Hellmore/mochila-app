package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.SubjectWeeklyFrequencyCache
import br.com.mochila.model.Subject
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class SubjectRegisterPresenterTest {

    private val view = mockk<SubjectRegisterView>(relaxed = true)
    private val presenter = SubjectRegisterPresenter(view)

    @Before fun setUp() {
        mockkObject(SubjectRepository)
        mockkObject(SubjectWeeklyFrequencyCache)
        every { SubjectWeeklyFrequencyCache.set(any(), any()) } just Runs
        every { SubjectWeeklyFrequencyCache.remove(any()) } just Runs
    }

    @After fun tearDown() { unmockkAll() }

    private val validSubject = Subject(
        id = 0,
        name = "Cálculo",
        teacher = "Prof. Silva",
        minFrequency = 75,
        startDate = "01/03/2025",
        endDate = "30/06/2025",
        classHours = 4,
        semester = "2025.1",
    )

    @Test fun `campos em branco exibem erro de validacao`() {
        presenter.saveSubject(1, validSubject.copy(name = ""), 2, false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `frequencia minima zero exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(minFrequency = 0), 2, false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `frequencia semanal zero exibe erro`() {
        presenter.saveSubject(1, validSubject, 0, false)
        verify { view.showValidationError(match { it.contains("frequência") }) }
    }

    @Test fun `horas de aula zero exibem erro`() {
        presenter.saveSubject(1, validSubject.copy(classHours = 0), 2, false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data de inicio invalida exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(startDate = "99/99/9999"), 2, false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data de fim invalida exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(endDate = "99/99/9999"), 2, false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `salvar novo com sucesso persiste frequencia semanal e navega`() {
        every { SubjectRepository.insert(any(), any()) } returns 12
        presenter.saveSubject(1, validSubject, 2, false)
        verify { SubjectWeeklyFrequencyCache.set(12, 2) }
        verify { view.showSaveSuccess(false) }
        verify { view.navigateToHome() }
    }

    @Test fun `editar com sucesso persiste frequencia semanal`() {
        every { SubjectRepository.update(any(), any()) } returns true
        presenter.saveSubject(1, validSubject.copy(id = 5), 2, true)
        verify { SubjectWeeklyFrequencyCache.set(5, 2) }
        verify { view.showSaveSuccess(true) }
    }

    @Test fun `deleteSubject com sucesso remove cache e navega`() {
        every { SubjectRepository.delete(any(), any()) } returns true
        presenter.deleteSubject(1, 5)
        verify { SubjectWeeklyFrequencyCache.remove(5) }
        verify { view.navigateToHome() }
    }

    @Test fun `deleteSubject com falha exibe erro`() {
        every { SubjectRepository.delete(any(), any()) } returns false
        presenter.deleteSubject(1, 5)
        verify { view.showSaveError() }
        verify(exactly = 0) { SubjectWeeklyFrequencyCache.remove(any()) }
    }

    @Test fun `loadSubjectForEdit retorna disciplina do repositorio`() {
        every { SubjectRepository.findById(5) } returns validSubject.copy(id = 5)
        val result = presenter.loadSubjectForEdit(5)
        kotlin.test.assertEquals(5, result?.id)
    }
}
