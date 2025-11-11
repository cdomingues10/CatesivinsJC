package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.Usuari
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    /**
     * Obté l'usuari actualment autenticat a Firebase.
     */
    val currentUser: FirebaseUser?

    /**
     * Registra un nou usuari amb email i contrasenya.
     * @return Un Result que indica èxit o un error amb una excepció.
     */
    suspend fun register(email: String, password: String): Result<Unit>

    /**
     * Inicia sessió amb email i contrasenya.
     * @return Un Result que indica èxit o un error amb una excepció.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Tanca la sessió de l'usuari actual.
     */
    suspend fun logout()
}
