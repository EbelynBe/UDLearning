package com.example.udlearning.data

import com.google.firebase.firestore.FirebaseFirestore

class AdminRepository {
    private val db = FirebaseFirestore.getInstance()
    private val configDoc = db.collection("configuracion_sistema").document("parametros")

    fun loadConfiguracion(onResult: (Map<String, Any>?, String?) -> Unit) {
        configDoc.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data, null)
                } else {
                    // Create default config if it doesn't exist
                    val defaults = mapOf(
                        "horarioLimite" to "23:59",
                        "tiempoLimiteEvaluacion" to "60",
                        "alertasActivas" to true,
                        "permisosRol" to "estudiante,docente,administrador"
                    )
                    configDoc.set(defaults)
                        .addOnSuccessListener { onResult(defaults, null) }
                        .addOnFailureListener { onResult(null, it.message) }
                }
            }
            .addOnFailureListener { onResult(null, it.message) }
    }

    fun saveConfiguracion(
        horarioLimite: String,
        tiempoLimiteEvaluacion: String,
        alertasActivas: Boolean,
        permisosRol: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val data = mapOf(
            "horarioLimite" to horarioLimite,
            "tiempoLimiteEvaluacion" to tiempoLimiteEvaluacion,
            "alertasActivas" to alertasActivas,
            "permisosRol" to permisosRol
        )
        configDoc.set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun updateAlertasActivas(enabled: Boolean, onResult: (Boolean, String?) -> Unit) {
        configDoc.update("alertasActivas", enabled)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }
}
