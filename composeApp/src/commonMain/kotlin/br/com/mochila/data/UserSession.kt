package br.com.mochila.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.mochila.model.User

object UserSession {
    var currentUser: User? by mutableStateOf(null)
        private set

    fun set(user: User) { currentUser = user }
    fun updatePhoto(path: String?) { currentUser = currentUser?.copy(photoPath = path) }
    fun clear() { currentUser = null }
}
