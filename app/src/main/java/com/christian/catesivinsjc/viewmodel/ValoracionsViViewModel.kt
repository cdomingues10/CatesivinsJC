package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.ValoracionsViRepositoryImpl
import com.christian.catesivinsjc.domain.ValoracionsViRepository
import com.christian.catesivinsjc.model.ValoracionsVi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ValoracionsViViewModel(
    private val repository: ValoracionsViRepository = ValoracionsViRepositoryImpl()
): ViewModel() {

    private val _valoracions = MutableStateFlow<List<ValoracionsVi>>(emptyList())
    val valoracions: StateFlow<List<ValoracionsVi>> = _valoracions

    /**
     * Carrega i comença a escoltar les valoracions per a un vi específic.
     */
    fun carregarValoracions(vinoId: String) {
        viewModelScope.launch {
            repository.obtenirValoracionsPerVi(vinoId).collect { valoracionsList ->
                _valoracions.value = valoracionsList
            }
        }
    }
}
