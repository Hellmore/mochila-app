package br.com.mochila.data

object FaltaRepository {

    fun countByUser(userId: Int): Map<Int, Int> {
        val conn = DatabaseHelper.connect() ?: return emptyMap()
        return try {
            val sql = "SELECT id_disciplina, COUNT(*) as cnt FROM falta WHERE id_usuario = ? GROUP BY id_disciplina"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val result = mutableMapOf<Int, Int>()
            while (rs.next()) {
                result[rs.getInt("id_disciplina")] = rs.getInt("cnt")
            }
            rs.close()
            stmt.close()
            result
        } catch (e: Exception) {
            println("⚠️ Erro ao listar faltas por usuário: ${e.message}")
            emptyMap()
        } finally {
            conn.close()
        }
    }

    fun countBySubject(userId: Int, subjectId: Int): Int {
        val conn = DatabaseHelper.connect() ?: return 0
        return try {
            val sql = "SELECT COUNT(*) as cnt FROM falta WHERE id_usuario = ? AND id_disciplina = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            stmt.setInt(2, subjectId)
            val rs = stmt.executeQuery()
            val count = if (rs.next()) rs.getInt("cnt") else 0
            rs.close()
            stmt.close()
            count
        } catch (e: Exception) {
            println("⚠️ Erro ao contar faltas: ${e.message}")
            0
        } finally {
            conn.close()
        }
    }
}
