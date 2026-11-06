package com.pitchperfect.app

import com.google.firebase.firestore.DocumentId

data class BusinessIdea(
    @DocumentId val id: String = "",
    val creatorId: String = "",
    val title: String = "",
    val problem: String = "",
    val solution: String = "",
    val category: String = "",
    val targetUsers: String = "",
    val requiredSkills: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)