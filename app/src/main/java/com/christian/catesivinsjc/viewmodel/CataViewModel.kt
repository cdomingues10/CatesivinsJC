package com.christian.catesivinsjc.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.CataRepositoryImpl
import com.christian.catesivinsjc.domain.CataRepository
import com.christian.catesivinsjc.model.Cata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CataViewModel(
    private val cataRepository: CataRepository = CataRepositoryImpl()
) : ViewModel() {

    private val _cates = MutableStateFlow<List<Cata>>(emptyList())
    val cates: StateFlow<List<Cata>> = _cates.asStateFlow()

    init {
        Log.d("CataViewModel", "Inicialitzant ViewModel i carregant cates...")
        getCates()
    }

    fun getCates() {
        viewModelScope.launch {
            try {
                // Ara el repositori ja ens retorna les cates amb el nomBodega emplenat!
                cataRepository.obtenirCates().collect { catesList ->
                    Log.d("CataViewModel", "Dades rebudes i processades! Nombre de cates: ${catesList.size}")
                    _cates.value = catesList
                }
            } catch (e: Exception) {
                Log.e("CataViewModel", "Error carregant cates", e)
            }
        }
    }
}
