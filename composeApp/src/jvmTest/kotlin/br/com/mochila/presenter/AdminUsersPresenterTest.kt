package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.UserSummary
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class AdminUsersPresenterTest {

    private val view = mockk<AdminUsersView>(relaxed = true)
    private val presenter = AdminUsersPresenter(view)

    private val adminUser = UserSummary(1, "Admin", "admin@x.com", isAdmin = true, emailVerified = true, createdAt = "2025-01-01")
    private val regularUser = UserSummary(2, "Regular", "user@x.com", isAdmin = false, emailVerified = true, createdAt = "2025-01-01")
    private val otherAdmin = UserSummary(3, "OutroAdmin", "outro@x.com", isAdmin = true, emailVerified = true, createdAt = "2025-01-01")

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

    @Test fun `loadUsers lista nao vazia chama showUsers`() {
        every { UserRepository.listAll() } returns listOf(adminUser, regularUser)
        presenter.loadUsers()
        verify { view.showUsers(listOf(adminUser, regularUser)) }
    }

    @Test fun `loadUsers lista vazia chama showEmptyState`() {
        every { UserRepository.listAll() } returns emptyList()
        presenter.loadUsers()
        verify { view.showEmptyState() }
    }

    @Test fun `deleteUser com mesmo id exibe erro de auto-exclusao`() {
        presenter.deleteUser(adminId = 1, target = adminUser)
        verify { view.showActionError(any()) }
        verify(exactly = 0) { UserRepository.delete(any()) }
    }

    @Test fun `deleteUser bem sucedido exibe mensagem de sucesso e recarrega lista`() {
        every { UserRepository.delete(2) } returns true
        every { UserRepository.listAll() } returns listOf(adminUser)
        presenter.deleteUser(adminId = 1, target = regularUser)
        verify { view.showActionSuccess(any()) }
        verify { view.showUsers(any()) }
    }

    @Test fun `deleteUser com falha exibe erro`() {
        every { UserRepository.delete(2) } returns false
        presenter.deleteUser(adminId = 1, target = regularUser)
        verify { view.showActionError(any()) }
    }

    @Test fun `toggleAdmin auto-remocao de admin exibe erro`() {
        presenter.toggleAdmin(adminId = 1, target = adminUser)
        verify { view.showActionError(any()) }
        verify(exactly = 0) { AdminRepository.demoteFromAdmin(any()) }
    }

    @Test fun `toggleAdmin promover usuario exibe sucesso e recarrega lista`() {
        every { AdminRepository.promoteToAdmin(2) } returns true
        every { UserRepository.listAll() } returns listOf(adminUser, regularUser)
        presenter.toggleAdmin(adminId = 1, target = regularUser)
        verify { view.showActionSuccess(any()) }
        verify { view.showUsers(any()) }
    }

    @Test fun `toggleAdmin remover admin de outro usuario exibe sucesso e recarrega lista`() {
        every { AdminRepository.demoteFromAdmin(3) } returns true
        every { UserRepository.listAll() } returns listOf(adminUser)
        presenter.toggleAdmin(adminId = 1, target = otherAdmin)
        verify { view.showActionSuccess(any()) }
        verify { view.showUsers(any()) }
    }

    @Test fun `toggleAdmin falha ao promover exibe erro`() {
        every { AdminRepository.promoteToAdmin(2) } returns false
        presenter.toggleAdmin(adminId = 1, target = regularUser)
        verify { view.showActionError(any()) }
    }

    @Test fun `toggleAdmin falha ao remover exibe erro`() {
        every { AdminRepository.demoteFromAdmin(3) } returns false
        presenter.toggleAdmin(adminId = 1, target = otherAdmin)
        verify { view.showActionError(any()) }
    }
}
