package br.com.mochila.data

import br.com.mochila.model.TaskPriority
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals

class TaskPriorityCacheTest {

    @After
    fun tearDown() {
        TaskPriorityCache.remove(1)
        TaskPriorityCache.remove(2)
    }

    @Test
    fun `retorna MEDIA quando prioridade nao foi definida`() {
        assertEquals(TaskPriority.MEDIA, TaskPriorityCache.get(99))
    }

    @Test
    fun `armazena e recupera prioridade por tarefa`() {
        TaskPriorityCache.set(1, TaskPriority.ALTA)
        assertEquals(TaskPriority.ALTA, TaskPriorityCache.get(1))
    }

    @Test
    fun `remove prioridade ao excluir tarefa do cache`() {
        TaskPriorityCache.set(2, TaskPriority.BAIXA)
        TaskPriorityCache.remove(2)
        assertEquals(TaskPriority.MEDIA, TaskPriorityCache.get(2))
    }
}
