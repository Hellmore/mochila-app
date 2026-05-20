package br.com.mochila.presenter

import br.com.mochila.data.SubjectRepository
import br.com.mochila.data.TaskRepository
import br.com.mochila.model.Subject
import br.com.mochila.model.Task
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomePresenterTest {

    private val view = mockk<HomeView>(relaxed = true)
    private val presenter = HomePresenter(view)

    @Before fun setUp() {
        mockkObject(SubjectRepository)
        mockkObject(TaskRepository)
    }

    @After fun tearDown() { unmockkAll() }

    @Test fun `lista vazia de disciplinas exibe estado vazio`() {
        every { SubjectRepository.listByUser(any()) } returns emptyList()
        presenter.loadSubjects(1)
        verify { view.showEmptyState() }
        verify(exactly = 0) { view.showSubjects(any()) }
    }

    @Test fun `lista com disciplinas chama showSubjects`() {
        val subjects = listOf(Subject(id = 1, name = "Math", semester = "2025.1"))
        every { SubjectRepository.listByUser(any()) } returns subjects
        presenter.loadSubjects(1)
        verify { view.showSubjects(subjects) }
    }

    @Test fun `filterSubjects sem filtro retorna todas`() {
        val s1 = Subject(name = "Math", semester = "2025.1")
        val s2 = Subject(name = "Physics", semester = "2025.2")
        val result = presenter.filterSubjects(listOf(s1, s2), "Todos", "")
        assertEquals(2, result.size)
    }

    @Test fun `filterSubjects por semestre filtra corretamente`() {
        val s1 = Subject(name = "Math", semester = "2025.1")
        val s2 = Subject(name = "Physics", semester = "2025.2")
        val result = presenter.filterSubjects(listOf(s1, s2), "2025.1", "")
        assertEquals(1, result.size)
        assertEquals("Math", result.first().name)
    }

    @Test fun `filterSubjects por nome filtra corretamente`() {
        val s1 = Subject(name = "Mathematics", semester = "2025.1")
        val s2 = Subject(name = "Physics", semester = "2025.1")
        val result = presenter.filterSubjects(listOf(s1, s2), "Todos", "math")
        assertEquals(1, result.size)
        assertEquals("Mathematics", result.first().name)
    }

    @Test fun `getSemesters retorna semestres distintos ordenados`() {
        val subjects = listOf(
            Subject(semester = "2025.2"),
            Subject(semester = "2025.1"),
            Subject(semester = "2025.1"),
        )
        val result = presenter.getSemesters(subjects)
        assertEquals(listOf("2025.1", "2025.2"), result)
    }

    @Test fun `getSemesters ignora semestres em branco`() {
        val subjects = listOf(Subject(semester = ""), Subject(semester = "2025.1"))
        val result = presenter.getSemesters(subjects)
        assertEquals(listOf("2025.1"), result)
    }

    @Test fun `loadPendingTasks passa somente tarefas pendentes para a view`() {
        val tasks = listOf(
            Task(title = "T1", status = "Pendente"),
            Task(title = "T2", status = "Concluida"),
        )
        every { TaskRepository.listByUser(any()) } returns tasks
        presenter.loadPendingTasks(1)
        val capturado = slot<List<Task>>()
        verify { view.showPendingTasks(capture(capturado)) }
        assertTrue(capturado.captured.all { it.status == "Pendente" })
        assertEquals(1, capturado.captured.size)
    }
}
