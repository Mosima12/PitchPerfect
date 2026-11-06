package com.pitchperfect.app

import com.google.firebase.firestore.DocumentId

data class Mentor(
    @DocumentId val id: String = "",
    val name: String = "",
    val role: String = "",
    val expertise: String = "",
    val yearsExperience: Int = 0,
    val availability: String = "Available" // "Available" or "Busy"
)