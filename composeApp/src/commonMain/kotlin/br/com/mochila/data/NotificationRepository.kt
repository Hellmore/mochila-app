package br.com.mochila.data

import br.com.mochila.model.Notification
import br.com.mochila.model.NotificationType
import java.sql.ResultSet

// Persistencia de notificacoes do usuario
object NotificationRepository {

    // Converte linha do ResultSet em Notification
    private fun ResultSet.toNotification(): Notification? = try {
        val typeStr = getString("tipo")
        val type = NotificationType.entries.find { it.name == typeStr } ?: return null
        Notification(
            id = getInt("id"),
            userId = getInt("id_usuario"),
            type = type,
            message = getString("mensagem"),
            isRead = getInt("lida") == 1,
            referenceId = getObject("referencia_id")?.let { (it as Number).toInt() },
            createdAt = getString("criado_em") ?: "",
        )
    } catch (e: Exception) {
        null
    }

    // Insere notificacao se ainda nao existir para o mesmo tipo e referencia
    fun insertIfNotExists(
        userId: Int,
        type: NotificationType,
        message: String,
        referenceId: Int?,
    ): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val checkSql = if (referenceId != null) {
                "SELECT COUNT(*) FROM notificacao WHERE id_usuario = ? AND tipo = ? AND referencia_id = ?"
            } else {
                "SELECT COUNT(*) FROM notificacao WHERE id_usuario = ? AND tipo = ? AND referencia_id IS NULL"
            }
            val check = conn.prepareStatement(checkSql)
            check.setInt(1, userId)
            check.setString(2, type.name)
            if (referenceId != null) check.setInt(3, referenceId)
            val rs = check.executeQuery()
            val exists = rs.next() && rs.getInt(1) > 0
            rs.close()
            check.close()
            if (exists) return false

            val insertSql =
                "INSERT INTO notificacao (id_usuario, tipo, mensagem, referencia_id) VALUES (?, ?, ?, ?)"
            val stmt = conn.prepareStatement(insertSql)
            stmt.setInt(1, userId)
            stmt.setString(2, type.name)
            stmt.setString(3, message)
            if (referenceId != null) stmt.setInt(4, referenceId)
            else stmt.setNull(4, java.sql.Types.NULL)
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir notificação: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    // Lista notificacoes do usuario (mais recentes primeiro)
    fun listByUser(userId: Int): List<Notification> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql =
                "SELECT * FROM notificacao WHERE id_usuario = ? ORDER BY criado_em DESC"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val result = mutableListOf<Notification>()
            while (rs.next()) rs.toNotification()?.let { result.add(it) }
            rs.close()
            stmt.close()
            result
        } catch (e: Exception) {
            println("⚠️ Erro ao listar notificações: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    // Conta notificacoes nao lidas
    fun countUnread(userId: Int): Int {
        val conn = DatabaseHelper.connect() ?: return 0
        return try {
            val sql = "SELECT COUNT(*) FROM notificacao WHERE id_usuario = ? AND lida = 0"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val count = if (rs.next()) rs.getInt(1) else 0
            rs.close()
            stmt.close()
            count
        } catch (e: Exception) {
            0
        } finally {
            conn.close()
        }
    }

    // Marca todas as notificacoes do usuario como lidas
    fun markAllAsRead(userId: Int): Boolean =
        DatabaseHelper.executeUpdate(
            "UPDATE notificacao SET lida = 1 WHERE id_usuario = ? AND lida = 0",
            listOf(userId),
        )
}
