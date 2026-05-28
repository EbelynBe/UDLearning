package com.example.udlearning.util

import com.example.udlearning.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object DatabaseSeeder {

    suspend fun seedDatabase() {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("No hay usuario logueado")

        // 1. Historial Estudiante (usa el UID real)
        val historial1Ref = db.collection("historial_estudiante").document()
        historial1Ref.set(HistorialEstudiante(
            id = historial1Ref.id,
            estudianteId = currentUserId,
            actividadId = "act_1",
            nombreActividad = "Presente simple",
            asignatura = "Inglés I",
            tipoRegistro = "Actividad",
            fechaRealizacion = System.currentTimeMillis(),
            estado = "Completada",
            calificacion = 4.5,
            retroalimentacion = "Buen trabajo."
        )).await()

        val historial2Ref = db.collection("historial_estudiante").document()
        historial2Ref.set(HistorialEstudiante(
            id = historial2Ref.id,
            estudianteId = currentUserId,
            actividadId = "act_2",
            nombreActividad = "Pasado continuo",
            asignatura = "Inglés I",
            tipoRegistro = "Evaluación",
            fechaRealizacion = System.currentTimeMillis() - 86400000,
            estado = "Completada",
            calificacion = 3.8,
            retroalimentacion = "Revisar conjugaciones."
        )).await()

        val historial3Ref = db.collection("historial_estudiante").document()
        historial3Ref.set(HistorialEstudiante(
            id = historial3Ref.id,
            estudianteId = currentUserId,
            actividadId = "act_3",
            nombreActividad = "Phrasal verbs",
            asignatura = "Inglés II",
            tipoRegistro = "Actividad",
            fechaRealizacion = System.currentTimeMillis() - 172800000,
            estado = "Pendiente",
            calificacion = 0.0,
            retroalimentacion = ""
        )).await()

        // 2. Metrica Progreso (usa el UID real)
        val metricaRef = db.collection("metrica_progreso").document()
        metricaRef.set(MetricaProgreso(
            id = metricaRef.id,
            estudianteId = currentUserId,
            asignatura = "Inglés I",
            promedio = 4.1,
            actividadesCompletadas = 12,
            actividadesPendientes = 3,
            evolucionTemporal = mapOf("S1" to 3.0, "S2" to 3.5, "S3" to 4.1),
            actividadesPorMes = mapOf("Ene" to 4, "Feb" to 5, "Mar" to 3)
        )).await()

        // 3. Estadistica Grupal
        val estadisticaRef = db.collection("estadistica_grupal").document()
        estadisticaRef.set(EstadisticaGrupal(
            id = estadisticaRef.id,
            grupoId = "grupo_A",
            nombreGrupo = "Grupo A - Inglés I",
            asignatura = "Inglés I",
            promedioGrupal = 3.9,
            temasMasDificiles = listOf("Presente perfecto", "Phrasal verbs"),
            totalEstudiantes = 24,
            periodoAnalizado = "2026-I",
            distribucionNotas = mapOf("1-2" to 2, "2-3" to 5, "3-4" to 12, "4-5" to 5)
        )).await()

        // 4. Notificaciones (usa el UID real)
        val notificacionRef = db.collection("notificaciones").document()
        notificacionRef.set(Notificacion(
            id = notificacionRef.id,
            destinatarioId = currentUserId,
            tipo = "Nueva Sesion",
            mensaje = "El docente ha publicado una nueva sesión de Inglés I",
            fechaGeneracion = System.currentTimeMillis(),
            estado = "No Leida"
        )).await()

        // 5. Configuracion Sistema
        val configRef = db.collection("configuracion_sistema").document()
        configRef.set(ConfiguracionSistema(
            id = configRef.id,
            parametro = "tiempo_evaluacion",
            valor = "60",
            descripcion = "Tiempo límite por evaluación en minutos",
            rolAplicable = "Todos",
            fechaModificacion = System.currentTimeMillis()
        )).await()
    }
}
