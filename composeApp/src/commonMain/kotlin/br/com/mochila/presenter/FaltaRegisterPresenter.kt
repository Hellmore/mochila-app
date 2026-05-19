package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.model.Falta
import br.com.mochila.util.DateValidator

interface FaltaRegisterView {
    fun showFalta(falta: Falta)
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToFaltasList()
}

class FaltaRegisterPresenter(private val view: FaltaRegisterView) {

    fun loadFalta(faltaId: Int) {
        val falta = FaltaRepository.findById(faltaId)
        if (falta != null) view.showFalta(falta)
    }

    fun saveFalta(userId: Int, falta: Falta, displayDate: String, displayStatus: String, isEditing: Boolean) {
        if (falta.subjectId == 0) {
            view.showValidationError("Selecione uma matéria.")
            return
        }
        if (displayDate.isBlank() || !DateValidator.isValid(displayDate)) {
            view.showValidationError("Data inválida. Use DD/MM/AAAA.")
            return
        }
        val dbDate = FaltaRepository.formatDateForDb(displayDate)
        val dbStatus = if (displayStatus == "Não Justificada") "Nao Justificada" else displayStatus
        val toSave = falta.copy(date = dbDate, status = dbStatus)
        val success = if (isEditing) FaltaRepository.update(userId, toSave)
        else FaltaRepository.insert(userId, toSave)
        if (success) {
            view.showSaveSuccess(isEditing)
            view.navigateToFaltasList()
        } else {
            view.showSaveError()
        }
    }

    fun deleteFalta(userId: Int, faltaId: Int) {
        val success = FaltaRepository.delete(userId, faltaId)
        if (success) {
            view.showDeleteSuccess()
            view.navigateToFaltasList()
        } else {
            view.showDeleteError()
        }
    }
}
