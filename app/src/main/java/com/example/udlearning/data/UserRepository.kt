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
}
