package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.ViRepositoryImpl
import com.christian.catesivinsjc.domain.ViRepository
import com.christian.catesivinsjc.model.Vi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViViewModel(
    private val viRepository: ViRepository = ViRepositoryImpl()
): ViewModel() {

    private val _vins = MutableStateFlow<List<Vi>>(emptyList())
    val vins: StateFlow<List<Vi>> = _vins

    init {
        // Quan el ViewModel es crea, comencem a escoltar les dades del repositori
        viewModelScope.launch {
            viRepository.obtenirVins().collect { vinsList ->
                _vins.value = vinsList
            }
        }
    }
}
