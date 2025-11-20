package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val senha: String
)


object UsuarioRepository {

    private fun validarEmail(email: String): Boolean {
        return email.contains("@") && email.length <= 30
    }

    private fun validarNome(nome: String): Boolean {
        return nome.length in 3..30
    }

    private fun validarSenha(senha: String): Boolean {
        val regex = Regex("""^(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,25}$""")
        return regex.matches(senha)
    }

    fun insertUsuario(nome: String, email: String, senha: String): Boolean {

        if (!validarEmail(email)) {
            println("Email inválido: $email")
            return false
        }

        if (!validarNome(nome)) {
            println("Nome inválido: $nome")
            return false
        }

        if (!validarSenha(senha)) {
            println("Senha inválida. Deve ter 8 a 25 caracteres, incluir letra maiúscula, número e caractere especial.")
            return false
        }

        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)"
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, email)
            stmt.setString(3, senha)
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir usuário: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun validarLogin(email: String, senha: String): Int? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario, senha FROM usuario WHERE email = ?"
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs: ResultSet = stmt.executeQuery()

            var userId: Int? = null
            if (rs.next()) {
                val senhaNoBanco = rs.getString("senha")
                if (senhaNoBanco == senha) {
                    userId = rs.getInt("id_usuario")
                }
            }
            rs.close()
            stmt.close()
            userId
        } catch (e: Exception) {
            println("⚠️ Erro ao validar login: ${e.message}")
            null
        } finally {
            DatabaseHelper.close()
        }
    }

    fun getUsuarioById(userId: Int): Usuario? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario, nome, email, senha FROM usuario WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()

            val usuario = if (rs.next()) {
                Usuario(
                    id = rs.getInt("id_usuario"),
                    nome = rs.getString("nome"),
                    email = rs.getString("email"),
                    senha = rs.getString("senha")
                )
            } else null

            rs.close()
            stmt.close()
            usuario
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar usuário por ID: ${e.message}")
            null
        } finally {
            DatabaseHelper.close()
        }
    }

    fun updateUsuario(userId: Int, nome: String, email: String, novaSenha: String?): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            // Constrói o SQL dinamicamente para atualizar a senha apenas se fornecida
            val sql = if (novaSenha.isNullOrBlank()) {
                "UPDATE usuario SET nome = ?, email = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = ?"
            } else {
                "UPDATE usuario SET nome = ?, email = ?, senha = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = ?"
            }
            
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, email)
            if (novaSenha.isNullOrBlank()) {
                stmt.setInt(3, userId)
            } else {
                stmt.setString(3, novaSenha)
                stmt.setInt(4, userId)
            }
            
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar usuário: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun deleteUsuario(userId: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "DELETE FROM usuario WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)

            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao deletar usuário: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun emailExiste(email: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "SELECT id_usuario FROM usuario WHERE email = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs = stmt.executeQuery()

            val existe = rs.next()

            rs.close()
            stmt.close()
            existe
        } catch (e: Exception) {
            println("⚠️ Erro ao verificar email: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

}
