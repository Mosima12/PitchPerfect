package com.pitchperfect.app.model

data class User(
    val id: String = "",
    val name: String = "",
    val university: String = "",
    val email: String = "",
    val skills: List<String> = emptyList(),
    val bio: String = "",
    val points: Int = 0
)