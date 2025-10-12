package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.util.Date

data class Cata (
    @DocumentId
    var cataId: String = "",
    var data: Date,
    var descripcio: String = "",
    var nom: String = "",
    var ubicacio: String = "",

    //referncies als vins utilitzats
    val vinsIds: List<String> = emptyList(),


    )