package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.TaskCategoryCache
import br.com.mochila.data.TaskPriorityCache
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
            TaskPriorityCache.remove(task.id)
            TaskCategoryCache.remove(task.id)
            LogRepository.insertAcao(userId, "EXCLUIR_TAREFA", "tarefa", task.id)
            view.showDeleteSuccess()
            view.navigateToTasksList()
        } else {
            LogRepository.insertErro("TaskDetailPresenter", "Erro ao excluir tarefa id=${task.id}", userId)
            view.showDeleteError()
        }
    }
}
