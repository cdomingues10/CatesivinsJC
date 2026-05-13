package com.christian.catesivinsjc.data

import com.christian.catesivinsjc.domain.ValoracionsCataRepository
import com.christian.catesivinsjc.model.ValoracionsCata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ValoracionsCataRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ValoracionsCataRepository {

    //implementem la funcio de guardarvaloracions
    override suspend fun guardarValoracio(valoracio: ValoracionsCata) {

        //ID combinant cata i usuari
        val idDocument = "${valoracio.userId}_${valoracio.cataId}"
        firestore.collection(colValoracionsCata)
            .document(idDocument)
            .set(valoracio)
            .await()
    }

    private val colValoracionsCata = "ValoracionsCata"

    override fun obtenirValoracionsPerCata(cataId: String): Flow<List<ValoracionsCata>> = callbackFlow {

        // Creem una consulta per filtrar les valoracions per l'ID de la cata
        val query: Query = firestore.collection(colValoracionsCata)
            .whereEqualTo("cataId", cataId)

        val subscription =
            query.addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    launch {
                        val valoracions = snapshot.documents.mapNotNull { document ->
                            document.toObject(ValoracionsCata::class.java)
                        }
                        //Anem a extraure els noms d'usuari
                        val valoracionsCompletades = valoracions.map { valoracio ->
                            try {
                                val usuariSnapshot = firestore.collection("Usuaris")
                                    .document(valoracio.userId).get().await()
                                valoracio.apply {
                                    nomUsuari = usuariSnapshot.getString("nom") ?: "Nom desconegut"
                                }
                            } catch (e: Exception) {
                                valoracio.apply { nomUsuari = "Tastador" }
                            }
                        }

                        trySend(valoracionsCompletades)
                    }
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
