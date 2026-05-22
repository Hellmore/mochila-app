package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.UserSummary

interface AdminUsersView {
    fun showUsers(users: List<UserSummary>)
    fun showEmptyState()
    fun showActionSuccess(message: String)
    fun showActionError(message: String)
}

class AdminUsersPresenter(private val view: AdminUsersView) {

    fun loadUsers() {
        val users = UserRepository.listAll()
        if (users.isEmpty()) view.showEmptyState() else view.showUsers(users)
    }

    fun deleteUser(adminId: Int, target: UserSummary) {
        if (adminId == target.id) {
            view.showActionError("Você não pode excluir sua própria conta pelo painel admin.")
            return
        }
        val success = UserRepository.delete(target.id)
        if (success) {
            LogRepository.insertAcao(adminId, "ADMIN_EXCLUIR_USUARIO", "usuario", target.id, "Email: ${target.email}")
            view.showActionSuccess("Usuário \"${target.name}\" excluído.")
            loadUsers()
        } else {
            view.showActionError("Erro ao excluir usuário.")
        }
    }

    fun toggleAdmin(adminId: Int, target: UserSummary) {
        if (adminId == target.id && target.isAdmin) {
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
            LogRepository.insertAcao(adminId, acao, "administrador", target.id)
            val msg = if (target.isAdmin) "\"${target.name}\" removido de admin." else "\"${target.name}\" promovido a admin."
            view.showActionSuccess(msg)
            loadUsers()
        } else {
            view.showActionError("Erro ao alterar status de admin.")
        }
    }
}
