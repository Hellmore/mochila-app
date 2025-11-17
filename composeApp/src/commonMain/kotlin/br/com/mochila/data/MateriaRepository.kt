package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

data class Materia(
    val id_disciplina: Int,
    val nome: String,
    val professor: String,
    val frequencia_minima: Int,
    val data_inicio: String,
    val data_fim: String,
    val hora_aula: Int,
    val semestre: String
)

object MateriaRepository {

    fun insertMateria(
        idUsuario: Int,
        nome: String,
        professor: String,
        frequenciaMinima: Int,
        dataInicio: String,
        dataFim: String,
        horaAula: Int,
        semestre: String
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                INSERT INTO disciplina 
                (id_usuario, nome, professor, frequencia_minima, data_inicio, data_fim, hora_aula, semestre)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setInt(1, idUsuario)
            stmt.setString(2, nome)
            stmt.setString(3, professor)
            stmt.setInt(4, frequenciaMinima)
            stmt.setString(5, dataInicio)
            stmt.setString(6, dataFim)
            stmt.setInt(7, horaAula)
            stmt.setString(8, semestre)

            stmt.executeUpdate()
            stmt.close()

            println("✅ Disciplina cadastrada: $nome para o usuário ID=$idUsuario")
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir disciplina: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun listarMaterias(idUsuario: Int): List<Materia> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        val materias = mutableListOf<Materia>()

        return try {
            val sql = "SELECT * FROM disciplina WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, idUsuario)

            val rs: ResultSet = stmt.executeQuery()
            while (rs.next()) {
                materias.add(
                    Materia(
                        id_disciplina = rs.getInt("id_disciplina"),
                        nome = rs.getString("nome"),
                        professor = rs.getString("professor"),
                        frequencia_minima = rs.getInt("frequencia_minima"),
                        data_inicio = rs.getString("data_inicio"),
                        data_fim = rs.getString("data_fim"),
                        hora_aula = rs.getInt("hora_aula"),
                        semestre = rs.getString("semestre")   // <-- ADICIONADO
                    )
                )
            }

            rs.close()
            stmt.close()

            println("📚 ${materias.size} disciplinas carregadas para o usuário ID=$idUsuario.")
            materias
        } catch (e: Exception) {
            println("⚠️ Erro ao listar disciplinas: ${e.message}")
            emptyList()
        } finally {
            DatabaseHelper.close()
        }
    }

    fun atualizarMateria(
        idUsuario: Int,
        idDisciplina: Int,
        nome: String,
        professor: String,
        frequenciaMinima: Int,
        dataInicio: String,
        dataFim: String,
        horaAula: Int,
        semestre: String
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false

        return try {
            val sql = """
                UPDATE disciplina
                SET nome = ?, professor = ?, frequencia_minima = ?, data_inicio = ?, data_fim = ?, hora_aula = ?, semestre = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_disciplina = ? AND id_usuario = ?
            """
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, professor)
            stmt.setInt(3, frequenciaMinima)
            stmt.setString(4, dataInicio)
            stmt.setString(5, dataFim)
            stmt.setInt(6, horaAula)
            stmt.setString(7, semestre)    // <-- ADICIONADO
            stmt.setInt(8, idDisciplina)
            stmt.setInt(9, idUsuario)

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

    fun deletarMateria(idUsuario: Int, idDisciplina: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false

        return try {
            val sql = "DELETE FROM disciplina WHERE id_disciplina = ? AND id_usuario = ?"
            val stmt = conn.prepareStatement(sql)

            stmt.setInt(1, idDisciplina)
            stmt.setInt(2, idUsuario)

            val rows = stmt.executeUpdate()
            stmt.close()

            println("🗑️ Disciplina removida ID=$idDisciplina pelo usuário ID=$idUsuario")

            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao deletar disciplina: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun buscarPorId(idDisciplina: Int): Materia? {
        val conn = DatabaseHelper.connect() ?: return null

        return try {
            val sql = "SELECT * FROM disciplina WHERE id_disciplina = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, idDisciplina)

            val rs: ResultSet = stmt.executeQuery()
            val materia = if (rs.next()) {
                Materia(
                    id_disciplina = rs.getInt("id_disciplina"),
                    nome = rs.getString("nome"),
                    professor = rs.getString("professor"),
                    frequencia_minima = rs.getInt("frequencia_minima"),
                    data_inicio = rs.getString("data_inicio"),
                    data_fim = rs.getString("data_fim"),
                    hora_aula = rs.getInt("hora_aula"),
                    semestre = rs.getString("semestre")   // <-- ADICIONADO
                )
            } else null

            rs.close()
            stmt.close()

            materia
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar disciplina por ID: ${e.message}")
            null
        } finally {
            DatabaseHelper.close()
        }
    }
}
