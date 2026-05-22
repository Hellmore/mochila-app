package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.SubjectWeeklyFrequencyCache
import br.com.mochila.model.Subject
import br.com.mochila.util.DateValidator

interface SubjectRegisterView {
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun navigateToHome()
}

class SubjectRegisterPresenter(private val view: SubjectRegisterView) {
    fun loadSubjectForEdit(subjectId: Int): Subject? {
        return SubjectRepository.findById(subjectId)
    }

    fun saveSubject(userId: Int, subject: Subject, weeklyClassCount: Int, isEditing: Boolean) {
        if (subject.name.isBlank() || subject.teacher.isBlank() ||
            subject.startDate.isBlank() || subject.endDate.isBlank() ||
            subject.semester.isBlank()
        ) {
            view.showValidationError("Nenhum campo pode estar vazio.")
            return
        }

        if (subject.minFrequency <= 0) {
            view.showValidationError("Verifique o campo de frequência mínima.")
            return
        }

        if (weeklyClassCount <= 0) {
            view.showValidationError("Verifique o campo de frequência (aulas por semana).")
            return
        }

        if (subject.classHours <= 0) {
            view.showValidationError("Verifique o campo de horas por aula.")
            return
        }

        if (!DateValidator.isValid(subject.startDate)) {
            view.showValidationError("Data de início inválida.")
            return
        }

        if (!DateValidator.isValid(subject.endDate)) {
            view.showValidationError("Data de término inválida.")
            return
        }

        if (isEditing) {
            val success = SubjectRepository.update(userId, subject)
            if (success) {
                SubjectWeeklyFrequencyCache.set(subject.id, weeklyClassCount)
                LogRepository.insertAcao(userId, "EDITAR_DISCIPLINA", "disciplina", subject.id)
                view.showSaveSuccess(true)
                view.navigateToHome()
            } else {
                LogRepository.insertErro("SubjectRegisterPresenter", "Erro ao editar disciplina", userId)
                view.showSaveError()
            }
            return
        }

        val newId = SubjectRepository.insert(userId, subject)
        if (newId != null) {
            SubjectWeeklyFrequencyCache.set(newId, weeklyClassCount)
            LogRepository.insertAcao(userId, "CRIAR_DISCIPLINA", "disciplina", newId)
            view.showSaveSuccess(false)
            view.navigateToHome()
        } else {
            LogRepository.insertErro("SubjectRegisterPresenter", "Erro ao criar disciplina", userId)
            view.showSaveError()
        }
    }

    fun deleteSubject(userId: Int, subjectId: Int) {
        val success = SubjectRepository.delete(userId, subjectId)
        if (success) {
            SubjectWeeklyFrequencyCache.remove(subjectId)
            LogRepository.insertAcao(userId, "EXCLUIR_DISCIPLINA", "disciplina", subjectId)
            view.showSaveSuccess(isEditing = true)
            view.navigateToHome()
        } else {
            LogRepository.insertErro("SubjectRegisterPresenter", "Erro ao excluir disciplina id=$subjectId", userId)
            view.showSaveError()
        }
    }
}
