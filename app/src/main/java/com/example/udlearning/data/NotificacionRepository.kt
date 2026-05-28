package com.example.udlearning.data

import com.example.udlearning.data.model.Notificacion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificacionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificacionesCollection = db.collection("notificaciones")

    fun getNotificaciones(usuarioId: String): Flow<List<Notificacion>> = flow {
        try {
            val snapshot = notificacionesCollection
                .whereEqualTo("destinatarioId", usuarioId)
                .get()
                .await()
            emit(snapshot.toObjects(Notificacion::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
