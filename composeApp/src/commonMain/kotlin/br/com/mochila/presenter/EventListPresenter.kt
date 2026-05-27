package br.com.mochila.presenter

import br.com.mochila.data.EventRepository
import br.com.mochila.model.Event

// Contrato da tela de listagem de eventos
interface EventListView {
    fun showEvents(events: List<Event>)
    fun showEmptyState()
    fun navigateToEventEdit(eventId: Int)
}

// Carrega eventos do usuario e aplica filtros
class EventListPresenter(private val view: EventListView) {

    fun loadEvents(userId: Int) {
        // Busca eventos registrados pelo usuario
        val events = EventRepository.listByUser(userId)
        if (events.isEmpty()) view.showEmptyState() else view.showEvents(events)
    }

    fun onEventClicked(eventId: Int) {
        view.navigateToEventEdit(eventId)
    }

    // Filtra eventos por materia e mes
    fun filterEvents(
        events: List<Event>,
        subjectFilter: String,
        monthFilter: String,
        monthNames: List<String>,
    ): List<Event> {
        var result = events
        if (subjectFilter != "Todos") {
            result = result.filter { it.subjectName == subjectFilter }
        }
        if (monthFilter != "Todos") {
            val monthIndex = monthNames.indexOf(monthFilter)
            if (monthIndex >= 0) {
                val monthNum = monthIndex + 1
                result = result.filter { EventRepository.eventMonthNumber(it.eventDate) == monthNum }
            }
        }
        return result
    }
}
