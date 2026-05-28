package com.example.udlearning.ui.screens.graficos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udlearning.data.HistorialRepository
import com.example.udlearning.data.model.MetricaProgreso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProgresoViewModel : ViewModel() {
    private val historialRepository = HistorialRepository()

    private val _metricas = MutableStateFlow<List<MetricaProgreso>>(emptyList())
    val metricas: StateFlow<List<MetricaProgreso>> = _metricas

    fun loadMetricas(estudianteId: String) {
        viewModelScope.launch {
            historialRepository.getHistorialEstudiante(estudianteId).collect { historial ->
                if (historial.isEmpty()) {
                    _metricas.value = emptyList()
                    return@collect
                }

                val completadas = historial.filter { it.estado == "Completada" }
                val promedio = if (completadas.isNotEmpty()) {
                    completadas.map { it.calificacion }.average()
                } else 0.0

                // Evolucion temporal based on actual completion dates
                val evolucion = mutableMapOf<String, Double>()
                val sortedCompletadas = completadas.sortedBy { it.fechaRealizacion }
                val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
                
                sortedCompletadas.forEachIndexed { index, act ->
                    val dateLabel = formatter.format(Date(act.fechaRealizacion))
                    // To show a cumulative average or just the score of that session
                    // Let's show the score of the session for the graph
                    evolucion["Sesión ${index + 1} ($dateLabel)"] = act.calificacion
                }

                val calculatedMetrica = MetricaProgreso(
                    id = "dynamic_metrica",
                    estudianteId = estudianteId,
                    promedio = String.format(Locale.US, "%.1f", promedio).toDouble(),
                    actividadesCompletadas = completadas.size,
                    actividadesPendientes = historial.size - completadas.size,
                    evolucionTemporal = evolucion,
                    actividadesPorMes = emptyMap()
                )

                _metricas.value = listOf(calculatedMetrica)
            }
        }
    }
}
