package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository

interface LoginView {
    fun showError(message: String)
    fun navigateToHome(userId: Int)
}

class LoginPresenter(private val view: LoginView) {

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view.showError("E-mail e senha não podem estar em branco.")
            return
        }

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