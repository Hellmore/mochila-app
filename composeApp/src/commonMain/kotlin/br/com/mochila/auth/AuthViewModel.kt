package br.com.mochila.auth

import androidx.compose.runtime.mutableStateOf
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // CORREÇÃO: Observar o fluxo de alterações de estado e convertê-lo em um StateFlow
    val currentUser = Firebase.auth.authStateChanged.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000), // Mantém o fluxo ativo por 5s após o último observador sair
        initialValue = Firebase.auth.currentUser // Pega o valor inicial síncrono
    )

    // Fluxo para expor mensagens de erro para a UI
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Estado de carregamento para a UI
    val isLoading = mutableStateOf(false)

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _error.value = "E-mail e senha não podem estar em branco."
                return@launch
            }
            isLoading.value = true
            try {
                Firebase.auth.signInWithEmailAndPassword(email, password)
                // O login bem-sucedido será capturado pelo observer de currentUser
            } catch (e: Exception) {
                _error.value = e.message ?: "Ocorreu um erro desconhecido."
            }
            isLoading.value = false
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _error.value = "E-mail e senha não podem estar em branco."
                return@launch
            }
            isLoading.value = true
            try {
                Firebase.auth.createUserWithEmailAndPassword(email, password)
                // O cadastro bem-sucedido também faz o login
            } catch (e: Exception) {
                _error.value = e.message ?: "Ocorreu um erro desconhecido."
            }
            isLoading.value = false
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _error.value = "O campo de e-mail é obrigatório."
                return@launch
            }
            isLoading.value = true
            try {
                Firebase.auth.sendPasswordResetEmail(email)
                _error.value = "E-mail de recuperação enviado com sucesso!"
            } catch (e: Exception) {
                _error.value = e.message ?: "Ocorreu um erro desconhecido."
            }
            isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                Firebase.auth.signOut()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao tentar sair."
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}