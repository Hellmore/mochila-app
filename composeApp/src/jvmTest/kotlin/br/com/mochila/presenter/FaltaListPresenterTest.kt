package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.model.Falta
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FaltaListPresenterTest {

    private val view = mockk<FaltaListView>(relaxed = true)
    private val presenter = FaltaListPresenter(view)

    @Before fun setUp()    { mockkObject(FaltaRepository) }
    @After  fun tearDown() { unmockkAll() }

    @Test fun `lista vazia exibe estado vazio`() {
        every { FaltaRepository.listByUser(any()) } returns emptyList()
        presenter.loadFaltas(1)
        verify { view.showEmptyState() }
    }

    @Test fun `lista com faltas chama showFaltas`() {
        val faltas = listOf(Falta(id = 1, subjectName = "Cálculo"))
        every { FaltaRepository.listByUser(any()) } returns faltas
        presenter.loadFaltas(1)
        verify { view.showFaltas(faltas) }
    }

    @Test fun `onFaltaClicked navega para edicao`() {
        presenter.onFaltaClicked(4)
        verify { view.navigateToFaltaEdit(4) }
    }

    @Test fun `filterFaltas sem filtro retorna todas`() {
        val faltas = listOf(
            Falta(subjectName = "Cálculo", status = "Justificada"),
            Falta(subjectName = "Física", status = "Nao Justificada"),
        )
        val result = presenter.filterFaltas(faltas, "Todos", "Todos")
        assertEquals(2, result.size)
    }

    @Test fun `filterFaltas por disciplina filtra corretamente`() {
        val faltas = listOf(
            Falta(subjectName = "Cálculo", status = "Justificada"),
            Falta(subjectName = "Física", status = "Nao Justificada"),
        )
        val result = presenter.filterFaltas(faltas, "Cálculo", "Todos")
        assertEquals(1, result.size)
    }

    @Test fun `filterFaltas por status 'Não Justificada' mapeia para 'Nao Justificada'`() {
        val faltas = listOf(
            Falta(subjectName = "Cálculo", status = "Nao Justificada"),
            Falta(subjectName = "Física", status = "Justificada"),
        )
        val result = presenter.filterFaltas(faltas, "Todos", "Não Justificada")
        assertEquals(1, result.size)
        assertEquals("Nao Justificada", result.first().status)
    }

    @Test fun `filterFaltas por status Justificada filtra corretamente`() {
        val faltas = listOf(
            Falta(status = "Justificada"),
            Falta(status = "Nao Justificada"),
        )
        val result = presenter.filterFaltas(faltas, "Todos", "Justificada")
        assertEquals(1, result.size)
    }
}
