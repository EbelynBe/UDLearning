package com.example.udlearning.data

import com.example.udlearning.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveUser(user: User, onResult: (Boolean, String?) -> Unit) {
        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getUser(userId: String, onResult: (User?, String?) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user, null)
                } else {
                    onResult(null, "El usuario no existe en la base de datos")
                }
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

    fun getTeachers(onResult: (List<User>, String?) -> Unit) {
        db.collection("users")
            .whereIn("rol", listOf("docente", "profesor"))
            .get()
            .addOnSuccessListener { result ->
                val teachers = result.documents.mapNotNull { it.toObject(User::class.java)?.copy(userId = it.id) }
                onResult(teachers, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun getStudentsByGroup(groupId: String, onResult: (List<User>, String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("rol", "estudiante")
            .get()
            .addOnSuccessListener { result ->
                val students = result.documents.mapNotNull { it.toObject(User::class.java)?.copy(userId = it.id) }
                onResult(students, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun getAllUsers(onResult: (List<User>, String?) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java)?.copy(userId = it.id) }
                onResult(users, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun deleteUser(userId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("users")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }
}
