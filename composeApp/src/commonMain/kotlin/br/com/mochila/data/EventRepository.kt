package br.com.mochila.data

import br.com.mochila.model.DEFAULT_EVENT_COLOR_RGB
import br.com.mochila.model.Event
import kotlinx.datetime.LocalDateTime
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

object EventRepository {

    private fun ResultSet.toEvent(): Event? = try {
        val subjectIdRaw = findColumn("id_disciplina").let { col ->
            if (col > 0) getInt(col).let { if (wasNull()) null else it } else null
        }
        val subjectNameRaw = findColumn("nome_disciplina").let { col ->
            if (col > 0) getString(col).let { if (wasNull()) null else it } else null
        }
        val colorRaw = findColumn("cor_rgb").let { col ->
            if (col > 0) getInt(col).let { if (wasNull()) DEFAULT_EVENT_COLOR_RGB else it }
            else DEFAULT_EVENT_COLOR_RGB
        }
        val reminderRaw = findColumn("lembrete_minutos").let { col ->
            if (col > 0) getInt(col).let { if (wasNull()) null else it } else null
        }
        val reminderShownRaw = findColumn("lembrete_exibido").let { col ->
            if (col > 0) getInt(col) == 1 else false
        }
        Event(
            id = getInt("id_evento"),
            userId = getInt("id_usuario"),
            title = getString("titulo"),
            description = getString("descricao"),
            eventDate = getString("data_evento"),
            status = getString("status"),
            subjectId = subjectIdRaw,
            subjectName = subjectNameRaw,
            colorRgb = colorRaw,
            reminderMinutes = reminderRaw,
            reminderShown = reminderShownRaw,
        )
    } catch (e: Exception) {
        println("⚠️ Erro ao mapear evento: ${e.message}")
        null
    }

