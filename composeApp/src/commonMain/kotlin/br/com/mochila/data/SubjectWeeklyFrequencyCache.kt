package br.com.mochila.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Aulas semanais por disciplina (campo "Frequência" no cadastro).
 * Persistido em arquivo local — não usa coluna no banco.
 */
object SubjectWeeklyFrequencyCache {
    private const val FILE_NAME = "subject_weekly_frequency.txt"
    private const val DEFAULT_WEEKLY_CLASSES = 1
    private val store = mutableMapOf<Int, Int>()
    private var loaded = false

    var revision by mutableStateOf(0)
        private set

    fun get(subjectId: Int): Int {
        ensureLoaded()
        return store[subjectId] ?: DEFAULT_WEEKLY_CLASSES
    }

    fun set(subjectId: Int, weeklyClassCount: Int) {
        ensureLoaded()
        val value = weeklyClassCount.coerceAtLeast(1)
        if (store[subjectId] != value) {
            store[subjectId] = value
            revision++
            persist()
        }
    }

    fun remove(subjectId: Int) {
        ensureLoaded()
        if (store.remove(subjectId) != null) {
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
                val count = trimmed.substring(sep + 1).trim().toIntOrNull() ?: return@forEach
                if (count > 0) store[id] = count
            }
    }

    private fun persist() {
        val content = store.entries
            .sortedBy { it.key }
            .joinToString("\n") { (id, count) -> "$id=$count" }
        CategoryFileStore.write(FILE_NAME, content)
    }
}
