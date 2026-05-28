package com.example.udlearning.data

import com.example.udlearning.data.model.MetricaProgreso
import com.example.udlearning.data.model.EstadisticaGrupal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MetricasRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getMetricasEstudiante(estudianteId: String): Flow<List<MetricaProgreso>> = flow {
        try {
            val snapshot = db.collection("metrica_progreso")
                .whereEqualTo("estudianteId", estudianteId)
                .get()
                .await()
            emit(snapshot.toObjects(MetricaProgreso::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getEstadisticasDocente(): Flow<List<EstadisticaGrupal>> = flow {
        try {
            val snapshot = db.collection("estadistica_grupal")
                .get()
                .await()
            emit(snapshot.toObjects(EstadisticaGrupal::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
