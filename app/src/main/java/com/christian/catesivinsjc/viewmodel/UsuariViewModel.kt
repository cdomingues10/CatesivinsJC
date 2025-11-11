package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.UsuariRepositoryImpl
import com.christian.catesivinsjc.domain.UsuariRepository
import com.christian.catesivinsjc.model.Usuari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariViewModel(
    private val repository: UsuariRepository = UsuariRepositoryImpl()
) : ViewModel() {

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari = _usuari.asStateFlow()

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
}
