package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.model.Falta

// Contrato da tela de listagem de faltas
interface FaltaListView {
    fun showFaltas(faltas: List<Falta>)
    fun showEmptyState()
    fun navigateToFaltaDetail(faltaId: Int)
}

// Carrega faltas do usuario e aplica filtros
class FaltaListPresenter(private val view: FaltaListView) {

    fun loadFaltas(userId: Int) {
        // Busca faltas registradas pelo usuario
        val faltas = FaltaRepository.listByUser(userId)
        if (faltas.isEmpty()) view.showEmptyState() else view.showFaltas(faltas)
    }

    fun onFaltaClicked(faltaId: Int) {
        view.navigateToFaltaDetail(faltaId)
    }

    // Filtra faltas por materia e status
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
