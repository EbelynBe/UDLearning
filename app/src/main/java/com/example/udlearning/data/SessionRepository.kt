package com.example.udlearning.data

import com.example.udlearning.data.model.Activity
import com.example.udlearning.data.model.Session
import com.example.udlearning.data.model.Participant
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class SessionRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createSession(session: Session, activities: List<Activity>, onResult: (Boolean, String?) -> Unit) {
        val documentId = if (session.sessionId.isEmpty()) UUID.randomUUID().toString() else session.sessionId
        val newSession = session.copy(sessionId = documentId)
        
        db.collection("sessions")
            .document(documentId)
            .set(newSession)
            .addOnSuccessListener {
                // If there are activities, save them in the subcollection
                if (activities.isNotEmpty()) {
                    var remaining = activities.size
                    var hasError = false
                    
                    activities.forEach { activity ->
                        val actId = if (activity.activityId.isEmpty()) UUID.randomUUID().toString() else activity.activityId
                        val newActivity = activity.copy(activityId = actId)
                        
                        db.collection("sessions")
                            .document(documentId)
                            .collection("activities")
                            .document(actId)
                            .set(newActivity)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    hasError = true
                                }
                                remaining--
                                if (remaining == 0) {
                                    if (hasError) {
                                        onResult(false, "Sesión creada pero hubo error al guardar algunas actividades.")
                                    } else {
                                        onResult(true, null)
                                    }
                                }
                            }
                    }
                } else {
                    onResult(true, null)
                }
            }
            .addOnFailureListener {
                onResult(false, it.message ?: "Error al guardar la sesión")
            }
    }
    
    fun addParticipantToSession(sessionId: String, participant: Participant, onResult: (Boolean, String?) -> Unit) {
        db.collection("sessions")
            .document(sessionId)
            .collection("participants")
            .document(participant.userId)
            .set(participant)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getStudentSessions(groupId: String, onResult: (List<Session>, String?) -> Unit) {
        db.collection("sessions")
            .whereArrayContains("grupos", groupId)
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.documents.mapNotNull { it.toObject(Session::class.java)?.copy(sessionId = it.id) }
                onResult(sessions, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun getActivitiesForSession(sessionId: String, onResult: (List<Activity>, String?) -> Unit) {
        db.collection("sessions")
            .document(sessionId)
            .collection("activities")
            .get()
            .addOnSuccessListener { result ->
                val activities = result.documents.mapNotNull { it.toObject(Activity::class.java)?.copy(activityId = it.id) }
                onResult(activities, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun getTeacherSessions(teacherId: String, onResult: (List<Session>, String?) -> Unit) {
        db.collection("sessions")
            .whereEqualTo("creadoPor", teacherId)
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.documents.mapNotNull { it.toObject(Session::class.java)?.copy(sessionId = it.id) }
                onResult(sessions, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun getSession(sessionId: String, onResult: (Session?, String?) -> Unit) {
        db.collection("sessions").document(sessionId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val session = document.toObject(Session::class.java)?.copy(sessionId = document.id)
                    onResult(session, null)
                } else {
                    onResult(null, "Sesión no encontrada")
                }
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

    fun updateSessionStatus(sessionId: String, status: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("sessions")
            .document(sessionId)
            .update("estado", status)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { exception -> onResult(false, exception.message) }
    }

    fun updateSessionFull(session: Session, onResult: (Boolean, String?) -> Unit) {
        db.collection("sessions")
            .document(session.sessionId)
            .set(session)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { exception -> onResult(false, exception.message) }
    }

    fun deleteSession(sessionId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("sessions")
            .document(sessionId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { exception -> onResult(false, exception.message) }
    }

    fun getParticipantCount(sessionId: String, onResult: (Int, String?) -> Unit) {
        db.collection("sessions")
            .document(sessionId)
            .collection("participants")
            .get()
            .addOnSuccessListener { result ->
                onResult(result.size(), null)
            }
            .addOnFailureListener { exception ->
                onResult(0, exception.message)
            }
    }
}
