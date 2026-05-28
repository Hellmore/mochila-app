package br.com.mochila.presenter

import br.com.mochila.data.EventRepository
import br.com.mochila.model.Event

// Contrato da tela de detalhes do evento
interface EventDetailView {
    fun showEvent(event: Event)
    fun showEventNotFound()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToEventsList()
    fun navigateToEdit(event: Event)
    fun navigateBack()
}

// Carrega e exclui um evento
class EventDetailPresenter(private val view: EventDetailView) {

    fun loadEvent(eventId: Int) {
        val event = EventRepository.findById(eventId)
        if (event != null) {
            view.showEvent(event)
        } else {
            view.showEventNotFound()
        }
    }

    fun onEditClicked(event: Event) {
        view.navigateToEdit(event)
    }

    fun onDeleteConfirmed(userId: Int, event: Event) {
        val success = EventRepository.delete(userId, event.id)
        if (success) {
            view.showDeleteSuccess()
            view.navigateToEventsList()
        } else {
            view.showDeleteError()
        }
    }
}
