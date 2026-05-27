package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task

// Contrato da tela de listagem de tarefas
interface TaskListView {
    fun showTasks(tasks: List<Task>)
    fun showEmptyState()
    fun navigateToTaskDetail(taskId: Int)
}

// Carrega tarefas, aplica filtros e altera status
class TaskListPresenter(private val view: TaskListView) {

    fun loadTasks(userId: Int) {
        // Busca tarefas do usuario
        val tasks = TaskRepository.listByUser(userId)
        if (tasks.isEmpty()) {
            view.showEmptyState()
        } else {
            view.showTasks(tasks)
        }
    }

    fun onTaskClicked(taskId: Int) {
        view.navigateToTaskDetail(taskId)
    }

    // Filtra tarefas por busca e status
    fun filterTasks(
        tasks: List<Task>,
        searchQuery: String,
        statusFilter: String,
    ): List<Task> {
        var result = tasks
        if (searchQuery.isNotBlank()) {
            result = result.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
        if (statusFilter != "Todos") {
            result = result.filter { it.status == statusFilter }
        }
        return result
    }

    fun completeTask(userId: Int, task: Task) {
        if (task.status == "Concluida") return
        val updated = task.copy(status = "Concluida")
        // Atualiza status no repositorio
        if (TaskRepository.update(userId, updated)) {
            loadTasks(userId)
        }
    }

    fun cancelTask(userId: Int, task: Task) {
        if (task.status == "Cancelada") return
        val updated = task.copy(status = "Cancelada")
        // Atualiza status no repositorio
        if (TaskRepository.update(userId, updated)) {
            loadTasks(userId)
        }
    }
}
