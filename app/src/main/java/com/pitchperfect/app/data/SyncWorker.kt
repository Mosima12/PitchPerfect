package com.pitchperfect.app

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Get all pending swipes from Room DB
            val db = AppDatabase.getDatabase(applicationContext)
            val pendingSwipes = db.swipeDao().getAllPendingSwipes()

            if (pendingSwipes.isNotEmpty()) {
                val firestore = FirebaseFirestore.getInstance()

                for (swipe in pendingSwipes) {
                    val swipeData = hashMapOf(
                        "swiperId" to swipe.swiperId,
                        "targetId" to swipe.targetId,
                        "action" to swipe.action,
                        "timestamp" to System.currentTimeMillis()
                    )

                    // 2. Upload to Firebase Firestore
                    firestore.collection("swipes").add(swipeData).await()

                    // 3. Delete from Room DB since it's now synced
                    db.swipeDao().deleteSwipe(swipe)
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}