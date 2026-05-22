package br.com.mochila.data

import br.com.mochila.model.Event
import br.com.mochila.model.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EventRepositoryTest : RepositoryTestBase() {

    private val userId = 1

    @org.junit.Before
    override fun setUpBase() {
        super.setUpBase()
        UserRepository.insert(User(name = "Teste", email = "t@t.com", password = "X"))
    }

    private fun sampleEvent(title: String = "Prova") = Event(
        title = title,
        eventDate = "2025-06-15 10:00",
        status = "Agendado",
    )

    @Test fun `insert retorna id gerado`() {
        assertNotNull(EventRepository.insert(userId, sampleEvent()))
    }

    @Test fun `listByUser retorna eventos inseridos`() {
        EventRepository.insert(userId, sampleEvent("A"))
        EventRepository.insert(userId, sampleEvent("B"))
        assertEquals(2, EventRepository.listByUser(userId).size)
    }

    @Test fun `listByUser retorna vazio para usuario sem eventos`() {
        assertEquals(0, EventRepository.listByUser(userId).size)
    }

    @Test fun `findById retorna evento correto`() {
        EventRepository.insert(userId, sampleEvent("Seminário"))
        val events = EventRepository.listByUser(userId)
        val found = EventRepository.findById(events.first().id)
        assertNotNull(found)
        assertEquals("Seminário", found.title)
    }

    @Test fun `findById retorna null para id inexistente`() {
        assertNull(EventRepository.findById(99999))
    }

    @Test fun `delete remove evento`() {
        EventRepository.insert(userId, sampleEvent())
        val event = EventRepository.listByUser(userId).first()
        assertTrue(EventRepository.delete(userId, event.id))
        assertNull(EventRepository.findById(event.id))
    }

    @Test fun `delete retorna false para id inexistente`() {
        assertFalse(EventRepository.delete(userId, 99999))
    }

    @Test fun `formatEventDateForDb converte DD-MM-YYYY para YYYY-MM-DD com horario`() {
        assertEquals("2025-06-15 00:00:00", EventRepository.formatEventDateForDb("15/06/2025"))
    }

    @Test fun `formatEventDateForDisplay converte YYYY-MM-DD para DD-MM-YYYY`() {
        assertEquals("15/06/2025", EventRepository.formatEventDateForDisplay("2025-06-15"))
    }

    @Test fun `eventMonthNumber extrai mes corretamente`() {
        assertEquals(6, EventRepository.eventMonthNumber("2025-06-15"))
        assertEquals(1, EventRepository.eventMonthNumber("2025-01-01"))
    }
}
