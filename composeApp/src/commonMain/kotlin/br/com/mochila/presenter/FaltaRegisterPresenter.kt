package br.com.mochila.presenter

import br.com.mochila.data.FaltaRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.model.Falta
import br.com.mochila.util.AbsenceLimit
import br.com.mochila.util.DateValidator

// Contrato da tela de cadastro e edicao de falta
interface FaltaRegisterView {
    fun showFalta(falta: Falta)
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun showAbsenceLimitWarning(message: String)
    fun navigateToFaltasList()
}

// Cria, edita e exclui faltas com aviso de limite
class FaltaRegisterPresenter(private val view: FaltaRegisterView) {

    fun loadFalta(faltaId: Int) {
        // Busca falta existente para edicao
        val falta = FaltaRepository.findById(faltaId)
        if (falta != null) view.showFalta(falta)
    }

    fun saveFalta(userId: Int, falta: Falta, displayDate: String, displayStatus: String, isEditing: Boolean) {
        // Valida materia selecionada
        if (falta.subjectId == 0) {
            view.showValidationError("Selecione uma matéria.")
            return
        }
        // Valida formato da data
        if (displayDate.isBlank() || !DateValidator.isValid(displayDate)) {
            view.showValidationError("Data inválida. Use DD/MM/AAAA.")
            return
        }
        val dbDate = FaltaRepository.formatDateForDb(displayDate)
        val dbStatus = if (displayStatus == "Não Justificada") "Nao Justificada" else displayStatus
        val toSave = falta.copy(date = dbDate, status = dbStatus)
        // Insere ou atualiza no repositorio
        val success = if (isEditing) FaltaRepository.update(userId, toSave)
        else FaltaRepository.insert(userId, toSave)
        if (success) {
            LogRepository.insertAcao(userId, if (isEditing) "EDITAR_FALTA" else "CRIAR_FALTA", "falta", if (isEditing) falta.id else null)
            view.showSaveSuccess(isEditing)
            maybeWarnAbsenceLimit(userId, toSave.subjectId)
        } else {
            LogRepository.insertErro("FaltaRegisterPresenter", "Erro ao ${if (isEditing) "editar" else "criar"} falta", userId)
            view.showSaveError()
        }
    }

    // Verifica se o limite de faltas da materia foi atingido
    private fun maybeWarnAbsenceLimit(userId: Int, subjectId: Int) {
        val subject = SubjectRepository.findById(subjectId) ?: run {
            view.navigateToFaltasList()
            return
        }
        // Conta faltas da materia e compara com o limite
        val count = FaltaRepository.countBySubject(userId, subjectId)
        if (AbsenceLimit.isAtOrOverLimit(count, subject)) {
            val max = AbsenceLimit.maxAllowedAbsences(subject)
            view.showAbsenceLimitWarning(
                AbsenceLimit.warningMessage(subject.name, count, max),
            )
        } else {
            view.navigateToFaltasList()
        }
    }

    fun deleteFalta(userId: Int, faltaId: Int) {
        // Remove a falta do banco
        val success = FaltaRepository.delete(userId, faltaId)
        if (success) {
            LogRepository.insertAcao(userId, "EXCLUIR_FALTA", "falta", faltaId)
            view.showDeleteSuccess()
            view.navigateToFaltasList()
        } else {
            LogRepository.insertErro("FaltaRegisterPresenter", "Erro ao excluir falta id=$faltaId", userId)
            view.showDeleteError()
        }
    }
}
