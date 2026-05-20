package br.com.mochila.presenter

import br.com.mochila.data.UserRepository
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class RegisterPresenterTest {

    private val view = mockk<RegisterView>(relaxed = true)
    private val presenter = RegisterPresenter(view)

    @Before fun setUp()    { mockkObject(UserRepository) }
    @After  fun tearDown() { unmockkAll() }

    @Test fun `email sem arroba exibe erro de validacao`() {
        presenter.register("NomeValido", "emailinvalido", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email com mais de 30 caracteres exibe erro`() {
        presenter.register("NomeValido", "emailmuitolongodemaisdoquetrinta@x.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `nome com menos de 3 caracteres exibe erro`() {
        presenter.register("Ab", "a@b.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `senha sem maiuscula exibe erro`() {
        presenter.register("NomeValido", "a@b.com", "senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `senha sem caracter especial exibe erro`() {
        presenter.register("NomeValido", "a@b.com", "Senha1234")
        verify { view.showValidationError(any()) }
    }

    @Test fun `cadastro bem sucedido chama showRegisterSuccess e navega`() {
        every { UserRepository.insert(any()) } returns true
        presenter.register("NomeValido", "a@b.com", "Senha@123")
        verify { view.showRegisterSuccess() }
        verify { view.navigateToEmailVerify("a@b.com") }
    }

    @Test fun `falha no insert chama showRegisterError`() {
        every { UserRepository.insert(any()) } returns false
        presenter.register("NomeValido", "a@b.com", "Senha@123")
        verify { view.showRegisterError(any()) }
    }
}
