package com.example.udlearning.data

import com.example.udlearning.data.model.HistorialEstudiante
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistorialRepository {
    private val db = FirebaseFirestore.getInstance()
    private val historialCollection = db.collection("historial_estudiante")

    fun getHistorialEstudiante(estudianteId: String): Flow<List<HistorialEstudiante>> = flow {
        try {
            val snapshot = historialCollection
                .whereEqualTo("estudianteId", estudianteId)
                .get()
                .await()
            val historial = snapshot.toObjects(HistorialEstudiante::class.java)
            emit(historial)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun saveHistorial(historial: HistorialEstudiante, onResult: (Boolean) -> Unit) {
        val documentId = if (historial.id.isEmpty()) historialCollection.document().id else historial.id
        val finalHistorial = historial.copy(id = documentId)
        
        historialCollection
            .document(documentId)
            .set(finalHistorial)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    suspend fun getHistorialSnapshot(estudianteId: String): List<HistorialEstudiante> {
        return try {
            val snapshot = historialCollection
                .whereEqualTo("estudianteId", estudianteId)
                .get()
                .await()
            snapshot.toObjects(HistorialEstudiante::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
