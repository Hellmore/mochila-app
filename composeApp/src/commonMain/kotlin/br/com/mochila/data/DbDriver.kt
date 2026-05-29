package br.com.mochila.data

// Retorna a URL JDBC correta para cada plataforma e registra o driver necessario
internal expect fun jdbcConnectionUrl(dbPath: String): String

// Le o script SQL de inicializacao do banco de cada plataforma
internal expect fun readDbInitScript(): String?
