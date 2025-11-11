package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.ValoracionsCataRepositoryImpl
import com.christian.catesivinsjc.domain.ValoracionsCataRepository
import com.christian.catesivinsjc.model.ValoracionsCata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ValoracionsCataViewModel(
    private val repository: ValoracionsCataRepository = ValoracionsCataRepositoryImpl()
): ViewModel() {

    private val _valoracions = MutableStateFlow<List<ValoracionsCata>>(emptyList())
    val valoracions: StateFlow<List<ValoracionsCata>> = _valoracions

    /**
     * Carrega i comença a escoltar les valoracions per a una cata específica.
     */
    fun carregarValoracions(cataId: String) {
        viewModelScope.launch {
            // Cancelem la subscripció anterior (si n'hi ha) i en comencem una de nova
            repository.obtenirValoracionsPerCata(cataId).collect { valoracionsList ->
                _valoracions.value = valoracionsList
            }
        }
    }
}
