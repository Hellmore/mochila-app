package br.com.mochila.presenter

import br.com.mochila.data.LogRepository
import br.com.mochila.data.TokenRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.util.EmailService

// Valida codigo de verificacao de e-mail no cadastro
class EmailVerificationPresenter {

    fun verifyCode(email: String, code: String): String? {
        val trimmedCode = code.trim()
        // Valida preenchimento do codigo
        if (trimmedCode.isBlank()) return "Digite o código recebido no e-mail."
        // Valida formato de 6 digitos
        if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() })
            return "O código deve ter exatamente 6 dígitos."
        // Consome token e confirma verificacao
        if (!TokenRepository.validateAndConsumeToken(email.trim(), trimmedCode))
            return "Código inválido ou expirado. Tente se cadastrar novamente."
        if (!UserRepository.verifyEmail(email.trim()))
            return "Erro ao confirmar cadastro. Tente novamente."
        return null
    }
}

// Envia codigo de recuperacao de senha por e-mail
class RecoveryPresenter {

    suspend fun sendRecoveryCode(email: String, confirmEmail: String): String? {
        val trimmedEmail = email.trim()
        val trimmedConfirm = confirmEmail.trim()

        // Valida campos obrigatorios
        if (trimmedEmail.isBlank() || trimmedConfirm.isBlank())
            return "Preencha os dois campos de e-mail."
        // Valida coincidencia dos e-mails
        if (trimmedEmail != trimmedConfirm)
            return "Os e-mails não coincidem."
        // Valida formato basico do e-mail
        if (!trimmedEmail.contains("@"))
            return "E-mail inválido."
        // Verifica se o e-mail esta cadastrado
        if (!UserRepository.emailExists(trimmedEmail))
            return "E-mail não cadastrado."
        // Verifica configuracao do servico de e-mail
        if (!EmailService.isConfigured)
            return "Serviço de e-mail não configurado. Contate o suporte."

        val code = (100000..999999).random().toString()

        // Salva token de recuperacao no banco
        if (!TokenRepository.saveToken(trimmedEmail, code))
            return "Erro interno ao gerar código. Tente novamente."

        // Envia codigo por e-mail
        if (!EmailService.sendRecoveryEmail(trimmedEmail, code))
            return "Erro ao enviar o e-mail. Verifique a configuração do SendGrid."

        return null
    }
}

// Valida codigo de recuperacao antes de redefinir senha
class CodeVerificationPresenter {

    fun verifyCode(email: String, code: String): String? {
        val trimmedCode = code.trim()
        // Valida preenchimento do codigo
        if (trimmedCode.isBlank())
            return "Digite o código recebido no e-mail."
        // Valida formato de 6 digitos
        if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() })
            return "O código deve ter exatamente 6 dígitos."
        // Consome token de recuperacao
        if (!TokenRepository.validateAndConsumeToken(email.trim(), trimmedCode))
            return "Código inválido ou expirado. Solicite um novo código."
        return null
    }
}

// Redefine a senha apos validacao do codigo
class NewPasswordPresenter {

    fun resetPassword(email: String, newPassword: String, confirmPassword: String): String? {
        // Valida campos obrigatorios
        if (newPassword.isBlank() || confirmPassword.isBlank())
            return "Preencha os dois campos de senha."
        // Valida coincidencia das senhas
        if (newPassword != confirmPassword)
            return "As senhas não coincidem."
        // Valida requisitos da nova senha
        if (!isPasswordValid(newPassword))
            return "Senha inválida. Deve ter 8 a 25 caracteres, incluir letra maiúscula, número e caractere especial."

        // Busca usuario e atualiza senha
        val user = UserRepository.findByEmail(email.trim())
            ?: return "Usuário não encontrado."

        if (!UserRepository.update(user.id, user.name, user.email, newPassword))
            return "Erro ao atualizar a senha. Tente novamente."

        UserRepository.verifyEmail(email.trim())
        LogRepository.insertAcao(user.id, "RESET_SENHA", "usuario", user.id)

        return null
    }

    private fun isPasswordValid(password: String) =
        Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""").matches(password)
}
