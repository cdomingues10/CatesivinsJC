package com.christian.catesivinsjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.christian.catesivinsjc.model.Bodega
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BodegaViewModel: ViewModel() {
    //obtenim una instancia de la base de dades
    private val db = Firebase.firestore
    //Creem stackeflow per a guardar llista de bodegues
    //es privat perque només lutilitzarem al viewmodel
    private val _bodegues = MutableStateFlow<List<Bodega>>(emptyList())

    val bodegues: StateFlow<List<Bodega>> = _bodegues

    fun getBodegues(){
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("bodegues").get().await()
                val bodeguesllista = querySnapshot.toObjects(Bodega::class.java)
                _bodegues.value = bodeguesllista
                } catch (e: Exception) {
                // Manejar errors
                println("Error obtenint les bodegues $e")
            }
        }
    }
}