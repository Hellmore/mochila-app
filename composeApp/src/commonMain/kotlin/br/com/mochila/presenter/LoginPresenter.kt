package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository

// Contrato da tela de login
interface LoginView {
    fun showError(message: String)
    fun navigateToHome(userId: Int)
}

// Valida credenciais e autentica o usuario
class LoginPresenter(private val view: LoginView) {

    fun login(email: String, password: String) {
        // Valida campos obrigatorios
        if (email.isBlank() || password.isBlank()) {
            view.showError("E-mail e senha não podem estar em branco.")
            return
        }

        // Consulta existencia do e-mail e valida senha
        val emailExists = UserRepository.emailExists(email)
        val userId = UserRepository.validateLogin(email, password)

        when {
            !emailExists && userId == null -> view.showError("E-mail e senha incorretos.")
            !emailExists -> view.showError("E-mail incorreto.")
            emailExists && userId == null -> {
                LogRepository.insertErro("LoginPresenter", "Senha incorreta para e-mail: $email")
                view.showError("Senha incorreta.")
            }
            else -> {
                // Verifica se o e-mail foi confirmado antes de entrar
                if (!UserRepository.isEmailVerified(email)) {
                    view.showError("E-mail não verificado. Verifique sua caixa de entrada e confirme o código de cadastro.")
                } else {
                    LogRepository.insertAcao(userId!!, "LOGIN", "usuario", userId)
                    view.navigateToHome(userId)
                }
            }
        }
    }
}
