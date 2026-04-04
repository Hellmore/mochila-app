package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task

interface TaskListView {
    fun showTasks(tasks: List<Task>)
    fun showEmptyState()
    fun navigateToTaskDetail(taskId: Int)
}

class TaskListPresenter(private val view: TaskListView) {

    fun loadTasks(userId: Int) {
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

    fun filterTasks(tasks: List<Task>, searchQuery: String): List<Task> {
        if (searchQuery.isBlank()) return tasks
        return tasks.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }
}