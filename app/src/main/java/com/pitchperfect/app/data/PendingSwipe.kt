package com.pitchperfect.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_swipes")
data class PendingSwipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val swiperId: String,
    val targetId: String,
    val action: String // "like" or "pass"
)