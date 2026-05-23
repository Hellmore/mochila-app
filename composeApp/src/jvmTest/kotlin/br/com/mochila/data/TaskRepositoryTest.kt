package br.com.mochila.data

import br.com.mochila.model.Task
import br.com.mochila.model.TaskCategory
import br.com.mochila.model.TaskPriority
import br.com.mochila.model.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskRepositoryTest : RepositoryTestBase() {

    private val userId = 1

    @org.junit.Before
    override fun setUpBase() {
        super.setUpBase()
        UserRepository.insert(User(id = userId, name = "Teste", email = "t@t.com", password = "X"))
    }

    private fun sampleTask(title: String = "Estudar") =
        Task(title = title, description = "Descrição", status = "Pendente")

    @Test fun `insert retorna id gerado`() {
        val id = TaskRepository.insert(userId, sampleTask())
        assertNotNull(id)
        assertTrue(id!! > 0)
    }

    @Test fun `listByUser retorna tarefas do usuario`() {
        TaskRepository.insert(userId, sampleTask("T1"))
        TaskRepository.insert(userId, sampleTask("T2"))
        val tasks = TaskRepository.listByUser(userId)
        assertEquals(2, tasks.size)
    }

    @Test fun `listByUser retorna lista vazia para usuario sem tarefas`() {
        val tasks = TaskRepository.listByUser(userId)
        assertEquals(0, tasks.size)
    }

    @Test fun `findById retorna tarefa correta`() {
        TaskRepository.insert(userId, sampleTask("Estudar Kotlin"))
        val tasks = TaskRepository.listByUser(userId)
        val task = TaskRepository.findById(tasks.first().id)
        assertNotNull(task)
        assertEquals("Estudar Kotlin", task.title)
    }

    @Test fun `findById retorna null para id inexistente`() {
        assertNull(TaskRepository.findById(99999))
    }

    @Test fun `update altera titulo`() {
        TaskRepository.insert(userId, sampleTask("Antes"))
        val task = TaskRepository.listByUser(userId).first()
        val updated = task.copy(title = "Depois")
        assertTrue(TaskRepository.update(userId, updated))
        assertEquals("Depois", TaskRepository.findById(task.id)?.title)
    }

    @Test fun `delete remove tarefa`() {
        TaskRepository.insert(userId, sampleTask())
        val task = TaskRepository.listByUser(userId).first()
        assertTrue(TaskRepository.delete(userId, task.id))
        assertNull(TaskRepository.findById(task.id))
    }

    @Test fun `insert persiste categoria e prioridade`() {
        val id = TaskRepository.insert(
            userId,
            sampleTask().copy(
                category = TaskCategory.FICHAMENTO,
                priority = TaskPriority.ALTA,
            ),
        )!!
        val task = TaskRepository.findById(id)
        assertEquals(TaskCategory.FICHAMENTO, task?.category)
        assertEquals(TaskPriority.ALTA, task?.priority)
    }

    @Test fun `delete retorna false para id inexistente`() {
        assertFalse(TaskRepository.delete(userId, 99999))
    }
}
