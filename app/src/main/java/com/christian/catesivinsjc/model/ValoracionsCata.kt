package com.christian.catesivinsjc.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude


data class ValoracionsCata(
    @DocumentId
    val valoracioId: String? = null,

// 👈 Referència a la cata avaluada
    val cataId: String = "",

// 👈 Referència a qui avalua
    val userId: String = "",
    @get:Exclude
    var nomUsuari: String = "Usuari desconegut",

// Dades de la valoració de l'esdeveniment
    val puntuacioGeneral: Int = 0,     // Nota (ej. 1-5)
    val comentariGeneral: String = "",
    //data de la valoracio
    val dataValoracio: Timestamp = Timestamp.now(),

)
