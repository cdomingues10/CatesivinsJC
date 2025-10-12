package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Bodega(
    @DocumentId
    var id: String = "",

    val nom: String = "",
    var descripcio: String = "",
    var telefon: Long = 0,
    var correu: String = "",
    val ubicacio: String = "",
    val geo: GeoPoint? = null,



)
