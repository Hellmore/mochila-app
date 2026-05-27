package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.User

// Contrato da tela de configuracoes da conta
interface AccountSettingsView {
    fun showUser(user: User)
    fun showValidationError(message: String)
    fun showSaveSuccess()
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun showAdminCodeSuccess()
    fun showAdminCodeError(msg: String)
    fun navigateBack()
    fun navigateToLogin()
}

// Gerencia perfil, codigo admin e exclusao de conta
class AccountSettingsPresenter(private val view: AccountSettingsView) {

    fun loadUser(userId: Int) {
        // Carrega dados do usuario logado
        val user = UserRepository.findById(userId)
        if (user != null) {
            view.showUser(user)
        }
    }

    fun saveChanges(userId: Int, name: String, email: String) {
        // Valida tamanho do nome
        if (name.length !in 3..30) {
            view.showValidationError("O nome deve ter entre 3 e 30 caracteres.")
            return
        }

        // Valida formato do e-mail
        if (!email.contains("@") || email.length > 30) {
            view.showValidationError("E-mail inválido. Deve conter '@' e ter no máximo 30 caracteres.")
            return
        }

        // Atualiza perfil no repositorio
        val success = UserRepository.update(
            userId = userId,
            name = name,
            email = email,
            newPassword = null
        )

        if (success) {
            LogRepository.insertAcao(userId, "ATUALIZAR_PERFIL", "usuario", userId)
            view.showSaveSuccess()
        } else {
            LogRepository.insertErro("AccountSettingsPresenter", "Erro ao atualizar perfil", userId)
            view.showSaveError()
        }
    }

    fun redeemAdminCode(userId: Int, code: String) {
        // Valida preenchimento do codigo
        if (code.isBlank()) {
            view.showAdminCodeError("Digite o código de acesso.")
            return
        }
        // Impede resgate se ja for admin
        if (AdminRepository.isAdmin(userId)) {
            view.showAdminCodeError("Você já é administrador.")
            return
        }
        // Valida codigo secreto
        if (code != AdminRepository.ADMIN_SECRET_CODE) {
            view.showAdminCodeError("Código inválido.")
            return
        }
        // Promove usuario a administrador
        val success = AdminRepository.promoteToAdmin(userId)
        if (success) {
            LogRepository.insertAcao(userId, "ADMIN_PROMOVER_ADMIN", "administrador", userId, "Via código secreto")
            view.showAdminCodeSuccess()
        } else {
            view.showAdminCodeError("Erro ao resgatar o código.")
        }
    }

    fun deleteAccount(userId: Int) {
        // Exclui a conta do usuario
        val success = UserRepository.delete(userId)
        if (success) {
            LogRepository.insertAcao(userId, "EXCLUIR_CONTA", "usuario", userId)
            view.showDeleteSuccess()
            view.navigateToLogin()
        } else {
            LogRepository.insertErro("AccountSettingsPresenter", "Erro ao excluir conta", userId)
            view.showDeleteError()
        }
    }
}
