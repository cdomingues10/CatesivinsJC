package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.AuthRepositoryImpl
import com.christian.catesivinsjc.data.UsuariRepositoryImpl
import com.christian.catesivinsjc.domain.AuthRepository
import com.christian.catesivinsjc.domain.UsuariRepository
import com.christian.catesivinsjc.model.Usuari
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val message: String) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val usuariRepository: UsuariRepository = UsuariRepositoryImpl()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(authRepository.currentUser)
    val currentUser = _currentUser.asStateFlow()

    fun register(email: String, password: String, nom: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, password)

            result.onSuccess {
                val firebaseUser = authRepository.currentUser
                if (firebaseUser != null) {
                    // Creem l'objecte Usuari per guardar-lo a Firestore
                    val nouUsuari = Usuari(
                        userId = firebaseUser.uid,
                        nom = nom,
                        email = email
                    )
                    // Cridem al repositori d'usuaris per crear el document
                    val createUserResult = usuariRepository.createUsuari(nouUsuari)
                    createUserResult.onSuccess {
                        _authState.value = AuthState.Success("Registre completat i perfil creat!")
                        _currentUser.value = firebaseUser
                    }.onFailure {
                        _authState.value = AuthState.Error("Error creant el perfil a la base de dades: ${it.message}")
                    }
                } else {
                    _authState.value = AuthState.Error("Error: no s'ha pogut obtenir l'usuari després del registre.")
                }
            }.onFailure {
                _authState.value = AuthState.Error("Error en el registre: ${it.message}")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success("Sessió iniciada correctament!")
                _currentUser.value = authRepository.currentUser
            }.onFailure {
                _authState.value = AuthState.Error("Error en l'inici de sessió: ${it.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }
}
