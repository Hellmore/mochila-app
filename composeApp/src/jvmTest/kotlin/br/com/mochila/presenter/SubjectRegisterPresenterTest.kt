package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
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
        weeklyClasses = 2,
        semester = "2025.1",
    )

    @Test fun `campos em branco exibem erro de validacao`() {
        presenter.saveSubject(1, validSubject.copy(name = ""), false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `frequencia minima zero exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(minFrequency = 0), false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `frequencia semanal zero exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(weeklyClasses = 0), false)
        verify { view.showValidationError(match { it.contains("frequência") }) }
    }

    @Test fun `horas de aula zero exibem erro`() {
        presenter.saveSubject(1, validSubject.copy(classHours = 0), false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data de inicio invalida exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(startDate = "99/99/9999"), false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data de fim invalida exibe erro`() {
        presenter.saveSubject(1, validSubject.copy(endDate = "99/99/9999"), false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `salvar novo com sucesso persiste aula_semana e navega`() {
        every { SubjectRepository.insert(any(), any()) } returns 12
        presenter.saveSubject(1, validSubject, false)
        verify {
            SubjectRepository.insert(1, match { it.weeklyClasses == 2 })
        }
        verify { view.showSaveSuccess(false) }
        verify { view.navigateToHome() }
    }

    @Test fun `editar com sucesso persiste aula_semana`() {
        every { SubjectRepository.update(any(), any()) } returns true
        presenter.saveSubject(1, validSubject.copy(id = 5), true)
        verify {
            SubjectRepository.update(1, match { it.id == 5 && it.weeklyClasses == 2 })
        }
        verify { view.showSaveSuccess(true) }
    }

    @Test fun `deleteSubject com sucesso navega`() {
        every { SubjectRepository.delete(any(), any()) } returns true
        presenter.deleteSubject(1, 5)
        verify { view.navigateToHome() }
    }

    @Test fun `deleteSubject com falha exibe erro`() {
        every { SubjectRepository.delete(any(), any()) } returns false
        presenter.deleteSubject(1, 5)
        verify { view.showSaveError() }
    }

    @Test fun `loadSubjectForEdit retorna disciplina do repositorio`() {
        every { SubjectRepository.findById(5) } returns validSubject.copy(id = 5)
        val result = presenter.loadSubjectForEdit(5)
        kotlin.test.assertEquals(5, result?.id)
    }
}
