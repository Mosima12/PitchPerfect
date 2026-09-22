package com.pitchperfect.app

import android.content.Intent
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

class ProfileFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvUniversity = view.findViewById<TextView>(R.id.tvProfileUniversity)
        val etBio = view.findViewById<EditText>(R.id.etBio)
        val etSkills = view.findViewById<EditText>(R.id.etSkills)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val userId = auth.currentUser?.uid
        if (userId == null) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                tvName.text = doc.getString("name") ?: "Student"
                tvUniversity.text = doc.getString("university") ?: ""
                etBio.setText(doc.getString("bio") ?: "")
                val skills = doc.get("skills") as? List<*>
                etSkills.setText(skills?.joinToString(", ") ?: "")
            }

        btnSave.setOnClickListener {
            val bio = etBio.text.toString().trim()
            val skillsInput = etSkills.text.toString().trim()
            val skillsList = if (skillsInput.isEmpty()) emptyList() else skillsInput.split(",").map { it.trim() }

            db.collection("users").document(userId).update(
                mapOf("bio" to bio, "skills" to skillsList)
            ).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}