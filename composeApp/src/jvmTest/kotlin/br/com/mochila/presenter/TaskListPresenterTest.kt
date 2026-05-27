package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

// Testa listagem, filtros e alteracao de status de tarefas
class TaskListPresenterTest {

    private val view = mockk<TaskListView>(relaxed = true)
    private val presenter = TaskListPresenter(view)

    @Before fun setUp()    { mockkObject(TaskRepository) }
    @After  fun tearDown() { unmockkAll() }

    // Carregamento da lista
    @Test fun `lista vazia exibe estado vazio`() {
        every { TaskRepository.listByUser(any()) } returns emptyList()
        presenter.loadTasks(1)
        verify { view.showEmptyState() }
        verify(exactly = 0) { view.showTasks(any()) }
    }

    @Test fun `lista com tarefas chama showTasks`() {
        val tasks = listOf(Task(id = 1, title = "Estudar"))
        every { TaskRepository.listByUser(any()) } returns tasks
        presenter.loadTasks(1)
        verify { view.showTasks(tasks) }
    }

    // Navegacao
    @Test fun `onTaskClicked navega para o detalhe`() {
        presenter.onTaskClicked(7)
        verify { view.navigateToTaskDetail(7) }
    }

    // Filtros
    @Test fun `filterTasks com query vazia e filtro Todos retorna todas`() {
        val tasks = listOf(Task(title = "A"), Task(title = "B"))
        val result = presenter.filterTasks(tasks, "", "Todos")
        assertEquals(2, result.size)
    }

    @Test fun `filterTasks filtra por titulo ignorando maiusculas`() {
        val tasks = listOf(Task(title = "Estudar Kotlin"), Task(title = "Fazer Prova"))
        val result = presenter.filterTasks(tasks, "kotlin", "Todos")
        assertEquals(1, result.size)
        assertEquals("Estudar Kotlin", result.first().title)
    }

    @Test fun `filterTasks filtra por status`() {
        val tasks = listOf(
            Task(title = "T1", status = "Pendente"),
            Task(title = "T2", status = "Concluida"),
        )
        val result = presenter.filterTasks(tasks, "", "Pendente")
        assertEquals(1, result.size)
        assertEquals("T1", result.first().title)
    }

    // Conclusao de tarefa
    @Test fun `completeTask atualiza status para Concluida`() {
        val task = Task(id = 1, title = "Estudar", status = "Pendente")
        every { TaskRepository.update(any(), any()) } returns true
        every { TaskRepository.listByUser(any()) } returns emptyList()
        presenter.completeTask(1, task)
        val capturado = slot<Task>()
        verify { TaskRepository.update(any(), capture(capturado)) }
        assertEquals("Concluida", capturado.captured.status)
    }

    @Test fun `completeTask nao atualiza se ja estiver Concluida`() {
        val task = Task(id = 1, title = "Estudar", status = "Concluida")
        presenter.completeTask(1, task)
        verify(exactly = 0) { TaskRepository.update(any(), any()) }
    }

    // Cancelamento de tarefa
    @Test fun `cancelTask atualiza status para Cancelada`() {
        val task = Task(id = 1, title = "Estudar", status = "Pendente")
        every { TaskRepository.update(any(), any()) } returns true
        every { TaskRepository.listByUser(any()) } returns emptyList()
        presenter.cancelTask(1, task)
        val capturado = slot<Task>()
        verify { TaskRepository.update(any(), capture(capturado)) }
        assertEquals("Cancelada", capturado.captured.status)
    }
}
