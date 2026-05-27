package br.com.mochila.presenter

import br.com.mochila.data.EventRepository
import br.com.mochila.model.Event
import br.com.mochila.model.EventCategory
import br.com.mochila.util.DateValidator

// Contrato da tela de cadastro e edicao de evento
interface EventRegisterView {
    fun showEvent(event: Event)
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToEventsList()
}

// Cria, edita e exclui eventos
class EventRegisterPresenter(private val view: EventRegisterView) {

    fun loadEvent(eventId: Int) {
        // Busca evento existente para edicao
        val event = EventRepository.findById(eventId)
        if (event != null) view.showEvent(event)
    }

    fun saveEvent(userId: Int, event: Event, isEditing: Boolean, category: EventCategory) {
        // Valida titulo obrigatorio
        if (event.title.isBlank()) {
            view.showValidationError("Nome do evento é obrigatório.")
            return
        }
        val displayDate = if (Regex("""\d{2}/\d{2}/\d{4}""").matches(event.eventDate.trim())) {
            event.eventDate.trim()
        } else {
            EventRepository.formatEventDateForDisplay(event.eventDate)
        }
        // Valida formato da data do evento
        if (displayDate.isBlank() || !DateValidator.isValid(displayDate)) {
            view.showValidationError("Data inválida. Use DD/MM/AAAA.")
            return
        }
        val toSave = event.copy(
            eventDate = EventRepository.formatEventDateForDb(displayDate),
            category = category,
            reminderShown = if (isEditing) event.reminderShown else false,
        )
        // Atualiza ou insere no repositorio
        val saved = if (isEditing) {
            EventRepository.update(userId, toSave)
        } else {
            EventRepository.insert(userId, toSave) != null
        }
        if (saved) {
            view.showSaveSuccess(isEditing)
            view.navigateToEventsList()
        } else {
            view.showSaveError()
        }
    }

    fun deleteEvent(userId: Int, eventId: Int) {
        // Remove o evento do banco
        val success = EventRepository.delete(userId, eventId)
        if (success) {
            view.showDeleteSuccess()
            view.navigateToEventsList()
        } else {
            view.showDeleteError()
        }
    }
}
