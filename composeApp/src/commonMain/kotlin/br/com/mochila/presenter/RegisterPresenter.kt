package br.com.mochila.presenter

import br.com.mochila.data.AdminRepository
import br.com.mochila.data.LogRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.User
import br.com.mochila.util.TempEmailValidator

// Contrato da tela de cadastro de usuario
interface RegisterView {
    fun showValidationError(message: String)
    fun showRegisterSuccess()
    fun showRegisterError(message: String)
    fun navigateToEmailVerify(email: String)
}

// Valida dados e cadastra novo usuario
class RegisterPresenter(private val view: RegisterView) {

    suspend fun register(name: String, email: String, password: String, adminCode: String = "") {
        // Valida todos os campos de uma vez
        if (!isEmailValid(email) && !isNameValid(name) && !isPasswordValid(password)) {
            view.showValidationError("Todos os campos estão incorretos. Verifique e tente novamente.")
            return
        }

        // Valida formato do e-mail
        if (!isEmailValid(email)) {
            view.showValidationError("E-mail inválido. Deve conter '@' e ter no máximo 30 caracteres.")
            return
        }

        // Rejeita e-mails temporarios ou descartaveis
        if (TempEmailValidator.isDisposableAsync(email)) {
            view.showValidationError("Não é permitido usar e-mails temporários ou descartáveis.")
            return
        }

        // Valida tamanho do nome
        if (!isNameValid(name)) {
            view.showValidationError("O nome de usuário deve ter entre 3 e 30 caracteres.")
            return
        }

        // Valida requisitos da senha
        if (!isPasswordValid(password)) {
            view.showValidationError("Senha inválida. Deve ter 8 a 25 caracteres, incluir letra maiúscula, número e caractere especial.")
            return
        }

        // Insere o usuario no banco
        val success = UserRepository.insert(User(name = name, email = email, password = password))

        if (success) {
            val newUser = UserRepository.findByEmail(email)
            if (newUser != null) {
                LogRepository.insertAcao(newUser.id, "CADASTRO", "usuario", newUser.id)
                // Promove a admin se o codigo secreto for informado
                if (adminCode.isNotBlank() && adminCode == AdminRepository.ADMIN_SECRET_CODE) {
                    AdminRepository.promoteToAdmin(newUser.id)
                    LogRepository.insertAcao(newUser.id, "ADMIN_PROMOVER_ADMIN", "administrador", newUser.id, "Via código secreto")
                }
            }
            view.showRegisterSuccess()
            view.navigateToEmailVerify(email)
        } else {
            LogRepository.insertErro("RegisterPresenter", "Erro ao cadastrar e-mail: $email")
            view.showRegisterError("Erro ao cadastrar usuário. Verifique se o e-mail já existe.")
        }
    }

    private fun isEmailValid(email: String) = email.contains("@") && email.length <= 30

    private fun isNameValid(name: String) = name.length in 3..30

    private fun isPasswordValid(password: String) =
        Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""").matches(password)
}
