package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository

interface SignedRecoveryView {
    fun showValidationError(message: String)
    fun showChangeSuccess()
    fun showChangeError()
}

class SignedRecoveryPresenter(private val view: SignedRecoveryView) {

    fun changePassword(
        userId: Int,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        val user = UserRepository.findById(userId)
        if (user == null) {
            view.showChangeError()
            return
        }
        if (user.password != currentPassword) {
            view.showValidationError("Senha atual incorreta.")
            return
        }
        if (!Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""").matches(newPassword)) {
            view.showValidationError("A senha deve ter 8–25 caracteres, incluir letra maiúscula, número e símbolo.")
            return
        }
        if (newPassword != confirmPassword) {
            view.showValidationError("As senhas não coincidem.")
            return
        }
        val success = UserRepository.update(
            userId = userId,
            name = user.name,
            email = user.email,
            newPassword = newPassword
        )
        if (success) {
            LogRepository.insertAcao(userId, "ALTERAR_SENHA", "usuario", userId)
            view.showChangeSuccess()
        } else {
            LogRepository.insertErro("SignedRecoveryPresenter", "Erro ao alterar senha", userId)
            view.showChangeError()
        }
    }
}
