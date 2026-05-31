package com.christian.catesivinsjc.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Reserva(
    @DocumentId
    val reservaId: String? = null,
    val cataId: String = "",
    val userId: String = "",
    val nomCata: String = "",
    val dataCata: Timestamp? = null,
    val dataReserva: Timestamp = Timestamp.now(),
    val estat: String = "Confirmada" // Per a un projecte, millor que estiga confirmada directament
)
