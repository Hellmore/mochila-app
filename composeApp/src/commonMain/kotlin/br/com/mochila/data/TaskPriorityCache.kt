package br.com.mochila.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.mochila.model.TaskPriority

object TaskPriorityCache {
    private val store = mutableMapOf<Int, TaskPriority>()

    var revision by mutableStateOf(0)
        private set

    fun get(taskId: Int): TaskPriority = store[taskId] ?: TaskPriority.MEDIA

    fun set(taskId: Int, priority: TaskPriority) {
        if (store[taskId] != priority) {
            store[taskId] = priority
            revision++
        }
    }

    fun remove(taskId: Int) {
        if (store.remove(taskId) != null) {
            revision++
        }
    }
}
