package com.christian.catesivinsjc.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ValoracionsVi(
@DocumentId
val valoracioId: String? = null,

// 👈 Referència al vi avaluat
val vinoId: String = "",

// 👈 Referència a qui avalua
val userId: String = "",

// Dades de la valoració del vi
val puntuacioVi: Int = 0,     // Nota del vi (ej. 1-10)
val notesCata: String = "",    // Notes personals de la cata del vi

// Opcional: camp per saber la data o el lloc on es va tastar
 val dataValoracioVi: Timestamp = Timestamp.now(),
    
    )
