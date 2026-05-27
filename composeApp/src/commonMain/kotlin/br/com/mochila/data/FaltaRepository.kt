package br.com.mochila.data

import br.com.mochila.model.DEFAULT_SUBJECT_COLOR_RGB
import br.com.mochila.model.Falta
import java.sql.ResultSet

// CRUD e consultas de faltas por disciplina
object FaltaRepository {

    // Converte linha do ResultSet em Falta (com dados da disciplina)
    private fun ResultSet.toFalta(): Falta? = try {
        val colorRaw = try { getInt("cor_disciplina").let { if (wasNull()) DEFAULT_SUBJECT_COLOR_RGB else it } }
        catch (_: Exception) { DEFAULT_SUBJECT_COLOR_RGB }
        Falta(
            id = getInt("id_falta"),
            userId = getInt("id_usuario"),
            subjectId = getInt("id_disciplina"),
            subjectName = getString("nome_disciplina"),
            subjectColorRgb = colorRaw,
            date = getString("data_falta") ?: "",
            status = getString("status") ?: "Nao Justificada",
        )
    } catch (e: Exception) {
        println("⚠️ Erro ao mapear falta: ${e.message}")
        null
    }

    // Lista faltas do usuario ordenadas por data
    fun listByUser(userId: Int): List<Falta> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = """
                SELECT f.id_falta, f.id_usuario, f.id_disciplina, f.data_falta, f.status,
                       d.nome AS nome_disciplina,
                       COALESCE(d.cor_rgb, $DEFAULT_SUBJECT_COLOR_RGB) AS cor_disciplina
                FROM falta f
                LEFT JOIN disciplina d ON f.id_disciplina = d.id_disciplina
                WHERE f.id_usuario = ?
                ORDER BY f.data_falta DESC
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val result = mutableListOf<Falta>()
            while (rs.next()) rs.toFalta()?.let { result.add(it) }
            rs.close()
            stmt.close()
            result
        } catch (e: Exception) {
            println("⚠️ Erro ao listar faltas: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    // Busca falta por id
    fun findById(faltaId: Int): Falta? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                SELECT f.id_falta, f.id_usuario, f.id_disciplina, f.data_falta, f.status,
                       d.nome AS nome_disciplina,
                       COALESCE(d.cor_rgb, $DEFAULT_SUBJECT_COLOR_RGB) AS cor_disciplina
                FROM falta f
                LEFT JOIN disciplina d ON f.id_disciplina = d.id_disciplina
                WHERE f.id_falta = ?
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, faltaId)
            val rs = stmt.executeQuery()
            val falta = if (rs.next()) rs.toFalta() else null
            rs.close()
            stmt.close()
            falta
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar falta: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    // Registra nova falta
    fun insert(userId: Int, falta: Falta): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                INSERT INTO falta (id_usuario, id_disciplina, data_falta, status)
                VALUES (?, ?, ?, ?)
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            stmt.setInt(2, falta.subjectId)
            stmt.setString(3, falta.date)
            stmt.setString(4, falta.status)
            stmt.executeUpdate() > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir falta: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    // Atualiza disciplina, data ou status da falta
    fun update(userId: Int, falta: Falta): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                UPDATE falta
                SET id_disciplina = ?, data_falta = ?, status = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_falta = ? AND id_usuario = ?
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, falta.subjectId)
            stmt.setString(2, falta.date)
            stmt.setString(3, falta.status)
            stmt.setInt(4, falta.id)
            stmt.setInt(5, userId)
            stmt.executeUpdate() > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar falta: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    // Remove falta do usuario
    fun delete(userId: Int, faltaId: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "DELETE FROM falta WHERE id_falta = ? AND id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, faltaId)
            stmt.setInt(2, userId)
            stmt.executeUpdate() > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao excluir falta: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    // Conta faltas por disciplina para o usuario
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

    // Conta faltas em uma disciplina especifica
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

    // Converte yyyy-MM-dd para dd/MM/yyyy
    fun formatDateForDisplay(dbDate: String): String {
        val parts = dbDate.split("-")
        if (parts.size != 3) return dbDate
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }

    // Converte dd/MM/yyyy para yyyy-MM-dd
    fun formatDateForDb(displayDate: String): String {
        val parts = displayDate.split("/")
        if (parts.size != 3) return displayDate
        return "${parts[2]}-${parts[1]}-${parts[0]}"
    }
}
