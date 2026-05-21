package br.com.mochila.presenter

import br.com.mochila.data.TokenRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.util.EmailService

class EmailVerificationPresenter {

    fun verifyCode(email: String, code: String): String? {
        val trimmedCode = code.trim()
        if (trimmedCode.isBlank()) return "Digite o código recebido no e-mail."
        if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() })
            return "O código deve ter exatamente 6 dígitos."
        if (!TokenRepository.validateAndConsumeToken(email.trim(), trimmedCode))
            return "Código inválido ou expirado. Tente se cadastrar novamente."
        if (!UserRepository.verifyEmail(email.trim()))
            return "Erro ao confirmar cadastro. Tente novamente."
        return null
    }
}

class RecoveryPresenter {

    fun sendRecoveryCode(email: String, confirmEmail: String): String? {
        val trimmedEmail = email.trim()
        val trimmedConfirm = confirmEmail.trim()

        if (trimmedEmail.isBlank() || trimmedConfirm.isBlank())
            return "Preencha os dois campos de e-mail."
        if (trimmedEmail != trimmedConfirm)
            return "Os e-mails não coincidem."
        if (!trimmedEmail.contains("@"))
            return "E-mail inválido."
        if (!UserRepository.emailExists(trimmedEmail))
            return "E-mail não cadastrado."
        if (!EmailService.isConfigured)
            return "Serviço de e-mail não configurado. Contate o suporte."

        val code = (100000..999999).random().toString()

        if (!TokenRepository.saveToken(trimmedEmail, code))
            return "Erro interno ao gerar código. Tente novamente."

        if (!EmailService.sendRecoveryEmail(trimmedEmail, code))
            return "Erro ao enviar o e-mail. Verifique a configuração do SendGrid."

        return null
    }
}

class CodeVerificationPresenter {

    fun verifyCode(email: String, code: String): String? {
        val trimmedCode = code.trim()
        if (trimmedCode.isBlank())
            return "Digite o código recebido no e-mail."
        if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() })
            return "O código deve ter exatamente 6 dígitos."
        if (!TokenRepository.validateAndConsumeToken(email.trim(), trimmedCode))
            return "Código inválido ou expirado. Solicite um novo código."
        return null
    }
}

class NewPasswordPresenter {

    fun resetPassword(email: String, newPassword: String, confirmPassword: String): String? {
        if (newPassword.isBlank() || confirmPassword.isBlank())
            return "Preencha os dois campos de senha."
        if (newPassword != confirmPassword)
            return "As senhas não coincidem."
        if (!isPasswordValid(newPassword))
            return "Senha inválida. Deve ter 8 a 25 caracteres, incluir letra maiúscula, número e caractere especial."

        val user = UserRepository.findByEmail(email.trim())
            ?: return "Usuário não encontrado."

        if (!UserRepository.update(user.id, user.name, user.email, newPassword))
            return "Erro ao atualizar a senha. Tente novamente."

        UserRepository.verifyEmail(email.trim())

        return null
    }

    private fun isPasswordValid(password: String) =
        Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""").matches(password)
}
