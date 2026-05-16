package br.com.mochila.data

import br.com.mochila.model.DEFAULT_SUBJECT_COLOR_RGB
import br.com.mochila.model.Subject
import java.sql.PreparedStatement
import java.sql.ResultSet

object SubjectRepository {

    private fun ResultSet.toSubject() = Subject(
        id = getInt("id_disciplina"),
        name = getString("nome"),
        teacher = getString("professor"),
        minFrequency = getInt("frequencia_minima"),
        startDate = getString("data_inicio"),
        endDate = getString("data_fim"),
        classHours = getInt("hora_aula"),
        semester = getString("semestre"),
        colorRgb = getInt("cor_rgb").let { if (wasNull()) DEFAULT_SUBJECT_COLOR_RGB else it },
    )

    fun insert(
        userId: Int,
        subject: Subject
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                INSERT INTO disciplina 
                (id_usuario, nome, professor, frequencia_minima, data_inicio, data_fim, hora_aula, semestre, cor_rgb)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            stmt.setString(2, subject.name)
            stmt.setString(3, subject.teacher)
            stmt.setInt(4, subject.minFrequency)
            stmt.setString(5, subject.startDate)
            stmt.setString(6, subject.endDate)
            stmt.setInt(7, subject.classHours)
            stmt.setString(8, subject.semester)
            stmt.setInt(9, subject.colorRgb)
            stmt.executeUpdate()
            stmt.close()
            println("✅ Disciplina cadastrada: ${subject.name} para o usuário ID=$userId")
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir disciplina: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun listByUser(userId: Int): List<Subject> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = "SELECT * FROM disciplina WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs: ResultSet = stmt.executeQuery()
            val subjects = mutableListOf<Subject>()
            while (rs.next()) {
                subjects.add(rs.toSubject())
            }
            rs.close()
            stmt.close()
            println("📚 ${subjects.size} disciplinas carregadas para o usuário ID=$userId.")
            subjects
        } catch (e: Exception) {
            println("⚠️ Erro ao listar disciplinas: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    fun update(
        userId: Int,
        subject: Subject
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                UPDATE disciplina
                SET nome = ?, professor = ?, frequencia_minima = ?, data_inicio = ?, data_fim = ?, hora_aula = ?, semestre = ?, cor_rgb = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_disciplina = ? AND id_usuario = ?
            """
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, subject.name)
            stmt.setString(2, subject.teacher)
            stmt.setInt(3, subject.minFrequency)
            stmt.setString(4, subject.startDate)
            stmt.setString(5, subject.endDate)
            stmt.setInt(6, subject.classHours)
            stmt.setString(7, subject.semester)
            stmt.setInt(8, subject.colorRgb)
            stmt.setInt(9, subject.id)
            stmt.setInt(10, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar disciplina: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun delete(userId: Int, subjectId: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "DELETE FROM disciplina WHERE id_disciplina = ? AND id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, subjectId)
            stmt.setInt(2, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            println("🗑️ Disciplina removida ID=$subjectId pelo usuário ID=$userId")
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao deletar disciplina: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun listDistinctSemesters(userId: Int): List<String> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = """
                SELECT DISTINCT semestre FROM disciplina
                WHERE id_usuario = ? AND TRIM(semestre) != ''
                ORDER BY semestre
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val out = mutableListOf<String>()
            while (rs.next()) {
                out.add(rs.getString("semestre"))
            }
            rs.close()
            stmt.close()
            out
        } catch (e: Exception) {
            println("⚠️ Erro ao listar semestres: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    fun findById(subjectId: Int): Subject? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT * FROM disciplina WHERE id_disciplina = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, subjectId)
            val rs: ResultSet = stmt.executeQuery()
            val subject = if (rs.next()) rs.toSubject() else null
            rs.close()
            stmt.close()
            subject
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar disciplina por ID: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }
}