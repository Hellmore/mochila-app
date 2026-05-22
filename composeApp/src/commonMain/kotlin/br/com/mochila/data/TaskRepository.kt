package br.com.mochila.data

import br.com.mochila.model.Task
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

object TaskRepository {

    private fun ResultSet.toTask() = Task(
        id = getInt("id_tarefa"),
        userId = getInt("id_usuario"),
        title = getString("titulo"),
        description = getString("descricao"),
        status = getString("status"),
        blockers = getString("blockers"),
        dueDate = getString("data_limite"),
        subjectId = getInt("id_disciplina").takeIf { !wasNull() },
    )

    fun insert(userId: Int, task: Task): Int? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                INSERT INTO tarefa
                (id_usuario, titulo, descricao, status, blockers, data_limite, id_disciplina)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setInt(1, userId)
            stmt.setString(2, task.title)
            stmt.setString(3, task.description)
            stmt.setString(4, task.status)
            stmt.setString(5, task.blockers)
            stmt.setString(6, task.dueDate)
            if (task.subjectId != null) stmt.setInt(7, task.subjectId) else stmt.setNull(7, java.sql.Types.INTEGER)
            val rows = stmt.executeUpdate()
            val newId = if (rows > 0) {
                stmt.generatedKeys.use { keys ->
                    if (keys.next()) keys.getInt(1) else null
                }
            } else {
                null
            }
            stmt.close()
            if (newId != null) {
                println("✅ Tarefa cadastrada: ${task.title} (ID=$newId, User ID=$userId)")
            }
            newId
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir tarefa: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    fun listByUser(userId: Int): List<Task> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = "SELECT * FROM tarefa WHERE id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs: ResultSet = stmt.executeQuery()
            val tasks = mutableListOf<Task>()
            while (rs.next()) {
                tasks.add(rs.toTask())
            }
            rs.close()
            stmt.close()
            println("📌 ${tasks.size} tarefas carregadas para o usuário ID=$userId")
            tasks
        } catch (e: Exception) {
            println("⚠️ Erro ao listar tarefas: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    fun update(userId: Int, task: Task): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                UPDATE tarefa
                SET titulo = ?, descricao = ?, status = ?, blockers = ?, data_limite = ?,
                    id_disciplina = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_tarefa = ? AND id_usuario = ?
            """
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, task.title)
            stmt.setString(2, task.description)
            stmt.setString(3, task.status)
            stmt.setString(4, task.blockers)
            stmt.setString(5, task.dueDate)
            if (task.subjectId != null) stmt.setInt(6, task.subjectId) else stmt.setNull(6, java.sql.Types.INTEGER)
            stmt.setInt(7, task.id)
            stmt.setInt(8, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar tarefa: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun listBySubject(subjectId: Int): List<Task> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = "SELECT * FROM tarefa WHERE id_disciplina = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, subjectId)
            val rs: ResultSet = stmt.executeQuery()
            val tasks = mutableListOf<Task>()
            while (rs.next()) tasks.add(rs.toTask())
            rs.close()
            stmt.close()
            tasks
        } catch (e: Exception) {
            println("⚠️ Erro ao listar tarefas por disciplina: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    fun delete(userId: Int, taskId: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "DELETE FROM tarefa WHERE id_tarefa = ? AND id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, taskId)
            stmt.setInt(2, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            println("🗑️ Tarefa removida ID=$taskId (User ID=$userId)")
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao deletar tarefa: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun findById(taskId: Int): Task? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = "SELECT * FROM tarefa WHERE id_tarefa = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, taskId)
            val rs: ResultSet = stmt.executeQuery()
            val task = if (rs.next()) rs.toTask() else null
            rs.close()
            stmt.close()
            task
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar tarefa por ID: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }
}