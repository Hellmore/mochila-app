package br.com.mochila.presenter

import br.com.mochila.data.EventCategoryCache
import br.com.mochila.data.EventRepository
import br.com.mochila.model.Event
import br.com.mochila.model.EventCategory
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventRegisterPresenterTest {

    private val view = mockk<EventRegisterView>(relaxed = true)
    private val presenter = EventRegisterPresenter(view)

    @Before fun setUp() {
        mockkObject(EventRepository)
        mockkObject(EventCategoryCache)
        every { EventCategoryCache.set(any(), any()) } returns Unit
    }

    @After fun tearDown() { unmockkAll() }

    private val validEvent = Event(id = 0, title = "Prova", eventDate = "15/06/2025")

    @Test fun `titulo em branco exibe erro`() {
        presenter.saveEvent(1, validEvent.copy(title = ""), false, EventCategory.default)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data invalida exibe erro`() {
        every { EventRepository.formatEventDateForDisplay(any()) } returns "99/99/9999"
        val event = validEvent.copy(eventDate = "data_invalida")
        presenter.saveEvent(1, event, false, EventCategory.default)
        verify { view.showValidationError(any()) }
    }

    @Test fun `salvar novo com sucesso navega para lista`() {
        every { EventRepository.formatEventDateForDb("15/06/2025") } returns "2025-06-15 00:00:00"
        every { EventRepository.insert(any(), any()) } returns 7
        presenter.saveEvent(1, validEvent, false, EventCategory.SEMINARIO)
        verify { EventCategoryCache.set(7, EventCategory.SEMINARIO) }
        verify { view.showSaveSuccess(false) }
        verify { view.navigateToEventsList() }
    }

    @Test fun `editar com sucesso chama showSaveSuccess com true`() {
        every { EventRepository.formatEventDateForDb("15/06/2025") } returns "2025-06-15 00:00:00"
        every { EventRepository.update(any(), any()) } returns true
        presenter.saveEvent(1, validEvent.copy(id = 5), true, EventCategory.PROVA)
        verify { EventCategoryCache.set(5, EventCategory.PROVA) }
        verify { view.showSaveSuccess(true) }
    }

    @Test fun `falha ao salvar exibe erro`() {
        every { EventRepository.formatEventDateForDb("15/06/2025") } returns "2025-06-15 00:00:00"
        every { EventRepository.insert(any(), any()) } returns null
        presenter.saveEvent(1, validEvent, false, EventCategory.default)
        verify { view.showSaveError() }
    }

    @Test fun `deleteEvent com sucesso navega para lista`() {
        every { EventRepository.delete(any(), any()) } returns true
        presenter.deleteEvent(1, 3)
        verify { view.showDeleteSuccess() }
        verify { view.navigateToEventsList() }
    }

    @Test fun `deleteEvent com falha exibe erro`() {
        every { EventRepository.delete(any(), any()) } returns false
        presenter.deleteEvent(1, 3)
        verify { view.showDeleteError() }
    }

    @Test fun `loadEvent encontrado exibe evento`() {
        every { EventRepository.findById(3) } returns validEvent.copy(id = 3)
        presenter.loadEvent(3)
        verify { view.showEvent(any()) }
    }

    @Test fun `loadEvent nao encontrado nao exibe nada`() {
        every { EventRepository.findById(any()) } returns null
        presenter.loadEvent(99)
        verify(exactly = 0) { view.showEvent(any()) }
    }
}
