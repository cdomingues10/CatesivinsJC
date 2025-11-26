package com.christian.catesivinsjc.domain

import com.christian.catesivinsjc.model.Usuari
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    // Registre
    suspend fun register(email: String, password: String): Result<FirebaseUser?>
    
    // Obtenció de l'usuari autenticat actual (de Firebase Auth)
    fun getCurrentUser(): FirebaseUser?
    
    // Obtenció de les dades del perfil (de Firestore - Col·lecció "Usuari")
    suspend fun getUserProfile(uid: String): Result<Usuari?>
    
    // Tancar sessió
    fun logout()
}