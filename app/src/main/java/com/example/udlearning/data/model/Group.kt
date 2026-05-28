package com.example.udlearning.data.model

import com.google.firebase.Timestamp

data class Group(
    val groupId: String = "",
    val nombre: String = "",
    val nivel: String = "",
    val semestre: String = "",
    val docenteId: String? = null
)

data class GroupMember(
    val userId: String = "",
    val fechaIngreso: Timestamp = Timestamp.now()
)
