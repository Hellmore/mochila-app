package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.TaskCategoryCache
import br.com.mochila.data.TaskPriorityCache
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task
import br.com.mochila.model.TaskCategory
import br.com.mochila.model.TaskPriority
import br.com.mochila.util.DateValidator

interface TaskRegisterView {
    fun showTask(task: Task)
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToTasksList()
}

class TaskRegisterPresenter(private val view: TaskRegisterView) {

    fun loadTask(taskId: Int) {
        val task = TaskRepository.findById(taskId)
        if (task != null) {
            view.showTask(task)
        }
    }

    fun saveTask(
        userId: Int,
        task: Task,
        isEditing: Boolean,
        priority: TaskPriority,
        category: TaskCategory,
    ) {
        if (task.title.isBlank() || task.description.isBlank()) {
            view.showValidationError("Título e descrição são obrigatórios.")
            return
        }

        if (!task.dueDate.isNullOrBlank() && !DateValidator.isValid(task.dueDate)) {
            view.showValidationError("Data limite inválida.")
            return
        }

        val savedTaskId = if (isEditing) {
            if (TaskRepository.update(userId, task)) task.id else null
        } else {
            TaskRepository.insert(userId, task)
        }

        if (savedTaskId != null) {
            TaskPriorityCache.set(savedTaskId, priority)
            TaskCategoryCache.set(savedTaskId, category)
            LogRepository.insertAcao(userId, if (isEditing) "EDITAR_TAREFA" else "CRIAR_TAREFA", "tarefa", savedTaskId)
            view.showSaveSuccess(isEditing)
            view.navigateToTasksList()
        } else {
            LogRepository.insertErro("TaskRegisterPresenter", "Erro ao ${if (isEditing) "editar" else "criar"} tarefa", userId)
            view.showSaveError()
        }
    }

    fun deleteTask(userId: Int, taskId: Int) {
        val success = TaskRepository.delete(userId, taskId)
        if (success) {
            TaskPriorityCache.remove(taskId)
            TaskCategoryCache.remove(taskId)
            LogRepository.insertAcao(userId, "EXCLUIR_TAREFA", "tarefa", taskId)
            view.showDeleteSuccess()
            view.navigateToTasksList()
        } else {
            LogRepository.insertErro("TaskRegisterPresenter", "Erro ao excluir tarefa id=$taskId", userId)
            view.showDeleteError()
        }
    }
}
