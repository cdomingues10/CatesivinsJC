package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.ValoracionsCata
import kotlinx.coroutines.flow.Flow

interface ValoracionsCataRepository {
    /**
     * Obté un Flow amb la llista de valoracions per a una cata específica.
     * @param cataId L'identificador de la cata.
     * @return Un Flow que emet la llista de valoracions cada cop que hi ha canvis.
     */
    fun obtenirValoracionsPerCata(cataId: String): Flow<List<ValoracionsCata>>
}
