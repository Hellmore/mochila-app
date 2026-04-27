package br.com.mochila.data

import br.com.mochila.model.User
import java.sql.PreparedStatement
import java.sql.ResultSet

object UserRepository {

    private fun ResultSet.toUser() = User(
        id = getInt("id_usuario"),
        name = getString("nome"),
        email = getString("email"),
        password = getString("senha")
    )

    fun insert(user: User): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)"
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, user.name)
            stmt.setString(2, user.email)
            stmt.setString(3, user.password)
            stmt.executeUpdate()
            stmt.close()
            println("✅ Usuário cadastrado: ${user.email}")
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir usuário: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun validateLogin(email: String, password: String): Int? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario, senha FROM usuario WHERE email = ?"
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs: ResultSet = stmt.executeQuery()

            var userId: Int? = null
            if (rs.next()) {
                val storedPassword = rs.getString("senha")
                if (storedPassword == password) {
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
            conn.close()
        }
    }

    fun findById(userId: Int): User? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario, nome, email, senha FROM usuario WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val user = if (rs.next()) rs.toUser() else null
            rs.close()
            stmt.close()
            user
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar usuário por ID: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    fun update(userId: Int, name: String, email: String, newPassword: String?): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = if (newPassword.isNullOrBlank()) {
                "UPDATE usuario SET nome = ?, email = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = ?"
            } else {
                "UPDATE usuario SET nome = ?, email = ?, senha = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = ?"
            }
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, name)
            stmt.setString(2, email)
            if (newPassword.isNullOrBlank()) {
                stmt.setInt(3, userId)
            } else {
                stmt.setString(3, newPassword)
                stmt.setInt(4, userId)
            }
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar usuário: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun delete(userId: Int): Boolean {
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
            conn.close()
        }
    }

    fun verifyEmail(email: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val stmt = conn.prepareStatement(
                "UPDATE usuario SET email_verificado = 1 WHERE email = ?"
            )
            stmt.setString(1, email)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao verificar e-mail: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun isEmailVerified(email: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val stmt = conn.prepareStatement(
                "SELECT email_verificado FROM usuario WHERE email = ?"
            )
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            val verified = rs.next() && rs.getInt("email_verificado") == 1
            rs.close()
            stmt.close()
            verified
        } catch (e: Exception) {
            println("⚠️ Erro ao checar verificação: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun findByEmail(email: String): User? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario, nome, email, senha FROM usuario WHERE email = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            val user = if (rs.next()) rs.toUser() else null
            rs.close()
            stmt.close()
            user
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar usuário por email: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    fun emailExists(email: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "SELECT id_usuario FROM usuario WHERE email = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            val exists = rs.next()
            rs.close()
            stmt.close()
            exists
        } catch (e: Exception) {
            println("⚠️ Erro ao verificar email: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }
}