package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.model.Subject

// Contrato do SubjectDetail
interface SubjectDetailView {
    fun showSubject(subject: Subject)
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToHome()
    fun navigateToEdit(subject: Subject)
    fun navigateBack()
}

class SubjectDetailPresenter(private val view: SubjectDetailView) {

    fun loadSubject(subjectId: Int) {
        val subject = SubjectRepository.findById(subjectId)
        if (subject != null) {
            view.showSubject(subject)
        } else {
            view.navigateBack()
        }
    }

    fun onEditClicked(subject: Subject) {
        view.navigateToEdit(subject)
    }

    fun onDeleteConfirmed(userId: Int, subject: Subject) {
        val success = SubjectRepository.delete(
            userId = userId,
            subjectId = subject.id
        )
        if (success) {
            println("✅ Disciplina deletada com sucesso!")
            view.showDeleteSuccess()
            view.navigateToHome()
        } else {
            println("⚠️ Erro ao deletar disciplina.")
            view.showDeleteError()
        }
    }
}