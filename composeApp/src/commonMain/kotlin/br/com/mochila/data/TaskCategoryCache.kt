package br.com.mochila.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.mochila.model.TaskCategory

object TaskCategoryCache {
    private const val FILE_NAME = "task_categories.txt"
    private val store = mutableMapOf<Int, TaskCategory>()
    private var loaded = false

    var revision by mutableStateOf(0)
        private set

    fun get(taskId: Int): TaskCategory {
        ensureLoaded()
        return store[taskId] ?: TaskCategory.default
    }

    fun set(taskId: Int, category: TaskCategory) {
        ensureLoaded()
        if (store[taskId] != category) {
            store[taskId] = category
            revision++
            persist()
        }
    }

    fun remove(taskId: Int) {
        ensureLoaded()
        if (store.remove(taskId) != null) {
            revision++
            persist()
        }
    }

    internal fun resetForTests() {
        loaded = false
        store.clear()
        revision = 0
    }

    private fun ensureLoaded() {
        if (loaded) return
        loaded = true
        CategoryFileStore.read(FILE_NAME)
            ?.lineSequence()
            ?.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty()) return@forEach
                val sep = trimmed.indexOf('=')
                if (sep <= 0) return@forEach
                val id = trimmed.substring(0, sep).toIntOrNull() ?: return@forEach
                val name = trimmed.substring(sep + 1).trim()
                TaskCategory.fromNameOrNull(name)?.let { store[id] = it }
            }
    }

    private fun persist() {
        val content = store.entries
            .sortedBy { it.key }
            .joinToString("\n") { (id, category) -> "$id=${category.name}" }
        CategoryFileStore.write(FILE_NAME, content)
    }
}
