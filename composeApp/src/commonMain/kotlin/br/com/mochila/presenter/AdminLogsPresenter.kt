package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.model.LogAcao
import br.com.mochila.model.LogErro

// Contrato da tela de logs do painel admin
interface AdminLogsView {
    fun showLogsAcao(logs: List<LogAcao>)
    fun showLogsErro(logs: List<LogErro>)
}

// Carrega logs de acoes e erros do sistema
class AdminLogsPresenter(private val view: AdminLogsView) {

    fun loadLogsAcao() {
        // Busca historico de acoes dos usuarios
        view.showLogsAcao(LogRepository.listAcoes())
    }

    fun loadLogsErro() {
        // Busca historico de erros registrados
        view.showLogsErro(LogRepository.listErros())
    }
}
