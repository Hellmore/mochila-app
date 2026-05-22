package br.com.mochila.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.mochila.model.EventCategory

object EventCategoryCache {
    private const val FILE_NAME = "event_categories.txt"
    private val store = mutableMapOf<Int, EventCategory>()
    private var loaded = false

    var revision by mutableStateOf(0)
        private set

    fun get(eventId: Int): EventCategory {
        ensureLoaded()
        return store[eventId] ?: EventCategory.default
    }

    fun set(eventId: Int, category: EventCategory) {
        ensureLoaded()
        if (store[eventId] != category) {
            store[eventId] = category
            revision++
            persist()
        }
    }

    fun remove(eventId: Int) {
        ensureLoaded()
        if (store.remove(eventId) != null) {
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
                EventCategory.fromNameOrNull(name)?.let { store[id] = it }
            }
    }

    private fun persist() {
        val content = store.entries
            .sortedBy { it.key }
            .joinToString("\n") { (id, category) -> "$id=${category.name}" }
        CategoryFileStore.write(FILE_NAME, content)
    }
}
