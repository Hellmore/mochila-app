package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

data class Materia(
    val id_disciplina: Int,
    val nome: String,
    val frequencia_minima: Int,
    val data_inicio: String,
    val data_fim: String,
    val hora_aula: Int
)

object MateriaRepository {

    fun insertMateria(
        idUsuario: Int,
        nome: String,
        frequenciaMinima: Int,
        dataInicio: String,
        dataFim: String,
        horaAula: Int
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                INSERT INTO disciplina (id_usuario, nome, frequencia_minima, data_inicio, data_fim, hora_aula)
                VALUES (?, ?, ?, ?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setInt(1, idUsuario)
            stmt.setString(2, nome)
            stmt.setInt(3, frequenciaMinima)
            stmt.setString(4, dataInicio)
            stmt.setString(5, dataFim)
            stmt.setInt(6, horaAula)
            stmt.executeUpdate()
            stmt.close()
            println("✅ Disciplina cadastrada: $nome")
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir disciplina: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun listarMaterias(): List<Materia> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        val materias = mutableListOf<Materia>()
        return try {
            val sql = "SELECT * FROM disciplina"
            val stmt = conn.prepareStatement(sql)
            val rs: ResultSet = stmt.executeQuery()

            while (rs.next()) {
                materias.add(
                    Materia(
                        id_disciplina = rs.getInt("id_disciplina"),
                        nome = rs.getString("nome"),
                        frequencia_minima = rs.getInt("frequencia_minima"),
                        data_inicio = rs.getString("data_inicio"),
                        data_fim = rs.getString("data_fim"),
                        hora_aula = rs.getInt("hora_aula")
                    )
                )
            }

            rs.close()
            stmt.close()
            println("📚 ${materias.size} disciplinas carregadas do banco.")
            materias
        } catch (e: Exception) {
            println("⚠️ Erro ao listar disciplinas: ${e.message}")
            emptyList()
        } finally {
            DatabaseHelper.close()
        }
    }

    fun atualizarMateria(
        idDisciplina: Int,
        nome: String,
        frequenciaMinima: Int,
        dataInicio: String,
        dataFim: String,
        horaAula: Int
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                UPDATE disciplina
                SET nome = ?, frequencia_minima = ?, data_inicio = ?, data_fim = ?, hora_aula = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_disciplina = ?
            """
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setInt(2, frequenciaMinima)
            stmt.setString(3, dataInicio)
            stmt.setString(4, dataFim)
            stmt.setInt(5, horaAula)
            stmt.setInt(6, idDisciplina)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar disciplina: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun deletarMateria(idDisciplina: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "DELETE FROM disciplina WHERE id_disciplina = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, idDisciplina)
            val rows = stmt.executeUpdate()
            stmt.close()
            println("🗑️ Disciplina removida ID=$idDisciplina")
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao deletar disciplina: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }
}
