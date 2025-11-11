package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.Vi
import kotlinx.coroutines.flow.Flow

interface ViRepository {
    fun obtenirVins(): Flow<List<Vi>>
}
