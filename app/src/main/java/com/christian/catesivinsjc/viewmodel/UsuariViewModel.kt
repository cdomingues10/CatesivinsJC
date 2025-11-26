package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.AuthRepositoryImpl
import com.christian.catesivinsjc.data.UsuariRepositoryImpl
import com.christian.catesivinsjc.domain.AuthRepository
import com.christian.catesivinsjc.domain.UsuariRepository
import com.christian.catesivinsjc.model.Usuari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariViewModel(
    private val repository: UsuariRepository = UsuariRepositoryImpl(),
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari = _usuari.asStateFlow()
    
    // Estat per saber si hem fet logout
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    // Carrega automàticament l'usuari que ha fet login
    fun loadCurrentUser() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            // Si tenim usuari de Firebase Auth, busquem les seves dades a Firestore
            getUsuari(currentUser.uid)
        } else {
            // Si no hi ha ningú, potser hauríem de netejar l'estat o avisar
            _usuari.value = null
        }
    }

    fun getUsuari(userId: String) {
        viewModelScope.launch {
            repository.getUsuari(userId).collect {
                _usuari.value = it
            }
        }
    }

    suspend fun createUsuari(usuari: Usuari): Result<Unit> {
        return repository.createUsuari(usuari)
    }
    
    suspend fun updateUsuari(usuari: Usuari): Result<Unit> {
        return repository.updateUsuari(usuari)
    }

    // Funció per tancar sessió
    fun logout() {
        authRepository.logout()
        _usuari.value = null
        _isLoggedOut.value = true
    }
}
