package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.model.Subject

// Contrato do SubjectRegister
interface SubjectRegisterView {
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun navigateToHome()
}

class SubjectRegisterPresenter(private val view: SubjectRegisterView) {

    fun saveSubject(
        userId: Int,
        subject: Subject,
        isEditing: Boolean
    ) {
        // Validar campos vazios
        if (
            subject.name.isBlank() ||
            subject.teacher.isBlank() ||
            subject.startDate.isBlank() ||
            subject.endDate.isBlank() ||
            subject.semester.isBlank()
        ) {
            view.showValidationError("Nenhum campo pode estar vazio.")
            return
        }

        if (subject.minFrequency <= 0) {
            view.showValidationError("Verifique o campo de frequência mínima.")
            return
        }

        if (subject.classHours <= 0) {
            view.showValidationError("Verifique o campo de horas por aula.")
            return
        }

        // Validar datas
        if (!isDateValid(subject.startDate)) {
            view.showValidationError("Data de início inválida.")
            return
        }

        if (!isDateValid(subject.endDate)) {
            view.showValidationError("Data de término inválida.")
            return
        }

        // Salvar
        val success = if (isEditing) {
            SubjectRepository.update(userId, subject)
        } else {
            SubjectRepository.insert(userId, subject)
        }

        if (success) {
            view.showSaveSuccess(isEditing)
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

    private fun isDateValid(date: String): Boolean {
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(date)) return false

        val parts = date.split("/")
        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false

        if (month !in 1..12) return false

        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
            else -> return false
        }

        return day in 1..daysInMonth
    }
}