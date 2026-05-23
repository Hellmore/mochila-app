package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task

interface TaskDetailView {
    fun showTask(task: Task)
    fun showTaskNotFound()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToTasksList()
    fun navigateToEdit(task: Task)
    fun navigateBack()
}

class TaskDetailPresenter(private val view: TaskDetailView) {

    fun loadTask(taskId: Int) {
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
