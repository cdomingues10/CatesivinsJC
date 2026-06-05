package com.christian.catesivinsjc.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.data.CataRepositoryImpl
import com.christian.catesivinsjc.data.ValoracionsCataRepositoryImpl
import com.christian.catesivinsjc.domain.CataRepository
import com.christian.catesivinsjc.domain.ValoracionsCataRepository
import com.christian.catesivinsjc.model.Bodega
import com.christian.catesivinsjc.model.Cata
import com.christian.catesivinsjc.model.Reserva
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
    
    private val cataRepository: CataRepository = CataRepositoryImpl()

    private val _valoracions = MutableStateFlow<List<ValoracionsCata>>(emptyList())
    val valoracions: StateFlow<List<ValoracionsCata>> = _valoracions

    fun loadCataDetails(cataId: String) {
        viewModelScope.launch {
            valoracionsCataRepository.obtenirValoracionsPerCata(cataId).collect { llista ->
                _valoracions.value = llista
                try {
                    val cataDoc = firestore.collection("Cates").document(cataId).get().await()
                    val cataObj = cataDoc.toObject(Cata::class.java)?.apply { 
                        this.cataId = cataDoc.id 
                    }
                    _cata.value = cataObj

                    if (cataObj != null) {
                        if (cataObj.ubicacio != null) {
                            try {
                                val bodegaDoc = cataObj.ubicacio!!.get().await()
                                _bodega.value = bodegaDoc.toObject(Bodega::class.java)
                            } catch (e: Exception) {
                                Log.e("CataDetailViewModel", "Error carregant bodega", e)
                            }
                        }

                        val referencies = cataObj.viRef
                        if (!referencies.isNullOrEmpty()) {
                            try {
                                val tasks = referencies.map { it.get() }
                                val documents = Tasks.whenAllSuccess<DocumentSnapshot>(tasks).await()
                                val llistaVins = documents.mapNotNull { it.toObject(Vi::class.java) }
                                _vins.value = llistaVins
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
            } catch (e: Exception) {
                Log.e("CataDetailViewModel", "Error al guardar valoració", e)
            }
        }
    }

    fun reservarCata(numPersones: Int) {
        val cataActual = cata.value ?: return
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val novaReserva = Reserva(
                    cataId = cataActual.cataId,
                    userId = userId,
                    nomCata = cataActual.nom,
                    dataCata = com.google.firebase.Timestamp(cataActual.data),
                    numPersones = numPersones,
                    estat = "Confirmada"
                )
                cataRepository.ferReserva(novaReserva)
                Log.d("CataDetailViewModel", "Reserva per a $numPersones persones realitzada amb èxit")
            } catch (e: Exception) {
                Log.e("CataDetailViewModel", "Error en realitzar reserva", e)
            }
        }
    }
}
