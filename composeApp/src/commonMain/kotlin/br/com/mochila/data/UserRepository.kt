package br.com.mochila.data

import br.com.mochila.model.User
import br.com.mochila.model.UserSummary
import br.com.mochila.util.PasswordHash
import java.sql.PreparedStatement
import java.sql.ResultSet

// CRUD e autenticacao de usuarios
object UserRepository {

    // Converte linha do ResultSet em User
    private fun ResultSet.toUser() = User(
        id = getInt("id_usuario"),
        name = getString("nome"),
        email = getString("email"),
        password = getString("senha"),
        photoPath = runCatching { getString("foto_perfil") }.getOrNull(),
        isAdmin = runCatching { getInt("is_admin") == 1 }.getOrDefault(false),
    )

    // Cadastra novo usuario com senha hasheada
    fun insert(user: User): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)"
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, user.name)
            stmt.setString(2, user.email)
            stmt.setString(3, PasswordHash.hash(user.password))
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

    // Valida credenciais e retorna id do usuario ou null
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
                if (PasswordHash.verify(password, storedPassword)) {
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

    // Busca usuario completo por id
    fun findById(userId: Int): User? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                SELECT u.id_usuario, u.nome, u.email, u.senha, u.foto_perfil,
                       CASE WHEN a.id_adm IS NOT NULL THEN 1 ELSE 0 END as is_admin
                FROM usuario u
                LEFT JOIN administrador a ON a.id_usuario = u.id_usuario
                WHERE u.id_usuario = ?
            """.trimIndent()
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

    // Atualiza nome, email e opcionalmente senha
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
                stmt.setString(3, PasswordHash.hash(newPassword))
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

    // Remove referencia da foto de perfil
    fun removePhoto(userId: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val stmt = conn.prepareStatement(
                "UPDATE usuario SET foto_perfil = NULL, atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = ?"
            )
            stmt.setInt(1, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao remover foto: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    // Salva caminho da foto de perfil
    fun updatePhoto(userId: Int, photoPath: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val stmt = conn.prepareStatement(
                "UPDATE usuario SET foto_perfil = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id_usuario = ?"
            )
            stmt.setString(1, photoPath)
            stmt.setInt(2, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar foto: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    // Exclui usuario do banco
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

    // Marca e-mail como verificado
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

    // Consulta se e-mail ja foi verificado
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

    // Lista resumo de todos os usuarios (painel admin)
    fun listAll(): List<UserSummary> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = """
                SELECT u.id_usuario, u.nome, u.email, u.email_verificado, u.criado_em,
                       CASE WHEN a.id_adm IS NOT NULL THEN 1 ELSE 0 END as is_admin
                FROM usuario u
                LEFT JOIN administrador a ON a.id_usuario = u.id_usuario
                ORDER BY u.criado_em DESC
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            val rs = stmt.executeQuery()
            val users = mutableListOf<UserSummary>()
            while (rs.next()) {
                users.add(UserSummary(
                    id = rs.getInt("id_usuario"),
                    name = rs.getString("nome") ?: "",
                    email = rs.getString("email") ?: "",
                    isAdmin = rs.getInt("is_admin") == 1,
                    emailVerified = rs.getInt("email_verificado") == 1,
                    createdAt = rs.getString("criado_em") ?: "",
                ))
            }
            rs.close()
            stmt.close()
            users
        } catch (e: Exception) {
            println("⚠️ Erro ao listar usuários: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    // Busca resumo de um usuario por id
    fun findSummaryById(userId: Int): UserSummary? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                SELECT u.id_usuario, u.nome, u.email, u.email_verificado, u.criado_em,
                       CASE WHEN a.id_adm IS NOT NULL THEN 1 ELSE 0 END as is_admin
                FROM usuario u
                LEFT JOIN administrador a ON a.id_usuario = u.id_usuario
                WHERE u.id_usuario = ?
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val user = if (rs.next()) UserSummary(
                id            = rs.getInt("id_usuario"),
                name          = rs.getString("nome") ?: "",
                email         = rs.getString("email") ?: "",
                isAdmin       = rs.getInt("is_admin") == 1,
                emailVerified = rs.getInt("email_verificado") == 1,
                createdAt     = rs.getString("criado_em") ?: "",
            ) else null
            rs.close()
            stmt.close()
            user
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar resumo do usuário: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    // Busca usuario completo por e-mail
    fun findByEmail(email: String): User? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                SELECT u.id_usuario, u.nome, u.email, u.senha, u.foto_perfil,
                       CASE WHEN a.id_adm IS NOT NULL THEN 1 ELSE 0 END as is_admin
                FROM usuario u
                LEFT JOIN administrador a ON a.id_usuario = u.id_usuario
                WHERE u.email = ?
            """.trimIndent()
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

    // Verifica se e-mail ja esta cadastrado
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
