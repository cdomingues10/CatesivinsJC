package com.christian.catesivinsjc.data

import android.util.Log
import com.christian.catesivinsjc.domain.CataRepository
import com.christian.catesivinsjc.model.Cata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CataRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CataRepository {

    private val colCates = "Cates"

    override fun obtenirCates(): Flow<List<Cata>> = callbackFlow {

        val subscription = firestore.collection(colCates)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: Throwable? ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Llancem una coroutine per fer la feina "pesada" de buscar els noms
                    launch {
                        val catesList = snapshot.documents.mapNotNull { document ->
                            document.toObject(Cata::class.java)
                        }

                        // Ara recorrem la llista i busquem el nom i l'adreça de la bodega
                        val catesCompletades = catesList.map { cata ->
                            if (cata.ubicacio != null) {
                                try {
                                    // Esperem (await) a tenir la dada de la bodega
                                    val bodegaSnapshot = cata.ubicacio!!.get().await()
                                    
                                    // Recuperem el NOM (comercial) i la UBICACIÓ (adreça)
                                    val nomBodega = bodegaSnapshot.getString("nom") ?: "Nom desconegut"
                                    val direccioBodega = bodegaSnapshot.getString("ubicacio") ?: "Adreça desconeguda"
                                    
                                    // Modifiquem els camps temporals
                                    cata.apply { 
                                        this.nomBodega = nomBodega 
                                        this.direccioBodega = direccioBodega
                                    }
                                } catch (e: Exception) {
                                    Log.e("CataRepository", "Error buscant bodega: ${e.message}")
                                    cata.apply { 
                                        this.nomBodega = "Error" 
                                        this.direccioBodega = ""
                                    }
                                }
                            } else {
                                cata.apply { 
                                    this.nomBodega = "Sense ubicació" 
                                    this.direccioBodega = ""
                                }
                            }
                        }

                        // Enviem la llista JA COMPLETADA cap al ViewModel
                        trySend(catesCompletades)
                    }
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
