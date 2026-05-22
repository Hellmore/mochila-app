package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.User
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class AccountSettingsPresenterTest {

    private val view = mockk<AccountSettingsView>(relaxed = true)
    private val presenter = AccountSettingsPresenter(view)

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

    private val user = User(id = 1, name = "Ana", email = "ana@x.com")

    @Test fun `loadUser existente exibe usuario`() {
        every { UserRepository.findById(1) } returns user
        presenter.loadUser(1)
        verify { view.showUser(user) }
    }

    @Test fun `loadUser nao encontrado nao exibe nada`() {
        every { UserRepository.findById(any()) } returns null
        presenter.loadUser(99)
        verify(exactly = 0) { view.showUser(any()) }
    }

    @Test fun `nome muito curto exibe erro de validacao`() {
        presenter.saveChanges(1, "AB", "ana@x.com")
        verify { view.showValidationError(any()) }
    }

    @Test fun `email sem arroba exibe erro de validacao`() {
        presenter.saveChanges(1, "AnaLuiza", "emailsemarroba")
        verify { view.showValidationError(any()) }
    }

    @Test fun `dados validos com sucesso chama showSaveSuccess`() {
        every { UserRepository.update(any(), any(), any(), any()) } returns true
        presenter.saveChanges(1, "AnaLuiza", "ana@x.com")
        verify { view.showSaveSuccess() }
    }

    @Test fun `falha ao salvar exibe showSaveError`() {
        every { UserRepository.update(any(), any(), any(), any()) } returns false
        presenter.saveChanges(1, "AnaLuiza", "ana@x.com")
        verify { view.showSaveError() }
    }

    @Test fun `deleteAccount com sucesso navega para login`() {
        every { UserRepository.delete(any()) } returns true
        presenter.deleteAccount(1)
        verify { view.showDeleteSuccess() }
        verify { view.navigateToLogin() }
    }

    @Test fun `deleteAccount com falha exibe erro`() {
        every { UserRepository.delete(any()) } returns false
        presenter.deleteAccount(1)
        verify { view.showDeleteError() }
    }

    @Test fun `redeemAdminCode com codigo em branco exibe erro`() {
        presenter.redeemAdminCode(1, "   ")
        verify { view.showAdminCodeError(any()) }
        verify(exactly = 0) { AdminRepository.promoteToAdmin(any()) }
    }

    @Test fun `redeemAdminCode quando usuario ja eh admin exibe erro`() {
        every { AdminRepository.isAdmin(1) } returns true
        presenter.redeemAdminCode(1, AdminRepository.ADMIN_SECRET_CODE)
        verify { view.showAdminCodeError(any()) }
        verify(exactly = 0) { AdminRepository.promoteToAdmin(any()) }
    }

    @Test fun `redeemAdminCode com codigo errado exibe erro`() {
        every { AdminRepository.isAdmin(1) } returns false
        presenter.redeemAdminCode(1, "codigo-errado")
        verify { view.showAdminCodeError(any()) }
        verify(exactly = 0) { AdminRepository.promoteToAdmin(any()) }
    }

    @Test fun `redeemAdminCode com codigo correto chama showAdminCodeSuccess`() {
        every { AdminRepository.isAdmin(1) } returns false
        every { AdminRepository.promoteToAdmin(1) } returns true
        presenter.redeemAdminCode(1, AdminRepository.ADMIN_SECRET_CODE)
        verify { view.showAdminCodeSuccess() }
    }

    @Test fun `redeemAdminCode falha ao promover chama showAdminCodeError`() {
        every { AdminRepository.isAdmin(1) } returns false
        every { AdminRepository.promoteToAdmin(1) } returns false
        presenter.redeemAdminCode(1, AdminRepository.ADMIN_SECRET_CODE)
        verify { view.showAdminCodeError(any()) }
    }
}
