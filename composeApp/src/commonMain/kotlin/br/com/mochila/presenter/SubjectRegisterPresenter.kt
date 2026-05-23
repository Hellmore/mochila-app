package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
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

    fun saveSubject(userId: Int, subject: Subject, isEditing: Boolean) {
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

        if (subject.weeklyClasses <= 0) {
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

        val toSave = subject.copy(weeklyClasses = subject.weeklyClasses.coerceAtLeast(1))

        if (isEditing) {
            val success = SubjectRepository.update(userId, toSave)
            if (success) {
                view.showSaveSuccess(true)
                view.navigateToHome()
            } else {
                view.showSaveError()
            }
            return
        }

        val newId = SubjectRepository.insert(userId, toSave)
        if (newId != null) {
            view.showSaveSuccess(false)
            view.navigateToHome()
        } else {
            view.showSaveError()
        }
    }

    fun deleteSubject(userId: Int, subjectId: Int) {
        val success = SubjectRepository.delete(userId, subjectId)
        if (success) {
            view.showSaveSuccess(isEditing = true)
            view.navigateToHome()
        } else {
            view.showSaveError()
        }
    }
}
