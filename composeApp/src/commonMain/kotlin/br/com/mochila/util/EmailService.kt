package br.com.mochila.util

import java.net.HttpURLConnection
import java.net.URL

// Envio de e-mails via API SendGrid
object EmailService {

    val apiKey: String get() = sendGridApiKey()
    val senderEmail: String get() = sendGridSenderEmail()
    private val senderName: String get() = sendGridSenderName()

    val isConfigured: Boolean get() = apiKey.isNotBlank() && senderEmail.isNotBlank()

    // E-mail de confirmacao de cadastro
    fun sendVerificationEmail(toEmail: String, code: String): Boolean {
        if (!isConfigured) {
            println("⚠️ SendGrid não configurado. Verifique sendgrid.properties.")
            return false
        }
        val html = buildVerificationHtml(code)
        return sendEmail(toEmail, "Confirme seu cadastro - Mochila Hub", html)
    }

    // E-mail de recuperacao de senha
    fun sendRecoveryEmail(toEmail: String, code: String): Boolean {
        if (!isConfigured) {
            println("⚠️ SendGrid não configurado. Crie sendgrid.properties com SENDGRID_API_KEY e SENDGRID_SENDER_EMAIL.")
            return false
        }
        val html = buildRecoveryHtml(code)
        return sendEmail(toEmail, "Recuperação de Senha - Mochila Hub", html)
    }

    private fun buildVerificationHtml(code: String) =
        """<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:24px;"><h2 style="color:#5336CB;">Mochila Hub</h2><p>Obrigado por se cadastrar! Para ativar sua conta, use o código abaixo:</p><div style="background:#f4f1ff;border-radius:8px;padding:24px;text-align:center;margin:24px 0;"><span style="font-size:36px;font-weight:bold;letter-spacing:12px;color:#5336CB;">$code</span></div><p>Este código é válido por <strong>15 minutos</strong>.</p><p>Se você não criou uma conta no Mochila Hub, ignore este e-mail.</p><hr style="border:none;border-top:1px solid #eee;margin:24px 0;"><p style="color:#888;font-size:12px;">Equipe Mochila Hub</p></body></html>"""

    private fun buildRecoveryHtml(code: String) ="""<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:24px;"><h2 style="color:#5336CB;">Mochila Hub</h2><p>Recebemos uma solicitação de recuperação de senha para a sua conta.</p><p>Use o código abaixo para redefinir sua senha:</p><div style="background:#f4f1ff;border-radius:8px;padding:24px;text-align:center;margin:24px 0;"><span style="font-size:36px;font-weight:bold;letter-spacing:12px;color:#5336CB;">$code</span></div><p>Este código é válido por <strong>15 minutos</strong>.</p><p>Se você não solicitou a recuperação de senha, ignore este e-mail.</p><hr style="border:none;border-top:1px solid #eee;margin:24px 0;"><p style="color:#888;font-size:12px;">Equipe Mochila Hub</p></body></html>"""

    private fun sendEmail(toEmail: String, subject: String, htmlContent: String): Boolean {
        return try {
            val url = URL("https://api.sendgrid.com/v3/mail/send")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.doOutput = true

            val body = buildJsonBody(toEmail, subject, htmlContent)
            conn.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }

            val status = conn.responseCode
            if (status !in 200..299) {
                val err = conn.errorStream?.bufferedReader()?.readText() ?: ""
                println("⚠️ SendGrid $status: $err")
            }
            conn.disconnect()
            status in 200..299
        } catch (e: Exception) {
            println("⚠️ Erro ao enviar e-mail: ${e.message}")
            false
        }
    }

    private fun buildJsonBody(toEmail: String, subject: String, htmlContent: String) =
        """{"personalizations":[{"to":[{"email":"${j(toEmail)}"}]}],"from":{"email":"${j(senderEmail)}","name":"${j(senderName)}"},"subject":"${j(subject)}","content":[{"type":"text/html","value":"${j(htmlContent)}"}]}"""

    private fun j(s: String) = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}
