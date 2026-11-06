package com.pitchperfect.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface SwipeDao {
    @Insert
    suspend fun insertSwipe(swipe: PendingSwipe)

    @Query("SELECT * FROM pending_swipes")
    suspend fun getAllPendingSwipes(): List<PendingSwipe>

    @Delete
    suspend fun deleteSwipe(swipe: PendingSwipe)

    @Query("DELETE FROM pending_swipes")
    suspend fun clearAll()
}