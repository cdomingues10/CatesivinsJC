package com.christian.catesivinsjc.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.ValoracionsCataRepositoryImpl
import com.christian.catesivinsjc.domain.ValoracionsCataRepository
import com.christian.catesivinsjc.model.Bodega
import com.christian.catesivinsjc.model.Cata
import com.christian.catesivinsjc.model.ValoracionsCata
import com.christian.catesivinsjc.model.Vi
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CataDetailViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Estats per a la UI
    private val _cata = MutableStateFlow<Cata?>(null)
    val cata: StateFlow<Cata?> = _cata

    private val _bodega = MutableStateFlow<Bodega?>(null)
    val bodega: StateFlow<Bodega?> = _bodega

    private val _vins = MutableStateFlow<List<Vi>>(emptyList())
    val vins: StateFlow<List<Vi>> = _vins

    private val valoracionsCataRepository: ValoracionsCataRepository =
        ValoracionsCataRepositoryImpl()

    private val _valoracions = MutableStateFlow<List<ValoracionsCata>>(emptyList())
    val valoracions: StateFlow<List<ValoracionsCata>> = _valoracions

    fun loadCataDetails(cataId: String) {
        viewModelScope.launch {
            // Carrega la llista de valoracions de cata en temps real
            valoracionsCataRepository.obtenirValoracionsPerCata(cataId).collect { llista ->
                _valoracions.value = llista
                try {
                    // 1. Descarregar la cata
                    val cataDoc = firestore.collection("Cates").document(cataId).get().await()
                    val cataObj = cataDoc.toObject(Cata::class.java)
                    _cata.value = cataObj

                    if (cataObj != null) {
                        // 2. Descarregar la bodega
                        if (cataObj.ubicacio != null) {
                            try {
                                val bodegaDoc = cataObj.ubicacio!!.get().await()
                                _bodega.value = bodegaDoc.toObject(Bodega::class.java)
                            } catch (e: Exception) {
                                Log.e("CataDetailViewModel", "Error carregant bodega", e)
                            }
                        }

                        // 3. DESCARREGAR ELS VINS USANT viRef
                        val referencies = cataObj.viRef
                        if (!referencies.isNullOrEmpty()) {
                            try {
                                // Càrrega paral·lela de tots els vins
                                val tasks = referencies.map { it.get() }
                                val documents = Tasks.whenAllSuccess<DocumentSnapshot>(tasks).await()
                                val llistaVins = documents.mapNotNull { it.toObject(Vi::class.java) }

                                _vins.value = llistaVins
                                Log.d("CataDetailViewModel", "Vins carregats via viRef: ${llistaVins.size}")
                            } catch (e: Exception) {
                                Log.e("CataDetailViewModel", "Error carregant vins via viRef", e)
                            }
                        } else {
                            loadVinsByQuery(cataId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CataDetailViewModel", "Error carregant detalls de la cata", e)
                }
            }
        }
    }

    private suspend fun loadVinsByQuery(cataId: String) {
        try {
            val cataRef = firestore.collection("Cates").document(cataId)
            val vinsSnapshot = firestore.collection("Vins")
                .whereEqualTo("cataId", cataRef)
                .get()
                .await()
            _vins.value = vinsSnapshot.toObjects(Vi::class.java)
        } catch (e: Exception) {
            Log.e("CataDetailViewModel", "Error en fallback query", e)
        }
    }

    fun guardarOActualitzarValoracio(puntuacio: Int, comentari: String) {
        val cataId = cata.value?.cataId ?: return
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val novaValoracio = ValoracionsCata(
                    cataId = cataId,
                    userId = userId,
                    puntuacioGeneral = puntuacio,
                    comentariGeneral = comentari,
                    dataValoracio = com.google.firebase.Timestamp.now()
                )
                valoracionsCataRepository.guardarValoracio(novaValoracio)
                Log.d("CataDetailViewModel", "Valoració guardada correctament")
            } catch (e: Exception) {
                Log.e("CataDetailViewModel", "Error al guardar valoració", e)
            }
        }
    }
}
