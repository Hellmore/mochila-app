package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task

// Contrato da tela de detalhes da tarefa
interface TaskDetailView {
    fun showTask(task: Task)
    fun showTaskNotFound()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToTasksList()
    fun navigateToEdit(task: Task)
    fun navigateBack()
}

// Carrega, edita e exclui uma tarefa
class TaskDetailPresenter(private val view: TaskDetailView) {

    fun loadTask(taskId: Int) {
        // Busca a tarefa no repositorio
        val task = TaskRepository.findById(taskId)
        if (task != null) {
            view.showTask(task)
        } else {
            view.showTaskNotFound()
        }
    }

    fun onEditClicked(task: Task) {
        view.navigateToEdit(task)
    }

    fun onDeleteConfirmed(userId: Int, task: Task) {
        // Remove a tarefa do banco
        val success = TaskRepository.delete(userId = userId, taskId = task.id)
        if (success) {
            println("✅ Tarefa deletada com sucesso!")
            view.showDeleteSuccess()
            view.navigateToTasksList()
        } else {
            println("⚠️ Erro ao deletar tarefa.")
            view.showDeleteError()
        }
    }
}
