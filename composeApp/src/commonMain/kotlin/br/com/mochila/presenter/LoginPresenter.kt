package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Contrato da tela de login
interface LoginView {
    fun showError(message: String)
    fun navigateToHome(userId: Int)
}

// Valida credenciais e autentica o usuario
class LoginPresenter(private val view: LoginView) {

    suspend fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view.showError("E-mail e senha não podem estar em branco.")
            return
        }

        try {
            val emailExists = withContext(Dispatchers.IO) { UserRepository.emailExists(email) }
            val userId      = withContext(Dispatchers.IO) { UserRepository.validateLogin(email, password) }

            when {
                !emailExists && userId == null -> view.showError("E-mail e senha incorretos.")
                !emailExists -> view.showError("E-mail incorreto.")
                emailExists && userId == null -> {
                    runCatching { withContext(Dispatchers.IO) {
                        LogRepository.insertErro("LoginPresenter", "Senha incorreta para e-mail: $email")
                    }}
                    view.showError("Senha incorreta.")
                }
                else -> {
                    val isVerified = withContext(Dispatchers.IO) { UserRepository.isEmailVerified(email) }
                    if (!isVerified) {
                        view.showError("E-mail não verificado. Verifique sua caixa de entrada e confirme o código de cadastro.")
                    } else {
                        runCatching { withContext(Dispatchers.IO) {
                            LogRepository.insertAcao(userId!!, "LOGIN", "usuario", userId)
                        }}
                        view.navigateToHome(userId!!)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            println("⚠️ Erro inesperado no login: ${e::class.simpleName}: ${e.message}")
            view.showError("Erro ao fazer login. Tente novamente.")
        }
    }
}
