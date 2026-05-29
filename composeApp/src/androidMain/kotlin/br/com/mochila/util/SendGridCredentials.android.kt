package br.com.mochila.util

import br.com.mochila.BuildConfig

internal actual fun sendGridApiKey(): String = BuildConfig.SENDGRID_API_KEY
internal actual fun sendGridSenderEmail(): String = BuildConfig.SENDGRID_SENDER_EMAIL
internal actual fun sendGridSenderName(): String = BuildConfig.SENDGRID_SENDER_NAME
