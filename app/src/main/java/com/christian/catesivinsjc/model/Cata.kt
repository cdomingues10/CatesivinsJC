package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Cata (
    @DocumentId
    var cataId: String = "",
    var data: Date = Date(),
    var descripcio: String = "",
    var inclou: List<String> = emptyList(),
    var nom: String = "",
    
    // Referència a la base de dades (punter a la Bodega)
    var ubicacio: DocumentReference? = null,

    // ARRAY DE REFERÈNCIES: Llista de vins vinculats directament
    var viRef: List<DocumentReference>? = null,

    // Camps EXCLOSOS de Firestore (només per a la pantalla)
    @get:Exclude 
    var nomBodega: String = "Carregant...",
    
    @get:Exclude
    var direccioBodega: String = "",

    @get:Exclude
    var coordenades: GeoPoint? = null,

    @get:Exclude
    var imatgeBodega: String = ""

)
