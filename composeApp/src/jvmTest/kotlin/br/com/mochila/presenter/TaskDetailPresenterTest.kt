package br.com.mochila.presenter

import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

// Testa detalhe, edicao e exclusao de tarefas
class TaskDetailPresenterTest {

    private val view = mockk<TaskDetailView>(relaxed = true)
    private val presenter = TaskDetailPresenter(view)

    @Before fun setUp()    { mockkObject(TaskRepository) }
    @After  fun tearDown() { unmockkAll() }

    private val task = Task(id = 3, title = "Prova")

    // Carregamento da tarefa
    @Test fun `loadTask encontrado exibe tarefa`() {
        every { TaskRepository.findById(3) } returns task
        presenter.loadTask(3)
        verify { view.showTask(task) }
    }

    @Test fun `loadTask nao encontrado exibe taskNotFound`() {
        every { TaskRepository.findById(any()) } returns null
        presenter.loadTask(99)
        verify { view.showTaskNotFound() }
    }

    // Navegacao para edicao
    @Test fun `onEditClicked navega para edicao`() {
        presenter.onEditClicked(task)
        verify { view.navigateToEdit(task) }
    }

    // Exclusao de tarefa
    @Test fun `onDeleteConfirmed com sucesso navega para lista`() {
        every { TaskRepository.delete(any(), any()) } returns true
        presenter.onDeleteConfirmed(1, task)
        verify { view.showDeleteSuccess() }
        verify { view.navigateToTasksList() }
    }

    @Test fun `onDeleteConfirmed com falha exibe erro`() {
        every { TaskRepository.delete(any(), any()) } returns false
        presenter.onDeleteConfirmed(1, task)
        verify { view.showDeleteError() }
    }
}
