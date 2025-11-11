package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.UsuariRepository
import com.christian.catesivinsjc.model.Usuari
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UsuariRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UsuariRepository {

    private val colUsuaris = "usuaris"

    override fun getUsuari(userId: String): Flow<Usuari?> = callbackFlow {
        val docRef = firestore.collection(colUsuaris).document(userId)

        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(Usuari::class.java))
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun createUsuari(usuari: Usuari): Result<Unit> {
        return try {
            firestore.collection(colUsuaris).document(usuari.userId).set(usuari).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUsuari(usuari: Usuari): Result<Unit> {
        return try {
            firestore.collection(colUsuaris).document(usuari.userId).set(usuari).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
