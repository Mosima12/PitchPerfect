package com.pitchperfect.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val etBio = findViewById<EditText>(R.id.etBio)
        val etSkills = findViewById<EditText>(R.id.etSkills)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        // Load existing profile data
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        etBio.setText(document.getString("bio") ?: "")
                        val skills = document.get("skills") as? List<*>
                        etSkills.setText(skills?.joinToString(", ") ?: "")
                    }
                }
        }

        btnSave.setOnClickListener {
            val bio = etBio.text.toString().trim()
            val skillsInput = etSkills.text.toString().trim()
            val skillsList = if (skillsInput.isEmpty()) emptyList() else skillsInput.split(",").map { it.trim() }

            if (userId != null) {
                val updates = hashMapOf<String, Any>(
                    "bio" to bio,
                    "skills" to skillsList
                )
                db.collection("users").document(userId).update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}