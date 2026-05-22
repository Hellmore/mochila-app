package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.AdminUserStats
import br.com.mochila.model.UserSummary

interface AdminUserDetailView {
    fun showUser(user: UserSummary, stats: AdminUserStats)
    fun showNotFound()
    fun showActionSuccess(message: String)
    fun showActionError(message: String)
    fun navigateBack()
}

class AdminUserDetailPresenter(private val view: AdminUserDetailView) {

    private var currentTarget: UserSummary? = null

    fun loadUser(targetUserId: Int) {
        val user = UserRepository.findSummaryById(targetUserId)
        if (user == null) { view.showNotFound(); return }
        val stats = AdminRepository.getUserStats(targetUserId)
        currentTarget = user
        view.showUser(user, stats)
    }

    fun toggleAdmin(currentAdminId: Int, target: UserSummary) {
        if (currentAdminId == target.id && target.isAdmin) {
            view.showActionError("Você não pode remover seu próprio status de admin.")
            return
        }
        val success = if (target.isAdmin) {
            AdminRepository.demoteFromAdmin(target.id)
        } else {
            AdminRepository.promoteToAdmin(target.id)
        }
        if (success) {
            val acao = if (target.isAdmin) "ADMIN_REMOVER_ADMIN" else "ADMIN_PROMOVER_ADMIN"
            LogRepository.insertAcao(currentAdminId, acao, "administrador", target.id)
            val msg = if (target.isAdmin) "\"${target.name}\" removido de admin." else "\"${target.name}\" promovido a admin."
            view.showActionSuccess(msg)
            loadUser(target.id)
        } else {
            view.showActionError("Erro ao alterar status de admin.")
        }
    }

    fun deleteUser(currentAdminId: Int, target: UserSummary) {
        if (currentAdminId == target.id) {
            view.showActionError("Você não pode excluir sua própria conta pelo painel admin.")
            return
        }
        val success = UserRepository.delete(target.id)
        if (success) {
            LogRepository.insertAcao(currentAdminId, "ADMIN_EXCLUIR_USUARIO", "usuario", target.id, "Email: ${target.email}")
            view.showActionSuccess("Usuário \"${target.name}\" excluído.")
            view.navigateBack()
        } else {
            view.showActionError("Erro ao excluir usuário.")
        }
    }
}
