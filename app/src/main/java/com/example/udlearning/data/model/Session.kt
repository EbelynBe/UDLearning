package com.example.udlearning.data.model

import com.google.firebase.Timestamp

data class Session(
    val sessionId: String = "",
    val titulo: String = "",
    val tema: String = "",
    val nivel: String = "",
    val objetivo: String = "",
    val tipoActividad: String = "",
    val estado: String = "activa", // activa | proxima | finalizada | archivada
    val fechaInicio: Timestamp? = null,
    val fechaFin: Timestamp? = null,
    val duracion: Int = 0,
    val creadoPor: String = "",
    val grupos: List<String> = emptyList(),
    val fechaCreacion: Timestamp = Timestamp.now()
)

data class Activity(
    val activityId: String = "",
    val titulo: String = "",
    val tipo: String = "", // quiz | completar | emparejamiento
    val descripcion: String = "",
    val cantidad: Int = 0,
    val orden: Int = 0
)

data class Participant(
    val userId: String = "",
    val groupId: String = "",
    val estado: String = "activo", // activo | completado
    val fechaIngreso: Timestamp = Timestamp.now()
)
