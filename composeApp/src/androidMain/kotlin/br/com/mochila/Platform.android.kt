package br.com.mochila

import android.os.Build

// Identificacao da plataforma Android
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()