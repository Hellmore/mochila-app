package br.com.mochila.util

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Hash e verificacao de senha com PBKDF2
object PasswordHash {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    // Gera salt aleatorio e retorna salt:hash em hex
    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password, salt)
        return "${salt.toHex()}:${hash.toHex()}"
    }

    // Compara senha informada com valor armazenado
    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 2) return false
        val salt = parts[0].fromHex()
        val expectedHash = parts[1].fromHex()
        return pbkdf2(password, salt).contentEquals(expectedHash)
    }

    private fun pbkdf2(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

    private fun String.fromHex(): ByteArray {
        val data = ByteArray(length / 2)
        for (i in data.indices) {
            data[i] = ((Character.digit(this[i * 2], 16) shl 4) + Character.digit(this[i * 2 + 1], 16)).toByte()
        }
        return data
    }
}
