package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.Bodega
import kotlinx.coroutines.flow.Flow

interface BodegaRepository {
    // Aquesta funció retornarà un flux continu de llistes de Bodega
    fun obtenirBodegues(): Flow<List<Bodega>>
}

