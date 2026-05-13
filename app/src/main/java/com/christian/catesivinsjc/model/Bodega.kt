package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Bodega(
    @DocumentId
    var id: String = "",

    val Nom: String = "",
    var Descripcio: String = "",
    var Telefon: Long = 0,
    var Correu: String = "",
    val Ubicacio: String = "",
    val Geo: GeoPoint? = null,
    val Imatgebod: String = ""
)
