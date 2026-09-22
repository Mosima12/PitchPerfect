package com.pitchperfect.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DiscoverFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvFeed = view.findViewById<TextView>(R.id.tvDiscoverFeed)
        val progress = view.findViewById<ProgressBar>(R.id.progressDiscover)

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
                tvFeed.text = "⚠️ Could not load students. Pull to refresh."
            }
    }
}