    fun insert(userId: Int, event: Event): Int? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                INSERT INTO evento
                (id_usuario, titulo, descricao, data_evento, status, id_disciplina, cor_rgb, lembrete_minutos, lembrete_exibido)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
            """.trimIndent()
            val stmt: PreparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setInt(1, userId)
            stmt.setString(2, event.title)
            stmt.setString(3, event.description)
            stmt.setString(4, event.eventDate)
            stmt.setString(5, event.status)
            if (event.subjectId != null) stmt.setInt(6, event.subjectId) else stmt.setNull(6, java.sql.Types.INTEGER)
            stmt.setInt(7, event.colorRgb)
            if (event.reminderMinutes != null) stmt.setInt(8, event.reminderMinutes) else stmt.setNull(8, java.sql.Types.INTEGER)
            val rows = stmt.executeUpdate()
            val newId = if (rows > 0) {
                stmt.generatedKeys.use { keys ->
                    if (keys.next()) keys.getInt(1) else null
                }
            } else {
                null
            }
            stmt.close()
            newId
        } catch (e: Exception) {
            println("⚠️ Erro ao inserir evento: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    fun listByUser(userId: Int): List<Event> {
        val conn = DatabaseHelper.connect() ?: return emptyList()
        return try {
            val sql = """
                SELECT e.*, d.nome AS nome_disciplina
                FROM evento e
                LEFT JOIN disciplina d ON e.id_disciplina = d.id_disciplina
                WHERE e.id_usuario = ?
                ORDER BY e.data_evento ASC
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, userId)
            val rs = stmt.executeQuery()
            val events = mutableListOf<Event>()
            while (rs.next()) rs.toEvent()?.let { events.add(it) }
            rs.close()
            stmt.close()
            events
        } catch (e: Exception) {
            println("⚠️ Erro ao listar eventos: ${e.message}")
            emptyList()
        } finally {
            conn.close()
        }
    }

    fun update(userId: Int, event: Event): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = """
                UPDATE evento
                SET titulo = ?, descricao = ?, data_evento = ?, status = ?,
                    id_disciplina = ?, cor_rgb = ?, lembrete_minutos = ?,
                    lembrete_exibido = ?, atualizado_em = CURRENT_TIMESTAMP
                WHERE id_evento = ? AND id_usuario = ?
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setString(1, event.title)
            stmt.setString(2, event.description)
            stmt.setString(3, event.eventDate)
            stmt.setString(4, event.status)
            if (event.subjectId != null) stmt.setInt(5, event.subjectId) else stmt.setNull(5, java.sql.Types.INTEGER)
            stmt.setInt(6, event.colorRgb)
            if (event.reminderMinutes != null) stmt.setInt(7, event.reminderMinutes) else stmt.setNull(7, java.sql.Types.INTEGER)
            stmt.setInt(8, if (event.reminderShown) 1 else 0)
            stmt.setInt(9, event.id)
            stmt.setInt(10, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao atualizar evento: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun delete(userId: Int, eventId: Int): Boolean {
        val conn = DatabaseHelper.connect() ?: return false
        return try {
            val sql = "DELETE FROM evento WHERE id_evento = ? AND id_usuario = ?"
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, eventId)
            stmt.setInt(2, userId)
            val rows = stmt.executeUpdate()
            stmt.close()
            rows > 0
        } catch (e: Exception) {
            println("⚠️ Erro ao excluir evento: ${e.message}")
            false
        } finally {
            conn.close()
        }
    }

    fun findById(eventId: Int): Event? {
        val conn = DatabaseHelper.connect() ?: return null
        return try {
            val sql = """
                SELECT e.*, d.nome AS nome_disciplina
                FROM evento e
                LEFT JOIN disciplina d ON e.id_disciplina = d.id_disciplina
                WHERE e.id_evento = ?
            """.trimIndent()
            val stmt = conn.prepareStatement(sql)
            stmt.setInt(1, eventId)
            val rs = stmt.executeQuery()
            val event = if (rs.next()) rs.toEvent() else null
            rs.close()
            stmt.close()
            event
        } catch (e: Exception) {
            println("⚠️ Erro ao buscar evento: ${e.message}")
            null
        } finally {
            conn.close()
        }
    }

    fun findDueReminder(userId: Int, now: LocalDateTime): Event? {
        val nowEpoch = now.date.toEpochDays() * 24 * 60 + now.hour * 60 + now.minute
        return listByUser(userId).firstOrNull { event ->
            val minutes = event.reminderMinutes ?: return@firstOrNull false
            if (minutes <= 0 || event.reminderShown) return@firstOrNull false
            val eventDt = parseEventDateTime(event.eventDate) ?: return@firstOrNull false
            val eventEpoch = eventDt.date.toEpochDays() * 24 * 60 + eventDt.hour * 60 + eventDt.minute
            val triggerEpoch = eventEpoch - minutes
            nowEpoch >= triggerEpoch && nowEpoch < eventEpoch
        }
    }

    fun markReminderShown(eventId: Int) {
        val conn = DatabaseHelper.connect() ?: return
        try {
            val stmt = conn.prepareStatement(
                "UPDATE evento SET lembrete_exibido = 1 WHERE id_evento = ?"
            )
            stmt.setInt(1, eventId)
            stmt.executeUpdate()
            stmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
    }

    fun parseEventDateTime(raw: String): LocalDateTime? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return try {
            when {
                trimmed.contains("T") -> {
                    val parts = trimmed.split("T")
                    val date = parts[0].split("-")
                    val time = parts.getOrNull(1)?.take(8)?.split(":") ?: listOf("0", "0")
                    LocalDateTime(
                        date[0].toInt(), date[1].toInt(), date[2].toInt(),
                        time[0].toInt(), time.getOrNull(1)?.toInt() ?: 0,
                    )
                }
                trimmed.contains("-") && trimmed.length >= 10 -> {
                    val datePart = trimmed.substring(0, 10)
                    val timePart = trimmed.drop(10).trim().removePrefix(" ").removePrefix("T")
                    val date = datePart.split("-")
                    val time = if (timePart.isNotEmpty()) timePart.split(":") else listOf("0", "0", "0")
                    LocalDateTime(
                        date[0].toInt(), date[1].toInt(), date[2].toInt(),
                        time[0].toInt(), time.getOrNull(1)?.toInt() ?: 0,
                    )
                }
                Regex("""\d{2}/\d{2}/\d{4}""").containsMatchIn(trimmed) -> {
                    val datePart = Regex("""\d{2}/\d{2}/\d{4}""").find(trimmed)!!.value
                    val p = datePart.split("/")
                    LocalDateTime(p[2].toInt(), p[1].toInt(), p[0].toInt(), 0, 0)
                }
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun formatEventDateForDb(displayDate: String): String {
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(displayDate)) return displayDate
        val p = displayDate.split("/")
        return "${p[2]}-${p[1]}-${p[0]} 00:00:00"
    }

    fun formatEventDateForDisplay(dbDate: String): String {
        val dt = parseEventDateTime(dbDate) ?: return dbDate
        val dd = dt.dayOfMonth.toString().padStart(2, '0')
        val mm = dt.monthNumber.toString().padStart(2, '0')
        return "$dd/$mm/${dt.year}"
    }

    fun eventMonthNumber(dbDate: String): Int? = parseEventDateTime(dbDate)?.monthNumber
}
