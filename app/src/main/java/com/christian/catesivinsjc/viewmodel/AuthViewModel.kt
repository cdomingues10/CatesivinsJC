package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.AuthRepositoryImpl
import com.christian.catesivinsjc.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    // Estats del formulari
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Estat de l'operació
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // Funcions per actualitzar els camps
    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    // Funció principal de registre
    fun register() {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value.trim()

        // Validacions bàsiques
        if (currentEmail.isEmpty() || currentPassword.isEmpty()) {
            _errorMessage.value = "Omple tots els camps"
            return
        }
        
        if (currentPassword.length < 6) {
            _errorMessage.value = "La contrasenya ha de tenir almenys 6 caràcters"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.register(currentEmail, currentPassword)

            _isLoading.value = false
            
            result.onSuccess {
                _isSuccess.value = true
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconegut durant el registre"
            }
        }
    }
    
    // Reiniciar l'estat d'èxit (per si tornem a la pantalla)
    fun resetState() {
        _isSuccess.value = false
        _errorMessage.value = null
    }
}
