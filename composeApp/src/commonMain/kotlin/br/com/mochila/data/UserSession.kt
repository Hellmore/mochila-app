package br.com.mochila.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.mochila.model.User

// Estado reativo do usuario autenticado na sessao atual
object UserSession {
    var currentUser: User? by mutableStateOf(null)
        private set

    // Define usuario logado
    fun set(user: User) { currentUser = user }

    // Atualiza caminho da foto no estado da sessao
    fun updatePhoto(path: String?) { currentUser = currentUser?.copy(photoPath = path) }

    // Encerra sessao
    fun clear() { currentUser = null }
}
