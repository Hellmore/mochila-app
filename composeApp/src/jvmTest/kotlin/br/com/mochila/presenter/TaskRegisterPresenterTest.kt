package br.com.mochila.presenter

import br.com.mochila.data.TaskCategoryCache
import br.com.mochila.data.TaskPriorityCache
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Task
import br.com.mochila.model.TaskCategory
import br.com.mochila.model.TaskPriority
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class TaskRegisterPresenterTest {

    private val view = mockk<TaskRegisterView>(relaxed = true)
    private val presenter = TaskRegisterPresenter(view)

    @Before fun setUp() {
        mockkObject(TaskRepository)
        mockkObject(TaskPriorityCache)
        mockkObject(TaskCategoryCache)
        every { TaskPriorityCache.set(any(), any()) } returns Unit
        every { TaskCategoryCache.set(any(), any()) } returns Unit
    }

    @After fun tearDown() { unmockkAll() }

    private val validTask = Task(id = 1, title = "Estudar", description = "Revisar conteúdo")

    @Test fun `titulo em branco exibe erro de validacao`() {
        presenter.saveTask(1, validTask.copy(title = ""), false, TaskPriority.MEDIA, TaskCategory.default)
        verify { view.showValidationError(any()) }
    }

    @Test fun `descricao em branco exibe erro de validacao`() {
        presenter.saveTask(1, validTask.copy(description = ""), false, TaskPriority.MEDIA, TaskCategory.default)
        verify { view.showValidationError(any()) }
    }

    @Test fun `data invalida exibe erro de validacao`() {
        presenter.saveTask(1, validTask.copy(dueDate = "99/99/9999"), false, TaskPriority.MEDIA, TaskCategory.default)
        verify { view.showValidationError(any()) }
    }

    @Test fun `salvar novo com sucesso chama showSaveSuccess e navega`() {
        every { TaskRepository.insert(any(), any()) } returns 42
        presenter.saveTask(1, validTask, false, TaskPriority.ALTA, TaskCategory.FICHAMENTO)
        verify { TaskPriorityCache.set(42, TaskPriority.ALTA) }
        verify { TaskCategoryCache.set(42, TaskCategory.FICHAMENTO) }
        verify { view.showSaveSuccess(false) }
        verify { view.navigateToTasksList() }
    }

    @Test fun `editar com sucesso chama showSaveSuccess com isEditing true`() {
        every { TaskRepository.update(any(), any()) } returns true
        presenter.saveTask(1, validTask, true, TaskPriority.BAIXA, TaskCategory.TAREFA_DE_CASA)
        verify { TaskPriorityCache.set(1, TaskPriority.BAIXA) }
        verify { TaskCategoryCache.set(1, TaskCategory.TAREFA_DE_CASA) }
        verify { view.showSaveSuccess(true) }
    }

    @Test fun `falha ao salvar exibe erro`() {
        every { TaskRepository.insert(any(), any()) } returns null
        presenter.saveTask(1, validTask, false, TaskPriority.MEDIA, TaskCategory.default)
        verify { view.showSaveError() }
    }

    @Test fun `deleteTask com sucesso navega para lista`() {
        every { TaskRepository.delete(any(), any()) } returns true
        presenter.deleteTask(1, 5)
        verify { view.showDeleteSuccess() }
        verify { view.navigateToTasksList() }
    }

    @Test fun `deleteTask com falha exibe erro`() {
        every { TaskRepository.delete(any(), any()) } returns false
        presenter.deleteTask(1, 5)
        verify { view.showDeleteError() }
    }

    @Test fun `loadTask com tarefa existente chama showTask`() {
        every { TaskRepository.findById(any()) } returns validTask
        presenter.loadTask(1)
        verify { view.showTask(validTask) }
    }

    @Test fun `loadTask sem tarefa nao chama showTask`() {
        every { TaskRepository.findById(any()) } returns null
        presenter.loadTask(99)
        verify(exactly = 0) { view.showTask(any()) }
    }
}
