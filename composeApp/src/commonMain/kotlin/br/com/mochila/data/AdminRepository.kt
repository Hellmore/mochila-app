package br.com.mochila.data

import br.com.mochila.model.AdminUserStats

object AdminRepository {

    const val ADMIN_SECRET_CODE = "mochila@adm2025"

    fun isAdmin(userId: Int): Boolean {
        val rows = DatabaseHelper.executeQuery(
            "SELECT id_adm FROM administrador WHERE id_usuario = ?",
            listOf(userId),
        )
        return rows.isNotEmpty()
    }

    fun promoteToAdmin(userId: Int): Boolean =
        DatabaseHelper.executeUpdate(
            "INSERT OR IGNORE INTO administrador (id_usuario) VALUES (?)",
            listOf(userId),
        )

    fun demoteFromAdmin(userId: Int): Boolean =
        DatabaseHelper.executeUpdate(
            "DELETE FROM administrador WHERE id_usuario = ?",
            listOf(userId),
        )

    fun getUserStats(targetUserId: Int): AdminUserStats {
        val conn = DatabaseHelper.connect() ?: return AdminUserStats(0, 0, 0, 0)
        return try {
            fun countTable(table: String, col: String): Int {
                val stmt = conn.prepareStatement("SELECT COUNT(*) FROM $table WHERE $col = ?")
                stmt.setInt(1, targetUserId)
                val rs = stmt.executeQuery()
                val count = if (rs.next()) rs.getInt(1) else 0
                rs.close()
                stmt.close()
                return count
            }
            AdminUserStats(
                taskCount    = countTable("tarefa",    "id_usuario"),
                subjectCount = countTable("disciplina","id_usuario"),
                faltaCount   = countTable("falta",     "id_usuario"),
                eventCount   = countTable("evento",    "id_usuario"),
            )
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar stats do usuário: ${e.message}")
            AdminUserStats(0, 0, 0, 0)
        } finally {
            conn.close()
        }
    }
}
