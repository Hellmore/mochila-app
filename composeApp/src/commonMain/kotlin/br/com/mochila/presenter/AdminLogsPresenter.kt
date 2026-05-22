package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.model.LogAcao
import br.com.mochila.model.LogErro

interface AdminLogsView {
    fun showLogsAcao(logs: List<LogAcao>)
    fun showLogsErro(logs: List<LogErro>)
}

class AdminLogsPresenter(private val view: AdminLogsView) {

    fun loadLogsAcao() {
        view.showLogsAcao(LogRepository.listAcoes())
    }

    fun loadLogsErro() {
        view.showLogsErro(LogRepository.listErros())
    }
}
