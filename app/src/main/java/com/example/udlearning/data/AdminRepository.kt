package com.example.udlearning.data

import com.example.udlearning.data.model.ConfiguracionSistema
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AdminRepository {
    private val db = FirebaseFirestore.getInstance()
    
    fun getConfiguraciones(): Flow<List<ConfiguracionSistema>> = flow {
        try {
            val snapshot = db.collection("configuracion_sistema").get().await()
            emit(snapshot.toObjects(ConfiguracionSistema::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun updateConfiguracion(config: ConfiguracionSistema) {
        if (config.id.isNotEmpty()) {
            db.collection("configuracion_sistema").document(config.id).set(config).await()
        }
    }
}
