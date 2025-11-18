package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

// Data class para transportar dados do usuário
data class Usuario(val id: Int, val nome: String, val email: String)

object UsuarioRepository {

    /**
     * Cadastra um novo usuário no banco.
     */
    fun insertUsuario(nome: String, email: String, senha: String): Boolean {
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

    /**
     * Valida login do usuário e retorna o ID em caso de sucesso.
     */
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
    
    /**
     * ✅ Busca um usuário completo pelo seu ID.
     */
    fun getUsuarioById(userId: Int): Usuario? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario, nome, email FROM usuario WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()

            val usuario = if (rs.next()) {
                Usuario(
                    id = rs.getInt("id_usuario"),
                    nome = rs.getString("nome"),
                    email = rs.getString("email")
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
    
    /**
     * ✅ Atualiza os dados de um usuário.
     * Se a nova senha for nula ou vazia, ela não é alterada.
     */
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
}
