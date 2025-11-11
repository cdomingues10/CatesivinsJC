package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.ValoracionsCataRepository
import com.christian.catesivinsjc.model.ValoracionsCata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ValoracionsCataRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ValoracionsCataRepository {

    private val colValoracionsCata = "valoracionsCata"

    override fun obtenirValoracionsPerCata(cataId: String): Flow<List<ValoracionsCata>> = callbackFlow {

        // Creem una consulta per filtrar les valoracions per l'ID de la cata
        val query: Query = firestore.collection(colValoracionsCata)
            .whereEqualTo("cataId", cataId)

        val subscription = query.addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val valoracions = snapshot.documents.mapNotNull { document ->
                        document.toObject(ValoracionsCata::class.java)
                    }
                    trySend(valoracions)
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
