package com.christian.catesivinsjc.model

import com.google.firebase.firestore.DocumentId

data class Usuari(
    @DocumentId
    val userId: String = "",
    val nom: String = "",
    val email: String = "",
    //Pensant en un futur creem el rol, per si implementem mes rols a la app
    val rol: String = "usuari", // pot ser "usuari", "admin", "celler", etc.
    val fotoUrl: String = ""
)
