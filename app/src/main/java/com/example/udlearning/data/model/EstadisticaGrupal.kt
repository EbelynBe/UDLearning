package com.example.udlearning.data.model

data class EstadisticaGrupal(
    val id: String = "",
    val grupoId: String = "",
    val nombreGrupo: String = "",
    val asignatura: String = "",
    val promedioGrupal: Double = 0.0,
    val temasMasDificiles: List<String> = emptyList(),
    val totalEstudiantes: Int = 0,
    val periodoAnalizado: String = "",
    val distribucionNotas: Map<String, Int> = emptyMap() // Ej: "3-4" to 10
)
