package com.example.app_bvc

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class StudentProfile(val name: String?, val email: String?)

class FirebaseRepository(private val firestore: FirebaseFirestore) {

    suspend fun getStudentProfile(userId: String): StudentProfile? {
        val document = firestore.collection("students").document(userId).get().await()
        return if (document.exists()) {
            val name = document.getString("name")
            val email = document.getString("email")
            StudentProfile(name, email)
        } else {
            null
        }
    }
}

