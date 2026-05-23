package br.com.mochila.presenter

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

        val toSave = task.copy(priority = priority, category = category)
        val saved = if (isEditing) {
            TaskRepository.update(userId, toSave)
        } else {
            TaskRepository.insert(userId, toSave) != null
        }

        if (saved) {
            view.showSaveSuccess(isEditing)
            view.navigateToTasksList()
        } else {
            view.showSaveError()
        }
    }

    fun deleteTask(userId: Int, taskId: Int) {
        val success = TaskRepository.delete(userId, taskId)
        if (success) {
            view.showDeleteSuccess()
            view.navigateToTasksList()
        } else {
            view.showDeleteError()
        }
    }
}
