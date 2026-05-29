package br.com.mochila.util

import java.io.File
import java.util.Properties

private val props: Properties by lazy {
    Properties().apply {
        listOf(File("sendgrid.properties"), File("../sendgrid.properties"))
            .firstOrNull { it.exists() }
            ?.inputStream()?.use { load(it) }
    }
}

internal actual fun sendGridApiKey(): String =
    System.getenv("SENDGRID_API_KEY")?.takeIf { it.isNotBlank() }
        ?: props.getProperty("SENDGRID_API_KEY", "")

internal actual fun sendGridSenderEmail(): String =
    System.getenv("SENDGRID_SENDER_EMAIL")?.takeIf { it.isNotBlank() }
        ?: props.getProperty("SENDGRID_SENDER_EMAIL", "")

internal actual fun sendGridSenderName(): String =
    System.getenv("SENDGRID_SENDER_NAME")?.takeIf { it.isNotBlank() }
        ?: props.getProperty("SENDGRID_SENDER_NAME", "Mochila Hub")
