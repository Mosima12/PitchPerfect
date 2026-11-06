package com.pitchperfect.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var currentTargetId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFeed = view.findViewById<TextView>(R.id.tvDiscoverFeed)
        val progress = view.findViewById<ProgressBar>(R.id.progressDiscover)
        val btnLike = view.findViewById<Button>(R.id.btnLike)

        progress.visibility = View.VISIBLE
        tvFeed.text = ""

        val userId = auth.currentUser?.uid

        db.collection("users").get()
            .addOnSuccessListener { result ->
                progress.visibility = View.GONE
                val sb = StringBuilder()
                var count = 0
                for (doc in result) {
                    if (doc.id == userId) continue
                    val name = doc.getString("name") ?: "Student"
                    val uni = doc.getString("university") ?: ""
                    val bio = doc.getString("bio") ?: "No bio yet"
                    val skills = doc.get("skills") as? List<*> ?: emptyList<Any>()
                    val score = (65 + (skills.size * 8)).coerceAtMost(96)

                    if (currentTargetId == null) {
                        currentTargetId = doc.id
                    }

                    sb.append("👤  $name\n")
                    sb.append("🎓  $uni\n")
                    sb.append("📝  $bio\n")
                    if (skills.isNotEmpty()) {
                        sb.append("🛠   ${skills.joinToString(", ")}\n")
                    }
                    sb.append("💜  Match Score: $score%\n")
                    sb.append("─────────────────────\n\n")
                    count++
                }
                tvFeed.text = if (count == 0) {
                    "✨ No other students yet.\n\nInvite friends to join PitchPerfect and start collaborating!"
                } else sb.toString()
            }
            .addOnFailureListener {
                progress.visibility = View.GONE
                tvFeed.text = "⚠️ Could not load students. Please try again later."
            }

        // ---------------------------------------------------------
        // POE REQUIREMENT: OFFLINE SYNC LOGIC
        // ---------------------------------------------------------
        btnLike.setOnClickListener {
            val targetId = currentTargetId
            val swiperId = auth.currentUser?.uid
            val context = context ?: return@setOnClickListener

            if (targetId == null || swiperId == null) {
                Toast.makeText(context, "No user selected or not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isOnline = isNetworkAvailable(context)

            // 1. Save the swipe to the local Room Database (Works Offline)
            CoroutineScope(Dispatchers.IO).launch {
                val database = AppDatabase.getDatabase(context)
                val newSwipe = PendingSwipe(
                    swiperId = swiperId,
                    targetId = targetId,
                    action = "like"
                )
                database.swipeDao().insertSwipe(newSwipe)

                // 2. Trigger the background worker to sync to Firebase
                val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
                WorkManager.getInstance(context).enqueue(syncRequest)
            }

            // 3. Provide clear UI feedback for the video
            if (isOnline) {
                Toast.makeText(context, "Liked! Syncing to cloud...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Offline Mode: Liked! Saved locally. Will sync when online.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Helper function to check internet connection
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}