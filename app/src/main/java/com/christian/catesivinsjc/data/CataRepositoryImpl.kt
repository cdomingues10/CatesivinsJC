package com.christian.catesivinsjc.data

import android.util.Log
import com.christian.catesivinsjc.domain.CataRepository
import com.christian.catesivinsjc.model.Cata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
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
                    launch {
                        // 1. Convertim documents a objectes Cata
                        val catesList = snapshot.documents.mapNotNull { document ->
                            try {
                                document.toObject(Cata::class.java)?.apply {
                                    cataId = document.id 
                                }
                            } catch (e: Exception) {
                                Log.e("CataRepository", "Error llegint cata ${document.id}: ${e.message}")
                                null
                            }
                        }

                        // 2. Omplim la informació de les Bodegues (inclosa la imatge)
                        val catesCompletades = catesList.map { cata ->
                            if (cata.ubicacio != null) {
                                try {
                                    val bodegaSnapshot = cata.ubicacio!!.get().await()
                                    
                                    val nomBodega = bodegaSnapshot.getString("Nom") ?: "Nom desconegut"
                                    val direccioBodega = bodegaSnapshot.getString("Ubicacio") ?: "Adreça desconeguda"
                                    val geo = bodegaSnapshot.getGeoPoint("Geo") ?: GeoPoint(0.0, 0.0)
                                    val imatgeBodega = bodegaSnapshot.getString("Imatgebod") ?: ""


                                    cata.apply { 
                                        this.nomBodega = nomBodega 
                                        this.direccioBodega = direccioBodega
                                        this.coordenades = geo
                                        this.imatgeBodega = imatgeBodega
                                    }
                                } catch (e: Exception) {
                                    Log.e("CataRepository", "Error buscant bodega: ${e.message}")
                                    cata.apply { 
                                        this.nomBodega = "Error connexió" 
                                        this.direccioBodega = ""
                                        this.coordenades = GeoPoint(0.0, 0.0)
                                    }
                                }
                            } else {
                                cata.apply { 
                                    this.nomBodega = "Sense ubicació" 
                                    this.direccioBodega = ""
                                }
                            }
                        }

                        trySend(catesCompletades)
                    }
                }
            }

        awaitClose {
            subscription.remove()
        }
    }
}
