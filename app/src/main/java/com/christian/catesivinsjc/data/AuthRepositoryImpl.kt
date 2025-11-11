package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    // Injectem la instància de FirebaseAuth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            // Cridem a la funció de Firebase per crear l'usuari
            auth.createUserWithEmailAndPassword(email, password).await()
            // Si no hi ha hagut cap excepció, l'operació ha estat un èxit
            Result.success(Unit)
        } catch (e: Exception) {
            // Si Firebase llança una excepció, la capturem i la retornem com un error
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // El signOut normalment no falla, però és bona pràctica
            e.printStackTrace()
        }
    }
}
