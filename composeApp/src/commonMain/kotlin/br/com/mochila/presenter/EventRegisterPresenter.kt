package br.com.mochila.presenter

import br.com.mochila.data.EventCategoryCache
import br.com.mochila.data.EventRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.model.Event
import br.com.mochila.model.EventCategory
import br.com.mochila.util.DateValidator

interface EventRegisterView {
    fun showEvent(event: Event)
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToEventsList()
}

class EventRegisterPresenter(private val view: EventRegisterView) {

    fun loadEvent(eventId: Int) {
        val event = EventRepository.findById(eventId)
        if (event != null) view.showEvent(event)
    }

    fun saveEvent(userId: Int, event: Event, isEditing: Boolean, category: EventCategory) {
        if (event.title.isBlank()) {
            view.showValidationError("Nome do evento é obrigatório.")
            return
        }
        val displayDate = if (Regex("""\d{2}/\d{2}/\d{4}""").matches(event.eventDate.trim())) {
            event.eventDate.trim()
        } else {
            EventRepository.formatEventDateForDisplay(event.eventDate)
        }
        if (displayDate.isBlank() || !DateValidator.isValid(displayDate)) {
            view.showValidationError("Data inválida. Use DD/MM/AAAA.")
            return
        }
        val toSave = event.copy(
            eventDate = EventRepository.formatEventDateForDb(displayDate),
            reminderShown = if (isEditing) event.reminderShown else false,
        )
        val savedEventId = if (isEditing) {
            if (EventRepository.update(userId, toSave)) toSave.id else null
        } else {
            EventRepository.insert(userId, toSave)
        }
        if (savedEventId != null) {
            EventCategoryCache.set(savedEventId, category)
            LogRepository.insertAcao(userId, if (isEditing) "EDITAR_EVENTO" else "CRIAR_EVENTO", "evento", savedEventId)
            view.showSaveSuccess(isEditing)
            view.navigateToEventsList()
        } else {
            LogRepository.insertErro("EventRegisterPresenter", "Erro ao ${if (isEditing) "editar" else "criar"} evento", userId)
            view.showSaveError()
        }
    }

    fun deleteEvent(userId: Int, eventId: Int) {
        val success = EventRepository.delete(userId, eventId)
        if (success) {
            EventCategoryCache.remove(eventId)
            LogRepository.insertAcao(userId, "EXCLUIR_EVENTO", "evento", eventId)
            view.showDeleteSuccess()
            view.navigateToEventsList()
        } else {
            LogRepository.insertErro("EventRegisterPresenter", "Erro ao excluir evento id=$eventId", userId)
            view.showDeleteError()
        }
    }
}
