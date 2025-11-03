package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

object UsuarioRepository {

    /**
     * Cadastra um novo usuário no banco.
     * Retorna true se inserção for bem-sucedida, false caso contrário.
     */
    fun insertUsuario(nome: String, email: String, senha: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                INSERT INTO usuario (nome, email, senha) 
                VALUES (?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, email)
            stmt.setString(3, senha)
            stmt.executeUpdate()
            stmt.close()
            println("✅ Usuário cadastrado: $email")
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

            if (userId != null) {
                println("✅ Login autorizado para: $email (ID: $userId)")
            } else {
                println("❌ Credenciais inválidas para: $email")
            }
            userId
        } catch (e: Exception) {
            println("⚠️ Erro ao validar login: ${e.message}")
            null
        } finally {
            DatabaseHelper.close()
        }
    }

    /**
     * Busca o ID de um usuário pelo e-mail.
     */
    fun getUsuarioIdPorEmail(email: String): Int? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT id_usuario FROM usuario WHERE email = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            val userId = if (rs.next()) rs.getInt("id_usuario") else null
            rs.close()
            stmt.close()
            userId
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar ID do usuário: ${e.message}")
            null
        } finally {
            DatabaseHelper.close()
        }
    }

    /**
     * Busca um usuário pelo e-mail.
     */
    fun buscarUsuarioPorEmail(email: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "SELECT 1 FROM usuario WHERE email = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            val existe = rs.next()
            rs.close()
            stmt.close()
            existe
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar usuário: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }
}
