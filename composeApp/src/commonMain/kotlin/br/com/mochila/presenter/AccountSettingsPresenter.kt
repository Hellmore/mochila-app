package br.com.mochila.presenter

import br.com.mochila.data.UserRepository
import br.com.mochila.model.User

interface AccountSettingsView {
    fun showUser(user: User)
    fun showPasswordCheckSuccess()
    fun showPasswordCheckError()
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

    fun checkPassword(userId: Int, inputPassword: String) {
        val user = UserRepository.findById(userId)
        if (user != null && user.password == inputPassword) {
            view.showPasswordCheckSuccess()
        } else {
            view.showPasswordCheckError()
        }
    }

    fun saveChanges(
        userId: Int,
        name: String,
        email: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (name.length !in 3..30) {
            view.showValidationError("O nome deve ter entre 3 e 30 caracteres.")
            return
        }

        if (!email.contains("@") || email.length > 30) {
            view.showValidationError("E-mail inválido. Deve conter '@' e ter no máximo 30 caracteres.")
            return
        }

        if (newPassword.isNotBlank()) {
            if (!Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""").matches(newPassword)) {
                view.showValidationError("A senha deve ter 8–25 caracteres, incluir letra maiúscula, número e símbolo.")
                return
            }
            if (newPassword != confirmPassword) {
                view.showValidationError("As senhas não coincidem.")
                return
            }
        }

        val success = UserRepository.update(
            userId = userId,
            name = name,
            email = email,
            newPassword = if (newPassword.isBlank()) null else newPassword
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