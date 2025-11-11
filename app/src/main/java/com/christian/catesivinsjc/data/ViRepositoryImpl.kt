package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.ViRepository
import com.christian.catesivinsjc.model.Vi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ViRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViRepository {

    // Nom de la col·lecció a Firestore
    private val colVins = "vins"

    override fun obtenirVins(): Flow<List<Vi>> = callbackFlow {

        val subscription = firestore.collection(colVins)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val vins = snapshot.documents.mapNotNull { document ->
                        document.toObject(Vi::class.java)
                    }
                    trySend(vins)
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
