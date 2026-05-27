package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.AdminUserStats
import br.com.mochila.model.UserSummary
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

// Testa detalhe de usuario, promocao admin e exclusao na area admin
class AdminUserDetailPresenterTest {

    private val view = mockk<AdminUserDetailView>(relaxed = true)
    private val presenter = AdminUserDetailPresenter(view)

    private val adminUser = UserSummary(1, "Admin", "admin@x.com", isAdmin = true, emailVerified = true, createdAt = "2025-01-01")
    private val regularUser = UserSummary(2, "Regular", "user@x.com", isAdmin = false, emailVerified = true, createdAt = "2025-01-01")
    private val defaultStats = AdminUserStats(taskCount = 0, subjectCount = 0, faltaCount = 0, eventCount = 0)

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

    // Carregamento do usuario
    @Test fun `loadUser existente chama showUser com stats`() {
        every { UserRepository.findSummaryById(2) } returns regularUser
        every { AdminRepository.getUserStats(2) } returns defaultStats
        presenter.loadUser(2)
        verify { view.showUser(regularUser, defaultStats) }
    }

    @Test fun `loadUser nao encontrado chama showNotFound`() {
        every { UserRepository.findSummaryById(99) } returns null
        presenter.loadUser(99)
        verify { view.showNotFound() }
        verify(exactly = 0) { view.showUser(any(), any()) }
    }

    // Alternar status de administrador
    @Test fun `toggleAdmin auto-remocao exibe erro`() {
        presenter.toggleAdmin(currentAdminId = 1, target = adminUser)
        verify { view.showActionError(any()) }
        verify(exactly = 0) { AdminRepository.demoteFromAdmin(any()) }
    }

    @Test fun `toggleAdmin promover usuario chama showActionSuccess e recarrega`() {
        every { AdminRepository.promoteToAdmin(2) } returns true
        every { UserRepository.findSummaryById(2) } returns regularUser
        every { AdminRepository.getUserStats(2) } returns defaultStats
        presenter.toggleAdmin(currentAdminId = 1, target = regularUser)
        verify { view.showActionSuccess(any()) }
        verify { view.showUser(regularUser, defaultStats) }
    }

    @Test fun `toggleAdmin remover admin chama showActionSuccess e recarrega`() {
        val otherAdmin = UserSummary(3, "Outro", "outro@x.com", isAdmin = true, emailVerified = true, createdAt = "2025-01-01")
        every { AdminRepository.demoteFromAdmin(3) } returns true
        every { UserRepository.findSummaryById(3) } returns otherAdmin
        every { AdminRepository.getUserStats(3) } returns defaultStats
        presenter.toggleAdmin(currentAdminId = 1, target = otherAdmin)
        verify { view.showActionSuccess(any()) }
    }

    @Test fun `toggleAdmin falha chama showActionError`() {
        every { AdminRepository.promoteToAdmin(2) } returns false
        presenter.toggleAdmin(currentAdminId = 1, target = regularUser)
        verify { view.showActionError(any()) }
    }

    // Exclusao de usuario
    @Test fun `deleteUser auto-exclusao exibe erro`() {
        presenter.deleteUser(currentAdminId = 1, target = adminUser)
        verify { view.showActionError(any()) }
        verify(exactly = 0) { UserRepository.delete(any()) }
    }

    @Test fun `deleteUser bem sucedido navega de volta`() {
        every { UserRepository.delete(2) } returns true
        presenter.deleteUser(currentAdminId = 1, target = regularUser)
        verify { view.showActionSuccess(any()) }
        verify { view.navigateBack() }
    }

    @Test fun `deleteUser falha exibe erro`() {
        every { UserRepository.delete(2) } returns false
        presenter.deleteUser(currentAdminId = 1, target = regularUser)
        verify { view.showActionError(any()) }
        verify(exactly = 0) { view.navigateBack() }
    }
}
