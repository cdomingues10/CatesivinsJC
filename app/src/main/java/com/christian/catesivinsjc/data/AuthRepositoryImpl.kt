package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.AuthRepository
import com.christian.catesivinsjc.model.Usuari
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun register(email: String, password: String): Result<FirebaseUser?> {
        return try {
            // 1. Creem l'usuari a Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            // 2. Si va bé, guardem a Firestore a la col·lecció "Usuari" (amb majúscula)
            if (user != null) {
                val nouUsuari = Usuari(
                    userId = user.uid,
                    email = email,
                    nom = "", // Deixem buit
                    rol = "usuari", // Valor per defecte del teu model
                    fotoUrl = ""
                )

                // Guardem l'objecte directament. Firestore farà el mapatge automàticament.
                firestore.collection("Usuari").document(user.uid).set(nouUsuari).await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun getUserProfile(uid: String): Result<Usuari?> {
        return try {
            // Llegim de la col·lecció "Usuari"
            val snapshot = firestore.collection("Usuari").document(uid).get().await()
            val usuari = snapshot.toObject(Usuari::class.java)
            Result.success(usuari)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}