package br.com.mochila.presenter

import br.com.mochila.data.UserRepository
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

// Testa validacao de credenciais e fluxo de login
class LoginPresenterTest {

    private val view = mockk<LoginView>(relaxed = true)
    private val presenter = LoginPresenter(view)

    @Before fun setUp()    { mockkObject(UserRepository) }
    @After  fun tearDown() { unmockkAll() }

    // Validacao de campos obrigatorios
    @Test fun `email em branco exibe erro e nao navega`() {
        presenter.login("", "senha")
        verify { view.showError(any()) }
        verify(exactly = 0) { view.navigateToHome(any()) }
    }

    @Test fun `senha em branco exibe erro`() {
        presenter.login("a@b.com", "")
        verify { view.showError(any()) }
    }

    // Credenciais invalidas
    @Test fun `email inexistente e senha nula exibem erro de credenciais`() {
        every { UserRepository.emailExists(any()) } returns false
        every { UserRepository.validateLogin(any(), any()) } returns null
        presenter.login("x@x.com", "senha")
        verify { view.showError(any()) }
        verify(exactly = 0) { view.navigateToHome(any()) }
    }

    @Test fun `email existe mas senha incorreta exibe erro de senha`() {
        every { UserRepository.emailExists(any()) } returns true
        every { UserRepository.validateLogin(any(), any()) } returns null
        presenter.login("x@x.com", "errada")
        verify { view.showError(any()) }
    }

    // Email nao verificado
    @Test fun `email nao verificado exibe erro de verificacao`() {
        every { UserRepository.emailExists(any()) } returns true
        every { UserRepository.validateLogin(any(), any()) } returns 1
        every { UserRepository.isEmailVerified(any()) } returns false
        presenter.login("x@x.com", "Senha@1")
        verify { view.showError(any()) }
        verify(exactly = 0) { view.navigateToHome(any()) }
    }

    // Login bem sucedido
    @Test fun `login valido navega para home com userId correto`() {
        every { UserRepository.emailExists(any()) } returns true
        every { UserRepository.validateLogin(any(), any()) } returns 42
        every { UserRepository.isEmailVerified(any()) } returns true
        presenter.login("x@x.com", "Senha@1")
        verify { view.navigateToHome(42) }
    }
}
