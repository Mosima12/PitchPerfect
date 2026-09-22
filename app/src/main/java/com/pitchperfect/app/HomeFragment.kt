package com.pitchperfect.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pitchperfect.app.data.RetrofitClient
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val TAG = "HomeFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvStatPoints = view.findViewById<TextView>(R.id.tvStatPoints)
        val tvStatProjects = view.findViewById<TextView>(R.id.tvStatProjects)
        val tvStatMatches = view.findViewById<TextView>(R.id.tvStatMatches)
        val tvStatBadges = view.findViewById<TextView>(R.id.tvStatBadges)
        val tvApiInsight = view.findViewById<TextView>(R.id.tvApiInsight)

        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val points = doc.getLong("points") ?: 0
                tvStatPoints.text = points.toString()
            }

        db.collection("projects").get().addOnSuccessListener { result ->
            tvStatProjects.text = result.size().toString()
        }

        db.collection("users").get().addOnSuccessListener { result ->
            tvStatMatches.text = (result.size() - 1).coerceAtLeast(0).toString()
        }

        tvStatBadges.text = "1"

        // REST API INTEGRATION — fetch live data
        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.api.getPosts()
                Log.d(TAG, "API call successful: ${posts.size} posts")
                val sample = posts.firstOrNull()
                tvApiInsight.text = if (sample != null) {
                    "\"${sample.title.take(80)}...\"\n\n— Live from JSONPlaceholder API"
                } else {
                    "No insights available"
                }
            } catch (e: Exception) {
                Log.e(TAG, "API call failed", e)
                tvApiInsight.text = "Could not load insights. Check your internet connection."
            }
        }
    }
}