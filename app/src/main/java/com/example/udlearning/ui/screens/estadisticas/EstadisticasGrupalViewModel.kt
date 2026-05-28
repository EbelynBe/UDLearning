package com.example.udlearning.ui.screens.estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udlearning.data.GroupRepository
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.HistorialRepository
import com.example.udlearning.data.model.EstadisticaGrupal
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class EstadisticasGrupalViewModel : ViewModel() {
    private val groupRepository = GroupRepository()
    private val userRepository = UserRepository()
    private val historialRepository = HistorialRepository()
    
    private val _estadisticas = MutableStateFlow<List<EstadisticaGrupal>>(emptyList())
    val estadisticas: StateFlow<List<EstadisticaGrupal>> = _estadisticas

    fun loadEstadisticas() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            groupRepository.getGroups { groups, _ ->
                val myGroups = groups.filter { it.docenteId == currentUserId }
                
                // Procesar cada grupo de forma asíncrona
                viewModelScope.launch {
                    val stats = myGroups.map { group ->
                        // 1. Obtener estudiantes del grupo (bloqueando de forma segura en corrutina)
                        val students = try {
                            val snapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .whereEqualTo("groupId", group.groupId)
                                .whereEqualTo("rol", "estudiante")
                                .get()
                                .await()
                            snapshot.documents.mapNotNull { it.id }
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val totalEstudiantes = students.size
                        
                        // 2. Obtener historiales de esos estudiantes
                        var totalScore = 0.0
                        var totalActivities = 0
                        
                        for (studentId in students) {
                            val history = historialRepository.getHistorialSnapshot(studentId)
                            val completadas = history.filter { it.estado == "Completada" }
                            if (completadas.isNotEmpty()) {
                                totalScore += completadas.sumOf { it.calificacion }
                                totalActivities += completadas.size
                            }
                        }
                        
                        val promedioGrupal = if (totalActivities > 0) totalScore / totalActivities else 0.0
                        
                        EstadisticaGrupal(
                            id = group.groupId,
                            grupoId = group.groupId,
                            nombreGrupo = "${group.nombre} - Semestre ${group.semestre}",
                            asignatura = group.nivel,
                            promedioGrupal = String.format(Locale.US, "%.1f", promedioGrupal).toDouble(),
                            temasMasDificiles = listOf("Revisión general"), // Podría calcularse buscando la actividad con menor nota
                            totalEstudiantes = totalEstudiantes,
                            periodoAnalizado = "Actual",
                            distribucionNotas = emptyMap()
                        )
                    }
                    _estadisticas.value = stats
                }
            }
        }
    }
}
