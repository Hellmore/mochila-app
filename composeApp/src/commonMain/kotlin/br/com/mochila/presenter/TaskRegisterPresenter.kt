package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task

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

    fun saveTask(userId: Int, task: Task, isEditing: Boolean) {
        if (task.title.isBlank() || task.description.isBlank()) {
            view.showValidationError("Título e descrição são obrigatórios.")
            return
        }

        if (!task.dueDate.isNullOrBlank() && !isDateValid(task.dueDate)) {
            view.showValidationError("Data limite inválida.")
            return
        }

        val success = if (isEditing) {
            TaskRepository.update(userId, task)
        } else {
            TaskRepository.insert(userId, task)
        }

        if (success) {
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