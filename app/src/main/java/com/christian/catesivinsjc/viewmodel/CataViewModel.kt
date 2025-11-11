package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.CataRepositoryImpl
import com.christian.catesivinsjc.domain.CataRepository
import com.christian.catesivinsjc.model.Cata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CataViewModel(
    private val cataRepository: CataRepository = CataRepositoryImpl()
): ViewModel() {

    private val _cates = MutableStateFlow<List<Cata>>(emptyList())
    val cates: StateFlow<List<Cata>> = _cates

    init {
        // Quan el ViewModel es crea, comencem a escoltar les dades del repositori
        viewModelScope.launch {
            cataRepository.obtenirCates().collect { catesList ->
                _cates.value = catesList
            }
        }
    }
}
