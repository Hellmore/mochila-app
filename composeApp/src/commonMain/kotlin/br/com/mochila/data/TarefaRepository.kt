package br.com.mochila.data

import java.sql.PreparedStatement
import java.sql.ResultSet

data class Tarefa(
    val id_tarefa: Int,
    val id_usuario: Int,
    val titulo: String,
    val descricao: String,
    val status: String,
    val blockers: String?,
    val data_limite: String?
)

object TarefaRepository {

    fun insertTarefa(
        idUsuario: Int,
        titulo: String,
        descricao: String,
        status: String,
        blockers: String?,
        dataLimite: String?
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                INSERT INTO tarefa 
                (id_usuario, titulo, descricao, status, blockers, data_limite)
                VALUES (?, ?, ?, ?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setInt(1, idUsuario)
            stmt.setString(2, titulo)
            stmt.setString(3, descricao)
            stmt.setString(4, status)
            stmt.setString(5, blockers)
            stmt.setString(6, dataLimite)

            stmt.executeUpdate()
            stmt.close()

            println("✅ Tarefa cadastrada: $titulo (User ID=$idUsuario)")
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir tarefa: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun listarTarefas(idUsuario: Int): List<Tarefa> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        val tarefas = mutableListOf<Tarefa>()

        return try {
            val sql = "SELECT * FROM tarefa WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, idUsuario)

            val rs: ResultSet = stmt.executeQuery()
            while (rs.next()) {
                tarefas.add(
                    Tarefa(
                        id_tarefa = rs.getInt("id_tarefa"),
                        id_usuario = rs.getInt("id_usuario"),
                        titulo = rs.getString("titulo"),
                        descricao = rs.getString("descricao"),
                        status = rs.getString("status"),
                        blockers = rs.getString("blockers"),
                        data_limite = rs.getString("data_limite")
                    )
                )
            }

            rs.close()
            stmt.close()

            println("📌 ${tarefas.size} tarefas carregadas para o usuário ID=$idUsuario")
            tarefas
        } catch (e: Exception) {
            println("⚠️ Erro ao listar tarefas: ${e.message}")
            emptyList()
        } finally {
            DatabaseHelper.close()
        }
    }

    fun atualizarTarefa(
        idUsuario: Int,
        idTarefa: Int,
        titulo: String,
        descricao: String,
        status: String,
        blockers: String?,
        dataLimite: String?
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false

        return try {
            val sql = """
                UPDATE tarefa
                SET titulo = ?, descricao = ?, status = ?, blockers = ?, data_limite = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_tarefa = ? AND id_usuario = ?
            """
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, titulo)
            stmt.setString(2, descricao)
            stmt.setString(3, status)
            stmt.setString(4, blockers)
            stmt.setString(5, dataLimite)
            stmt.setInt(6, idTarefa)
            stmt.setInt(7, idUsuario)

            val rows = stmt.executeUpdate()
            stmt.close()

            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar tarefa: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun deletarTarefa(idUsuario: Int, idTarefa: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false

        return try {
            val sql = "DELETE FROM tarefa WHERE id_tarefa = ? AND id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, idTarefa)
            stmt.setInt(2, idUsuario)

            val rows = stmt.executeUpdate()
            stmt.close()

            println("🗑️ Tarefa removida ID=$idTarefa (User ID=$idUsuario)")
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao deletar tarefa: ${e.message}")
            false
        } finally {
            DatabaseHelper.close()
        }
    }

    fun buscarPorId(idTarefa: Int): Tarefa? {
        val conn = DatabaseHelper.connect() ?: return null

        return try {
            val sql = "SELECT * FROM tarefa WHERE id_tarefa = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, idTarefa)

            val rs: ResultSet = stmt.executeQuery()
            val tarefa = if (rs.next()) {
                Tarefa(
                    id_tarefa = rs.getInt("id_tarefa"),
                    id_usuario = rs.getInt("id_usuario"),
                    titulo = rs.getString("titulo"),
                    descricao = rs.getString("descricao"),
                    status = rs.getString("status"),
                    blockers = rs.getString("blockers"),
                    data_limite = rs.getString("data_limite")
                )
            } else null

            rs.close()
            stmt.close()

            tarefa
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar tarefa por ID: ${e.message}")
            null
        } finally {
            DatabaseHelper.close()
        }
    }
}
