package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Vi(
    @DocumentId
    var id: String = "",

    val Any: Long = 0,
    var Descripcio: String = "",
    var Nom: String = "",
    var Tipus: String = "",
    
    // NOU CANVI: Array de Strings per a les varietats
    val Varietat: List<String> = emptyList(),

    // Si la bodega també és una referència, hauria de ser DocumentReference?
    // De moment ho deixem com ho tenies si no t'ha donat error, o ho canviem a String si és només text.
    val bodegaId: String = "", 
    
    // NOU CANVI: Ara és una Referència de Firestore, no un simple text
    val cataId: DocumentReference? = null,

    val Imatgeurl: String = "",
)
