package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import java.util.Date

data class Cata (
    @DocumentId
    var cataId: String = "",
    var data: Date = Date(),
    var descripcio: String = "",
    var nom: String = "",
    
    // Referència a la base de dades (punter a la Bodega)
    var ubicacio: DocumentReference? = null,

    // Referències als vins utilitzats
    val vinsIds: List<String> = emptyList(),

    // Camps EXCLOSOS de Firestore (només per a la pantalla)
    @get:Exclude 
    var nomBodega: String = "Carregant...",
    
    @get:Exclude
    var direccioBodega: String = ""
)
