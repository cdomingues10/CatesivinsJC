package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Vi(
    @DocumentId
    var id: String = "",

    val Any: Long = 0,
    var Descripcio: String = "",
    var Nom: String = "",
    var Tipus: String = "",
    val Varietat: String = "",

    val bodegaId: String = "",
)
