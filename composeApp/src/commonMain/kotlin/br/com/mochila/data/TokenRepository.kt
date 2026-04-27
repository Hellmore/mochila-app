package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

object TokenRepository {

    fun saveToken(email: String, token: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "INSERT INTO token_recuperacao (email, token, expira_em) VALUES (?, ?, datetime(CURRENT_TIMESTAMP, '+15 minutes'))"
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setString(1, email)
            stmt.setString(2, token)
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao salvar token: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun validateAndConsumeToken(email: String, token: String): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val selectSql = """
                SELECT id FROM token_recuperacao
                WHERE email = ? AND token = ? AND usado = 0
                AND expira_em > CURRENT_TIMESTAMP
                ORDER BY criado_em DESC LIMIT 1
            """.trimIndent()
            val stmt: PreparedStatement = conn.prepareStatement(selectSql)
            stmt.setString(1, email)
            stmt.setString(2, token)
            val rs: ResultSet = stmt.executeQuery()
            val valid = rs.next()
            val id = if (valid) rs.getInt("id") else -1
            rs.close()
            stmt.close()

            if (valid) {
                val updateStmt = conn.prepareStatement("UPDATE token_recuperacao SET usado = 1 WHERE id = ?")
                updateStmt.setInt(1, id)
                updateStmt.executeUpdate()
                updateStmt.close()
            }
            valid
        } catch (e: Exception) {
            println("⚠️ Erro ao validar token: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }
}
