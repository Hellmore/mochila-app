package br.com.mochila.data

import br.com.mochila.model.LogAcao
import br.com.mochila.model.LogErro

// Registro e consulta de logs de acao e erro
object LogRepository {

    // Grava acao do usuario sobre uma tabela
    fun insertAcao(
        userId: Int,
        acao: String,
        tabela: String,
        idRegistro: Int? = null,
        descricao: String? = null,
    ) {
        DatabaseHelper.executeUpdate(
            """INSERT INTO log_acao (id_usuario, acao, descricao, tabela_afetada, id_registro_afetado)
               VALUES (?, ?, ?, ?, ?)""",
            listOf(userId, acao, descricao, tabela, idRegistro),
        )
    }

    // Grava erro de aplicacao, com usuario opcional
    fun insertErro(
        modulo: String,
        mensagem: String,
        userId: Int? = null,
    ) {
        DatabaseHelper.executeUpdate(
            "INSERT INTO log_erro (id_usuario, modulo, mensagem) VALUES (?, ?, ?)",
            listOf(userId, modulo, mensagem),
        )
    }

    // Lista acoes recentes com nome do usuario
    fun listAcoes(limit: Int = 200): List<LogAcao> {
        val rows = DatabaseHelper.executeQuery(
            """SELECT la.id_acao, la.id_usuario, la.acao, la.descricao,
                      la.tabela_afetada, la.id_registro_afetado, la.criado_em,
                      u.nome as nome_usuario
               FROM log_acao la
               LEFT JOIN usuario u ON u.id_usuario = la.id_usuario
               ORDER BY la.criado_em DESC
               LIMIT $limit""",
        )
        return rows.map { row ->
            LogAcao(
                id = (row["id_acao"] as? Number)?.toInt() ?: 0,
                userId = (row["id_usuario"] as? Number)?.toInt() ?: 0,
                userName = row["nome_usuario"] as? String,
                acao = row["acao"] as? String ?: "",
                descricao = row["descricao"] as? String,
                tabelaAfetada = row["tabela_afetada"] as? String ?: "",
                idRegistroAfetado = (row["id_registro_afetado"] as? Number)?.toInt(),
                criadoEm = row["criado_em"] as? String ?: "",
            )
        }
    }

    // Lista erros recentes com nome do usuario
    fun listErros(limit: Int = 200): List<LogErro> {
        val rows = DatabaseHelper.executeQuery(
            """SELECT le.id_erro, le.id_usuario, le.modulo, le.mensagem, le.criado_em,
                      u.nome as nome_usuario
               FROM log_erro le
               LEFT JOIN usuario u ON u.id_usuario = le.id_usuario
               ORDER BY le.criado_em DESC
               LIMIT $limit""",
        )
        return rows.map { row ->
            LogErro(
                id = (row["id_erro"] as? Number)?.toInt() ?: 0,
                userId = (row["id_usuario"] as? Number)?.toInt(),
                userName = row["nome_usuario"] as? String,
                modulo = row["modulo"] as? String ?: "",
                mensagem = row["mensagem"] as? String ?: "",
                criadoEm = row["criado_em"] as? String ?: "",
            )
        }
    }
}
