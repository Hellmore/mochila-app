package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task
import br.com.mochila.model.TaskCategory
import br.com.mochila.model.TaskPriority
import br.com.mochila.util.DateValidator

// Contrato da tela de cadastro e edicao de tarefa
interface TaskRegisterView {
    fun showTask(task: Task)
    fun showValidationError(message: String)
    fun showSaveSuccess(isEditing: Boolean)
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateToTasksList()
}

// Cria, edita e exclui tarefas
class TaskRegisterPresenter(private val view: TaskRegisterView) {

    fun loadTask(taskId: Int) {
        // Busca tarefa existente para edicao
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
        // Valida campos obrigatorios
        if (task.title.isBlank() || task.description.isBlank()) {
            view.showValidationError("Título e descrição são obrigatórios.")
            return
        }

        // Valida formato da data limite
        if (!task.dueDate.isNullOrBlank() && !DateValidator.isValid(task.dueDate)) {
            view.showValidationError("Data limite inválida.")
            return
        }

        val toSave = task.copy(priority = priority, category = category)
        // Atualiza ou insere no repositorio
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
        // Remove a tarefa do banco
        val success = TaskRepository.delete(userId, taskId)
        if (success) {
            view.showDeleteSuccess()
            view.navigateToTasksList()
        } else {
            view.showDeleteError()
        }
    }
}
