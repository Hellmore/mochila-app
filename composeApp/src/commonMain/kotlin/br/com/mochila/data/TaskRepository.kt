package br.com.mochila.data

import br.com.mochila.model.Task
import br.com.mochila.model.TaskCategory
import br.com.mochila.model.TaskPriority
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

// CRUD de tarefas do usuario
object TaskRepository {

    // Converte linha do ResultSet em Task
    private fun ResultSet.toTask() = Task(
        id = getInt("id_tarefa"),
        userId = getInt("id_usuario"),
        title = getString("titulo"),
        description = getString("descricao"),
        status = getString("status"),
        category = runCatching { getString("categoria_tarefa") }
            .getOrNull()
            ?.let { TaskCategory.fromNameOrNull(it) }
            ?: TaskCategory.default,
        priority = runCatching { getString("prioridade") }
            .getOrNull()
            ?.let { TaskPriority.fromNameOrNull(it) }
            ?: TaskPriority.default,
        blockers = getString("blockers"),
        dueDate = getString("data_limite"),
        subjectId = getInt("id_disciplina").takeIf { !wasNull() },
    )

    // Insere tarefa e retorna id gerado
    fun insert(userId: Int, task: Task): Int? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                INSERT INTO tarefa
                (id_usuario, titulo, descricao, status, categoria_tarefa, prioridade,
                 blockers, data_limite, id_disciplina)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            val stmt: PreparedStatement = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            stmt.setString(2, task.title)
            stmt.setString(3, task.description)
            stmt.setString(4, task.status)
            stmt.setString(5, task.category.name)
            stmt.setString(6, task.priority.name)
            stmt.setString(7, task.blockers)
            stmt.setString(8, task.dueDate)
            if (task.subjectId != null) stmt.setInt(9, task.subjectId) else stmt.setNull(9, java.sql.Types.INTEGER)
            stmt.executeUpdate()
            stmt.close()
            val rs = conn.createStatement().executeQuery("SELECT last_insert_rowid()")
            val newId = if (rs.next()) rs.getInt(1).takeIf { it > 0 } else null
            rs.close()
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

    // Lista todas as tarefas do usuario
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

    // Atualiza tarefa existente
    fun update(userId: Int, task: Task): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                UPDATE tarefa
                SET titulo = ?, descricao = ?, status = ?, categoria_tarefa = ?, prioridade = ?,
                    blockers = ?, data_limite = ?, id_disciplina = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_tarefa = ? AND id_usuario = ?
            """
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, task.title)
            stmt.setString(2, task.description)
            stmt.setString(3, task.status)
            stmt.setString(4, task.category.name)
            stmt.setString(5, task.priority.name)
            stmt.setString(6, task.blockers)
            stmt.setString(7, task.dueDate)
            if (task.subjectId != null) stmt.setInt(8, task.subjectId) else stmt.setNull(8, java.sql.Types.INTEGER)
            stmt.setInt(9, task.id)
            stmt.setInt(10, userId)
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

    // Lista tarefas vinculadas a uma disciplina
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

    // Remove tarefa do usuario
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

    // Busca tarefa por id
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
