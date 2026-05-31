package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.Cata
import com.christian.catesivinsjc.model.Reserva
import kotlinx.coroutines.flow.Flow

interface CataRepository {
    fun obtenirCates(): Flow<List<Cata>>
    suspend fun ferReserva(reserva: Reserva)
}
