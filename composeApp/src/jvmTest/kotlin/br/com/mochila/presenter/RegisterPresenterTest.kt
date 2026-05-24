package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.User
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class RegisterPresenterTest {

    private val view = mockk<RegisterView>(relaxed = true)
    private val presenter = RegisterPresenter(view)

    @Before fun setUp() {
        mockkObject(UserRepository)
        mockkObject(AdminRepository)
        mockkObject(LogRepository)
        every { LogRepository.insertAcao(any(), any(), any(), any()) } just Runs
        every { LogRepository.insertAcao(any(), any(), any(), any(), any()) } just Runs
        every { LogRepository.insertErro(any(), any()) } just Runs
        every { LogRepository.insertErro(any(), any(), any()) } just Runs
    }

    @After fun tearDown() { unmockkAll() }

    private val registeredUser = User(id = 1, name = "NomeValido", email = "a@b.com")

    @Test fun `email sem arroba exibe erro de validacao`() = runTest {
        presenter.register("NomeValido", "emailinvalido", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email com mais de 30 caracteres exibe erro`() = runTest {
        presenter.register("NomeValido", "emailmuitolongodemaisdoquetrinta@x.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email temporario mailinator exibe erro de validacao`() = runTest {
        presenter.register("NomeValido", "teste@mailinator.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email temporario yopmail exibe erro de validacao`() = runTest {
        presenter.register("NomeValido", "usuario@yopmail.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email temporario guerrillamail exibe erro de validacao`() = runTest {
        presenter.register("NomeValido", "fulano@guerrillamail.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email temporario nao chama UserRepository insert`() = runTest {
        presenter.register("NomeValido", "teste@tempmail.com", "Senha@123")
        verify(exactly = 0) { UserRepository.insert(any()) }
    }

    @Test fun `nome com menos de 3 caracteres exibe erro`() = runTest {
        presenter.register("Ab", "a@b.com", "Senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `senha sem maiuscula exibe erro`() = runTest {
        presenter.register("NomeValido", "a@b.com", "senha@123")
        verify { view.showValidationError(any()) }
    }

    @Test fun `senha sem caracter especial exibe erro`() = runTest {
        presenter.register("NomeValido", "a@b.com", "Senha1234")
        verify { view.showValidationError(any()) }
    }

    @Test fun `cadastro bem sucedido chama showRegisterSuccess e navega`() = runTest {
        every { UserRepository.insert(any()) } returns true
        every { UserRepository.findByEmail(any()) } returns registeredUser
        presenter.register("NomeValido", "a@b.com", "Senha@123")
        verify { view.showRegisterSuccess() }
        verify { view.navigateToEmailVerify("a@b.com") }
    }

    @Test fun `falha no insert chama showRegisterError`() = runTest {
        every { UserRepository.insert(any()) } returns false
        presenter.register("NomeValido", "a@b.com", "Senha@123")
        verify { view.showRegisterError(any()) }
    }

    @Test fun `registro com adminCode valido promove usuario a admin`() = runTest {
        every { UserRepository.insert(any()) } returns true
        every { UserRepository.findByEmail(any()) } returns registeredUser
        every { AdminRepository.promoteToAdmin(1) } returns true
        presenter.register("NomeValido", "a@b.com", "Senha@123", adminCode = AdminRepository.ADMIN_SECRET_CODE)
        verify { AdminRepository.promoteToAdmin(1) }
        verify { view.showRegisterSuccess() }
    }

    @Test fun `registro com adminCode invalido nao chama promoteToAdmin`() = runTest {
        every { UserRepository.insert(any()) } returns true
        every { UserRepository.findByEmail(any()) } returns registeredUser
        presenter.register("NomeValido", "a@b.com", "Senha@123", adminCode = "codigo-errado")
        verify(exactly = 0) { AdminRepository.promoteToAdmin(any()) }
    }

    @Test fun `registro sem adminCode nao chama promoteToAdmin`() = runTest {
        every { UserRepository.insert(any()) } returns true
        every { UserRepository.findByEmail(any()) } returns registeredUser
        presenter.register("NomeValido", "a@b.com", "Senha@123")
        verify(exactly = 0) { AdminRepository.promoteToAdmin(any()) }
    }

    @Test fun `falha no insert loga erro`() = runTest {
        every { UserRepository.insert(any()) } returns false
        presenter.register("NomeValido", "a@b.com", "Senha@123")
        verify { LogRepository.insertErro(any(), any()) }
    }
}
