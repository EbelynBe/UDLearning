package com.example.udlearning.data

import com.example.udlearning.data.model.Group
import com.example.udlearning.data.model.GroupMember
import com.google.firebase.firestore.FirebaseFirestore

class GroupRepository {

    private val db = FirebaseFirestore.getInstance()

    fun addGroupMember(groupId: String, member: GroupMember, onResult: (Boolean, String?) -> Unit) {
        db.collection("groups")
            .document(groupId)
            .collection("members")
            .document(member.userId)
            .set(member)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getGroups(onResult: (List<Group>, String?) -> Unit) {
        db.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                val groups = result.documents.mapNotNull { it.toObject(Group::class.java)?.copy(groupId = it.id) }
                onResult(groups, null)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList(), exception.message)
            }
    }

    fun updateGroup(group: Group, onResult: (Boolean, String?) -> Unit) {
        db.collection("groups")
            .document(group.groupId)
            .set(group)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}
