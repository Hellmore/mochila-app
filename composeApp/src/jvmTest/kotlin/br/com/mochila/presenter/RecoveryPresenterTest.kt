package br.com.mochila.presenter

import br.com.mochila.data.TokenRepository
import br.com.mochila.data.UserRepository
import br.com.mochila.model.User
import br.com.mochila.util.EmailService
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// Testa recuperacao de senha, verificacao de codigo e nova senha
class RecoveryPresenterTest {

    @Before fun setUp() {
        mockkObject(UserRepository)
        mockkObject(TokenRepository)
        mockkObject(EmailService)
    }

    @After fun tearDown() { unmockkAll() }

    // Envio do codigo de recuperacao
    @Test fun `sendRecoveryCode com campos em branco retorna erro`() {
        val result = RecoveryPresenter().sendRecoveryCode("", "a@b.com")
        assertNotNull(result)
    }

    @Test fun `sendRecoveryCode com emails diferentes retorna erro`() {
        val result = RecoveryPresenter().sendRecoveryCode("a@b.com", "c@d.com")
        assertNotNull(result)
    }

    @Test fun `sendRecoveryCode com email nao cadastrado retorna erro`() {
        every { UserRepository.emailExists(any()) } returns false
        every { EmailService.isConfigured } returns true
        val result = RecoveryPresenter().sendRecoveryCode("a@b.com", "a@b.com")
        assertNotNull(result)
    }

    @Test fun `sendRecoveryCode com sucesso retorna null`() {
        every { UserRepository.emailExists(any()) } returns true
        every { EmailService.isConfigured } returns true
        every { TokenRepository.saveToken(any(), any()) } returns true
        every { EmailService.sendRecoveryEmail(any(), any()) } returns true
        val result = RecoveryPresenter().sendRecoveryCode("a@b.com", "a@b.com")
        assertNull(result)
    }

    // Verificacao do codigo de recuperacao
    @Test fun `verifyCode em branco retorna erro`() {
        val result = CodeVerificationPresenter().verifyCode("a@b.com", "")
        assertNotNull(result)
    }

    @Test fun `verifyCode com menos de 6 digitos retorna erro`() {
        val result = CodeVerificationPresenter().verifyCode("a@b.com", "123")
        assertNotNull(result)
    }

    @Test fun `verifyCode invalido retorna erro`() {
        every { TokenRepository.validateAndConsumeToken(any(), any()) } returns false
        val result = CodeVerificationPresenter().verifyCode("a@b.com", "123456")
        assertNotNull(result)
    }

    @Test fun `verifyCode valido retorna null`() {
        every { TokenRepository.validateAndConsumeToken(any(), any()) } returns true
        val result = CodeVerificationPresenter().verifyCode("a@b.com", "123456")
        assertNull(result)
    }

    // Redefinicao de senha
    @Test fun `resetPassword com senhas diferentes retorna erro`() {
        val result = NewPasswordPresenter().resetPassword("a@b.com", "Nova@123", "Diferente@1")
        assertNotNull(result)
    }

    @Test fun `resetPassword com senha fraca retorna erro`() {
        val result = NewPasswordPresenter().resetPassword("a@b.com", "fraca", "fraca")
        assertNotNull(result)
    }

    @Test fun `resetPassword com usuario nao encontrado retorna erro`() {
        every { UserRepository.findByEmail(any()) } returns null
        val result = NewPasswordPresenter().resetPassword("a@b.com", "Nova@123", "Nova@123")
        assertNotNull(result)
    }

    @Test fun `resetPassword com sucesso retorna null`() {
        every { UserRepository.findByEmail(any()) } returns User(id = 1, name = "Ana", email = "a@b.com")
        every { UserRepository.update(any(), any(), any(), any()) } returns true
        every { UserRepository.verifyEmail(any()) } returns true
        val result = NewPasswordPresenter().resetPassword("a@b.com", "Nova@123", "Nova@123")
        assertNull(result)
    }

    // Verificacao de email no cadastro
    @Test fun `verifyCode de verificacao em branco retorna erro`() {
        val result = EmailVerificationPresenter().verifyCode("a@b.com", "")
        assertNotNull(result)
    }

    @Test fun `verifyCode de verificacao valido retorna null`() {
        every { TokenRepository.validateAndConsumeToken(any(), any()) } returns true
        every { UserRepository.verifyEmail(any()) } returns true
        val result = EmailVerificationPresenter().verifyCode("a@b.com", "123456")
        assertNull(result)
    }
}
