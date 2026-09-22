package com.pitchperfect.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProjectsActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        val etTitle = findViewById<EditText>(R.id.etProjectTitle)
        val etDescription = findViewById<EditText>(R.id.etProjectDescription)
        val etRequiredSkills = findViewById<EditText>(R.id.etRequiredSkills)
        val btnPost = findViewById<Button>(R.id.btnPostProject)
        val tvFeed = findViewById<TextView>(R.id.tvProjectFeed)

        // Load existing projects
        loadProjects(tvFeed)

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val requiredSkillsInput = etRequiredSkills.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Title and description are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requiredSkills = if (requiredSkillsInput.isEmpty()) emptyList() else requiredSkillsInput.split(",").map { it.trim() }
            val ownerId = auth.currentUser?.uid ?: "unknown"

            val project = hashMapOf(
                "ownerId" to ownerId,
                "title" to title,
                "description" to description,
                "requiredSkills" to requiredSkills,
                "status" to "open"
            )

            db.collection("projects").add(project)
                .addOnSuccessListener {
                    Toast.makeText(this, "Project posted!", Toast.LENGTH_SHORT).show()
                    etTitle.text.clear()
                    etDescription.text.clear()
                    etRequiredSkills.text.clear()
                    loadProjects(tvFeed)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loadProjects(tvFeed: TextView) {
        db.collection("projects").get()
            .addOnSuccessListener { result ->
                val sb = StringBuilder()
                for (document in result) {
                    val title = document.getString("title") ?: "Untitled"
                    val description = document.getString("description") ?: ""
                    val skills = document.get("skills") as? List<*> ?: (document.get("requiredSkills") as? List<*>)
                    sb.append("📌 $title\n")
                    sb.append("$description\n")
                    if (!skills.isNullOrEmpty()) {
                        sb.append("Required: ${skills.joinToString(", ")}\n")
                    }
                    sb.append("─────────────\n\n")
                }
                tvFeed.text = if (sb.isEmpty()) "No projects posted yet. Be the first!" else sb.toString()
            }
    }
}