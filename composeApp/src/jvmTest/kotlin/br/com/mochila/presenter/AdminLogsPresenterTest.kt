package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.model.LogAcao
import br.com.mochila.model.LogErro
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class AdminLogsPresenterTest {

    private val view = mockk<AdminLogsView>(relaxed = true)
    private val presenter = AdminLogsPresenter(view)

    @Before fun setUp() { mockkObject(LogRepository) }
    @After  fun tearDown() { unmockkAll() }

    @Test fun `loadLogsAcao repassa resultado do repositorio para a view`() {
        val logs = listOf(
            LogAcao(1, 1, "Ana", "CRIAR_TAREFA", null, "tarefa", null, "2025-01-01")
        )
        every { LogRepository.listAcoes() } returns logs
        presenter.loadLogsAcao()
        verify { view.showLogsAcao(logs) }
    }

    @Test fun `loadLogsAcao lista vazia repassa lista vazia para a view`() {
        every { LogRepository.listAcoes() } returns emptyList()
        presenter.loadLogsAcao()
        verify { view.showLogsAcao(emptyList()) }
    }

    @Test fun `loadLogsErro repassa resultado do repositorio para a view`() {
        val erros = listOf(
            LogErro(1, 1, "Ana", "Modulo", "Erro", "2025-01-01")
        )
        every { LogRepository.listErros() } returns erros
        presenter.loadLogsErro()
        verify { view.showLogsErro(erros) }
    }

    @Test fun `loadLogsErro lista vazia repassa lista vazia para a view`() {
        every { LogRepository.listErros() } returns emptyList()
        presenter.loadLogsErro()
        verify { view.showLogsErro(emptyList()) }
    }
}
