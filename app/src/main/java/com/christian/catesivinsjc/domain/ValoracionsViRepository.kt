package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.ValoracionsVi
import kotlinx.coroutines.flow.Flow

interface ValoracionsViRepository {
    /**
     * Obté un Flow amb la llista de valoracions per a un vi específic.
     * @param vinoId L'identificador del vi.
     * @return Un Flow que emet la llista de valoracions cada cop que hi ha canvis.
     */
    fun obtenirValoracionsPerVi(vinoId: String): Flow<List<ValoracionsVi>>
}
