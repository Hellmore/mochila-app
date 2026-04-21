package br.com.mochila.presenter

import br.com.mochila.data.UserRepository
import br.com.mochila.model.User

interface AccountSettingsView {
    fun showUser(user: User)
    fun showValidationError(message: String)
    fun showSaveSuccess()
    fun showSaveError()
    fun showDeleteSuccess()
    fun showDeleteError()
    fun navigateBack()
    fun navigateToLogin()
}

class AccountSettingsPresenter(private val view: AccountSettingsView) {

    fun loadUser(userId: Int) {
        val user = UserRepository.findById(userId)
        if (user != null) {
            view.showUser(user)
        }
    }

    fun saveChanges(userId: Int, name: String, email: String) {
        if (name.length !in 3..30) {
            view.showValidationError("O nome deve ter entre 3 e 30 caracteres.")
            return
        }

        if (!email.contains("@") || email.length > 30) {
            view.showValidationError("E-mail inválido. Deve conter '@' e ter no máximo 30 caracteres.")
            return
        }

        val success = UserRepository.update(
            userId = userId,
            name = name,
            email = email,
            newPassword = null
        )

        if (success) view.showSaveSuccess() else view.showSaveError()
    }

    fun deleteAccount(userId: Int) {
        val success = UserRepository.delete(userId)
        if (success) {
            println("✅ Conta excluída com sucesso.")
            view.showDeleteSuccess()
            view.navigateToLogin()
        } else {
            println("⚠️ Erro ao excluir conta.")
            view.showDeleteError()
        }
    }
}