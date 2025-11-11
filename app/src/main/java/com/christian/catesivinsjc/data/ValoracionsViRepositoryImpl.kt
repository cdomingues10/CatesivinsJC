package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.ValoracionsViRepository
import com.christian.catesivinsjc.model.ValoracionsVi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ValoracionsViRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ValoracionsViRepository {

    private val colValoracionsVi = "valoracionsVi"

    override fun obtenirValoracionsPerVi(vinoId: String): Flow<List<ValoracionsVi>> = callbackFlow {

        // Creem una consulta per filtrar les valoracions per l'ID del vi
        val query: Query = firestore.collection(colValoracionsVi)
            .whereEqualTo("vinoId", vinoId)

        val subscription = query.addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val valoracions = snapshot.documents.mapNotNull { document ->
                        document.toObject(ValoracionsVi::class.java)
                    }
                    trySend(valoracions)
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
