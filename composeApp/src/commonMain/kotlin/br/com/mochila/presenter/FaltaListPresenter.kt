package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.model.Falta

interface FaltaListView {
    fun showFaltas(faltas: List<Falta>)
    fun showEmptyState()
    fun navigateToFaltaEdit(faltaId: Int)
}

class FaltaListPresenter(private val view: FaltaListView) {

    fun loadFaltas(userId: Int) {
        val faltas = FaltaRepository.listByUser(userId)
        if (faltas.isEmpty()) view.showEmptyState() else view.showFaltas(faltas)
    }

    fun onFaltaClicked(faltaId: Int) {
        view.navigateToFaltaEdit(faltaId)
    }

    fun filterFaltas(
        faltas: List<Falta>,
        subjectFilter: String,
        statusFilter: String,
    ): List<Falta> {
        var result = faltas
        if (subjectFilter != "Todos") {
            result = result.filter { it.subjectName == subjectFilter }
        }
        if (statusFilter != "Todos") {
            val dbStatus = if (statusFilter == "Não Justificada") "Nao Justificada" else statusFilter
            result = result.filter { it.status == dbStatus }
        }
        return result
    }
}
