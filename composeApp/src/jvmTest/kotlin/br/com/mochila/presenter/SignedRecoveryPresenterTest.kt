package br.com.mochila.presenter

import br.com.mochila.data.UserRepository
import br.com.mochila.model.User
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

// Testa troca de senha com usuario autenticado
class SignedRecoveryPresenterTest {

    private val view = mockk<SignedRecoveryView>(relaxed = true)
    private val presenter = SignedRecoveryPresenter(view)

    @Before fun setUp()    { mockkObject(UserRepository) }
    @After  fun tearDown() { unmockkAll() }

    private val validUser = User(id = 1, name = "Ana", email = "ana@x.com", password = "atual123")

    // Usuario inexistente
    @Test fun `usuario nao encontrado exibe erro`() {
        every { UserRepository.findById(any()) } returns null
        presenter.changePassword(1, "atual123", "Nova@123", "Nova@123")
        verify { view.showChangeError() }
    }

    // Validacao de senhas
    @Test fun `senha atual incorreta exibe erro de validacao`() {
        every { UserRepository.findById(any()) } returns validUser
        presenter.changePassword(1, "errada", "Nova@123", "Nova@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `nova senha fraca exibe erro de validacao`() {
        every { UserRepository.findById(any()) } returns validUser
        presenter.changePassword(1, "atual123", "fraca", "fraca")
        verify { view.showValidationError(any()) }
    }

    @Test fun `senhas nao coincidem exibe erro de validacao`() {
        every { UserRepository.findById(any()) } returns validUser
        presenter.changePassword(1, "atual123", "Nova@123", "Diferente@1")
        verify { view.showValidationError(any()) }
    }

    // Troca de senha
    @Test fun `troca de senha bem sucedida exibe sucesso`() {
        every { UserRepository.findById(any()) } returns validUser
        every { UserRepository.update(any(), any(), any(), any()) } returns true
        presenter.changePassword(1, "atual123", "Nova@123", "Nova@123")
        verify { view.showChangeSuccess() }
    }

    @Test fun `falha ao atualizar senha exibe erro`() {
        every { UserRepository.findById(any()) } returns validUser
        every { UserRepository.update(any(), any(), any(), any()) } returns false
        presenter.changePassword(1, "atual123", "Nova@123", "Nova@123")
        verify { view.showChangeError() }
    }
}
