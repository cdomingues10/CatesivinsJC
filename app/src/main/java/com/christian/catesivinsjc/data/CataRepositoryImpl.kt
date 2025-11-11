package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.CataRepository
import com.christian.catesivinsjc.model.Cata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CataRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CataRepository {

    // Nom de la col·lecció a Firestore
    private val colCates = "cates"

    override fun obtenirCates(): Flow<List<Cata>> = callbackFlow {

        val subscription = firestore.collection(colCates)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val cates = snapshot.documents.mapNotNull { document ->
                        document.toObject(Cata::class.java)
                    }
                    trySend(cates)
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
