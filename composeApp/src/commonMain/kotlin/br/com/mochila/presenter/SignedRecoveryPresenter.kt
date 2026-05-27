package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository

// Contrato da tela de alteracao de senha logado
interface SignedRecoveryView {
    fun showValidationError(message: String)
    fun showChangeSuccess()
    fun showChangeError()
}

// Altera senha do usuario autenticado
class SignedRecoveryPresenter(private val view: SignedRecoveryView) {

    fun changePassword(
        userId: Int,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        // Carrega usuario para validar senha atual
        val user = UserRepository.findById(userId)
        if (user == null) {
            view.showChangeError()
            return
        }
        // Valida senha atual informada
        if (user.password != currentPassword) {
            view.showValidationError("Senha atual incorreta.")
            return
        }
        // Valida requisitos da nova senha
        if (!Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""").matches(newPassword)) {
            view.showValidationError("A senha deve ter 8–25 caracteres, incluir letra maiúscula, número e símbolo.")
            return
        }
        // Valida coincidencia das novas senhas
        if (newPassword != confirmPassword) {
            view.showValidationError("As senhas não coincidem.")
            return
        }
        // Atualiza senha no repositorio
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
