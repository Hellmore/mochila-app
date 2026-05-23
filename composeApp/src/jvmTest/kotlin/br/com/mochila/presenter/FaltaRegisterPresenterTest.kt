package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.model.Falta
import br.com.mochila.model.Subject
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class FaltaRegisterPresenterTest {

    private val view = mockk<FaltaRegisterView>(relaxed = true)
    private val presenter = FaltaRegisterPresenter(view)

    @Before fun setUp() {
        mockkObject(FaltaRepository)
        mockkObject(SubjectRepository)
        every { SubjectRepository.findById(1) } returns subjectCalculo
        every { FaltaRepository.countBySubject(any(), 1) } returns 0
    }

    @After fun tearDown() { unmockkAll() }

    private val faltaComDisciplina = Falta(subjectId = 1)

    /** 01/03–30/06/2025, 2 aulas/sem → 36 aulas; 75% freq. → limite de 9 faltas. */
    private val subjectCalculo = Subject(
        id = 1,
        name = "Cálculo",
        startDate = "01/03/2025",
        endDate = "30/06/2025",
        classHours = 4,
        weeklyClasses = 2,
        minFrequency = 75,
    )

    @Test fun `disciplina nao selecionada exibe erro`() {
        presenter.saveFalta(1, faltaComDisciplina.copy(subjectId = 0), "10/06/2025", "Justificada", false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data invalida exibe erro`() {
        presenter.saveFalta(1, faltaComDisciplina, "99/99/9999", "Justificada", false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data em branco exibe erro`() {
        presenter.saveFalta(1, faltaComDisciplina, "", "Justificada", false)
        verify { view.showValidationError(any()) }
    }

    @Test fun `salvar novo com sucesso navega para lista quando abaixo do limite`() {
        every { FaltaRepository.formatDateForDb("10/06/2025") } returns "2025-06-10"
        every { FaltaRepository.insert(any(), any()) } returns true
        every { SubjectRepository.findById(1) } returns subjectCalculo
        every { FaltaRepository.countBySubject(1, 1) } returns 8
        presenter.saveFalta(1, faltaComDisciplina, "10/06/2025", "Justificada", false)
        verify { view.showSaveSuccess(false) }
        verify { view.navigateToFaltasList() }
        verify(exactly = 0) { view.showAbsenceLimitWarning(any()) }
    }

    @Test fun `salvar novo no limite exibe aviso em vez de navegar`() {
        every { FaltaRepository.formatDateForDb("10/06/2025") } returns "2025-06-10"
        every { FaltaRepository.insert(any(), any()) } returns true
        every { SubjectRepository.findById(1) } returns subjectCalculo
        every { FaltaRepository.countBySubject(1, 1) } returns 9
        presenter.saveFalta(1, faltaComDisciplina, "10/06/2025", "Justificada", false)
        verify { view.showSaveSuccess(false) }
        verify { view.showAbsenceLimitWarning(match { it.contains("Cálculo") }) }
        verify(exactly = 0) { view.navigateToFaltasList() }
    }

    @Test fun `status 'Não Justificada' e mapeado para 'Nao Justificada' no db`() {
        every { FaltaRepository.formatDateForDb(any()) } returns "2025-06-10"
        val capturado = slot<Falta>()
        every { FaltaRepository.insert(any(), capture(capturado)) } returns true
        presenter.saveFalta(1, faltaComDisciplina, "10/06/2025", "Não Justificada", false)
        kotlin.test.assertEquals("Nao Justificada", capturado.captured.status)
    }

    @Test fun `editar com sucesso chama showSaveSuccess com true`() {
        every { FaltaRepository.formatDateForDb(any()) } returns "2025-06-10"
        every { FaltaRepository.update(any(), any()) } returns true
        presenter.saveFalta(1, faltaComDisciplina.copy(id = 5), "10/06/2025", "Justificada", true)
        verify { view.showSaveSuccess(true) }
    }

    @Test fun `deleteFalta com sucesso navega para lista`() {
        every { FaltaRepository.delete(any(), any()) } returns true
        presenter.deleteFalta(1, 4)
        verify { view.showDeleteSuccess() }
        verify { view.navigateToFaltasList() }
    }

    @Test fun `deleteFalta com falha exibe erro`() {
        every { FaltaRepository.delete(any(), any()) } returns false
        presenter.deleteFalta(1, 4)
        verify { view.showDeleteError() }
    }

    @Test fun `loadFalta encontrada exibe falta`() {
        val falta = Falta(id = 2, subjectId = 1)
        every { FaltaRepository.findById(2) } returns falta
        presenter.loadFalta(2)
        verify { view.showFalta(falta) }
    }
}
