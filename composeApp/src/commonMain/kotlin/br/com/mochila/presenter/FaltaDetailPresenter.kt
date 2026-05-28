package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.model.Falta

// Contrato da tela de detalhes da falta
interface FaltaDetailView {
    fun showFalta(falta: Falta)
    fun showFaltaNotFound()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToFaltasList()
    fun navigateToEdit(falta: Falta)
    fun navigateBack()
}

// Carrega e exclui uma falta
class FaltaDetailPresenter(private val view: FaltaDetailView) {

    fun loadFalta(faltaId: Int) {
        val falta = FaltaRepository.findById(faltaId)
        if (falta != null) {
            view.showFalta(falta)
        } else {
            view.showFaltaNotFound()
        }
    }

    fun onEditClicked(falta: Falta) {
        view.navigateToEdit(falta)
    }

    fun onDeleteConfirmed(userId: Int, falta: Falta) {
        val success = FaltaRepository.delete(userId, falta.id)
        if (success) {
            LogRepository.insertAcao(userId, "EXCLUIR_FALTA", "falta", falta.id)
            view.showDeleteSuccess()
            view.navigateToFaltasList()
        } else {
            LogRepository.insertErro("FaltaDetailPresenter", "Erro ao excluir falta id=${falta.id}", userId)
            view.showDeleteError()
        }
    }
}
