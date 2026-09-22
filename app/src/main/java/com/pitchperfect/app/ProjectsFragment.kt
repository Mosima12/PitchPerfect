package com.pitchperfect.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProjectsFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etTitle = view.findViewById<EditText>(R.id.etProjectTitle)
        val etDescription = view.findViewById<EditText>(R.id.etProjectDescription)
        val etSkills = view.findViewById<EditText>(R.id.etRequiredSkills)
        val btnPost = view.findViewById<Button>(R.id.btnPostProject)
        val tvFeed = view.findViewById<TextView>(R.id.tvProjectFeed)

        loadProjects(tvFeed)

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val skillsInput = etSkills.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Title and description required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val skills = if (skillsInput.isEmpty()) emptyList() else skillsInput.split(",").map { it.trim() }
            val ownerId = auth.currentUser?.uid ?: "unknown"

            val project = hashMapOf(
                "ownerId" to ownerId,
                "title" to title,
                "description" to description,
                "requiredSkills" to skills,
                "status" to "Open"
            )

            db.collection("projects").add(project)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Project posted!", Toast.LENGTH_SHORT).show()
                    etTitle.text.clear()
                    etDescription.text.clear()
                    etSkills.text.clear()
                    loadProjects(tvFeed)
                }
        }
    }

    private fun loadProjects(tvFeed: TextView) {
        db.collection("projects").get()
            .addOnSuccessListener { result ->
                val sb = StringBuilder()
                for (doc in result) {
                    val title = doc.getString("title") ?: "Untitled"
                    val description = doc.getString("description") ?: ""
                    val skills = doc.get("requiredSkills") as? List<*>
                    sb.append("📌  $title\n")
                    sb.append("$description\n")
                    if (!skills.isNullOrEmpty()) sb.append("Required: ${skills.joinToString(", ")}\n")
                    sb.append("─────────────\n\n")
                }
                tvFeed.text = if (sb.isEmpty()) "No projects yet. Be the first!" else sb.toString()
            }
    }
}