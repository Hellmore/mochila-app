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
                INSERT INTO usuario (nome, email) 
                VALUES (?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, email)
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
     * Valida login do usuário verificando e-mail e senha.
     */
    fun validarLogin(email: String, senha: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                SELECT * FROM usuario WHERE email = ?
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, email)
            val rs: ResultSet = stmt.executeQuery()
            val existe = rs.next()
            rs.close()
            stmt.close()

            if (existe) println("✅ Login autorizado para: $email")
            else println("❌ Credenciais inválidas.")

            existe
        } catch (e: Exception) {
            println("⚠️ Erro ao validar login: ${e.message}")
            false
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
