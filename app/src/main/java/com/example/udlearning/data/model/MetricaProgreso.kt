package com.example.udlearning.data.model

data class MetricaProgreso(
    val id: String = "",
    val estudianteId: String = "",
    val asignatura: String = "",
    val promedio: Double = 0.0,
    val actividadesCompletadas: Int = 0,
    val actividadesPendientes: Int = 0,
    val evolucionTemporal: Map<String, Double> = emptyMap(), // Ej: "Semana 1" to 4.0
    val actividadesPorMes: Map<String, Int> = emptyMap() // Ej: "Ene" to 5
)
