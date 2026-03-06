package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId

data class Usuari(
    @DocumentId
    val userId: String = "",
    val nom: String = "",
    val email: String = "",
    val rol: String = "usuari",
    val fotoUrl: String = ""
)
