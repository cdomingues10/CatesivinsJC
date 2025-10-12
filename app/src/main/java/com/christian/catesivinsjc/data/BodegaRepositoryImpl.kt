package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.BodegaRepository
import com.christian.catesivinsjc.model.Bodega
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BodegaRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : BodegaRepository {

    // Nom de la col·lecció a Firestore
    private val colBodegues = "Bodegues"

    // Funció clau que converteix el Listener de Firebase a un Flow de Kotlin
    override fun obtenirBodegues(): Flow<List<Bodega>> = callbackFlow {

        // 1. Creació del Listener de Firebase
        val subscription = firestore.collection(colBodegues)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->

                // 2. Gestió d'errors
                if (error != null) {
                    // Si hi ha un error, l'enviem al Flow i el tanquem.
                    close(error)
                    return@addSnapshotListener
                }

                // 3. Conversió de dades a la nostra llista de Kotlin
                if (snapshot != null) {
                    // map() converteix cada Document en una instància de Bodega
                    val bodegues = snapshot.documents.mapNotNull { document ->
                        // toObject() serialitza el document a la nostra data class Bodega
                        document.toObject(Bodega::class.java)
                    }
                    // Enviem la nova llista al Flow
                    trySend(bodegues)
                }
            }

        // 4. Mantenir el Flow obert
        // Quan el Flow ja no s'estigui utilitzant (per exemple, l'usuari tanca la pantalla),
        // aquesta clau tanca el listener de Firebase per estalviar recursos.
        awaitClose {
            subscription.remove()
        }
    }
}