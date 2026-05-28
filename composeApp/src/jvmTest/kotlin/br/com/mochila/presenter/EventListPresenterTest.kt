package br.com.mochila.presenter

import br.com.mochila.data.EventRepository
import br.com.mochila.model.Event
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

// Testa listagem, filtros e navegacao de eventos
class EventListPresenterTest {

    private val view = mockk<EventListView>(relaxed = true)
    private val presenter = EventListPresenter(view)

    @Before fun setUp()    { mockkObject(EventRepository) }
    @After  fun tearDown() { unmockkAll() }

    private val monthNames = listOf(
        "Janeiro","Fevereiro","Março","Abril","Maio","Junho",
        "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"
    )

    // Carregamento da lista
    @Test fun `lista vazia exibe estado vazio`() {
        every { EventRepository.listByUser(any()) } returns emptyList()
        presenter.loadEvents(1)
        verify { view.showEmptyState() }
    }

    @Test fun `lista com eventos chama showEvents`() {
        val events = listOf(Event(title = "Prova"))
        every { EventRepository.listByUser(any()) } returns events
        presenter.loadEvents(1)
        verify { view.showEvents(events) }
    }

    // Navegacao
    @Test fun `onEventClicked navega para detalhes`() {
        presenter.onEventClicked(3)
        verify { view.navigateToEventDetail(3) }
    }

    // Filtros
    @Test fun `filterEvents sem filtro retorna todos`() {
        val events = listOf(Event(title = "A"), Event(title = "B"))
        val result = presenter.filterEvents(events, "Todos", "Todos", monthNames)
        assertEquals(2, result.size)
    }

    @Test fun `filterEvents por disciplina filtra corretamente`() {
        val events = listOf(
            Event(title = "A", subjectName = "Cálculo"),
            Event(title = "B", subjectName = "Física"),
        )
        val result = presenter.filterEvents(events, "Cálculo", "Todos", monthNames)
        assertEquals(1, result.size)
        assertEquals("A", result.first().title)
    }

    @Test fun `filterEvents por mes filtra usando eventMonthNumber`() {
        val eJan = Event(title = "Evento Jan", eventDate = "2025-01-15")
        val eFev = Event(title = "Evento Fev", eventDate = "2025-02-10")
        every { EventRepository.eventMonthNumber("2025-01-15") } returns 1
        every { EventRepository.eventMonthNumber("2025-02-10") } returns 2

        val result = presenter.filterEvents(listOf(eJan, eFev), "Todos", "Janeiro", monthNames)
        assertEquals(1, result.size)
        assertEquals("Evento Jan", result.first().title)
    }
}
