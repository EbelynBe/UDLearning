package com.example.udlearning.data.model

import com.google.firebase.Timestamp

data class UserHistory(
    val sessionId: String = "",
    val tema: String = "",
    val puntaje: Int = 0,
    val totalPosible: Int = 0,
    val fecha: Timestamp = Timestamp.now()
)
