package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.Usuari
import kotlinx.coroutines.flow.Flow

interface UsuariRepository {
    /**
     * Obté la informació d'un usuari de Firestore a través del seu ID.
     * @return Un Flow que emet l'objecte Usuari o null si no existeix.
     */
    fun getUsuari(userId: String): Flow<Usuari?>

    /**
     * Crea un nou document d'usuari a Firestore.
     * @param usuari L'objecte Usuari a guardar.
     * @return Un Result que indica èxit o fracàs.
     */
    suspend fun createUsuari(usuari: Usuari): Result<Unit>

    /**
     * Actualitza un document d'usuari existent a Firestore.
     * @param usuari L'objecte Usuari amb les dades actualitzades.
     * @return Un Result que indica èxit o fracàs.
     */
    suspend fun updateUsuari(usuari: Usuari): Result<Unit>
}
