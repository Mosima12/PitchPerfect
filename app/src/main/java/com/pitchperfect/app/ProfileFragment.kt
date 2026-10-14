package com.pitchperfect.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

/**
 * ProfileFragment — Displays user profile data, allows editing bio/skills,
 * and provides access to Settings and Logout.
 *
 * Reference: Firebase Firestore — https://firebase.google.com/docs/firestore/manage-data/add-data
 */
class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvUniversity = view.findViewById<TextView>(R.id.tvProfileUniversity)
        val etBio = view.findViewById<EditText>(R.id.etBio)
        val etSkills = view.findViewById<EditText>(R.id.etSkills)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)
        val btnSettings = view.findViewById<Button>(R.id.btnSettings)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "No user logged in — redirecting to MainActivity")
            startActivity(Intent(requireContext(), MainActivity::class.java))
            return
        }

        // Load user profile from Firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "Loaded profile for $userId")
                tvName.text = doc.getString("name") ?: "Student"
                tvUniversity.text = doc.getString("university") ?: ""
                etBio.setText(doc.getString("bio") ?: "")
                val skills = doc.get("skills") as? List<*>
                etSkills.setText(skills?.joinToString(", ") ?: "")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to load profile", e)
            }

        // Save updated bio and skills
        btnSave.setOnClickListener {
            val bio = etBio.text.toString().trim()
            val skillsInput = etSkills.text.toString().trim()
            val skillsList = if (skillsInput.isEmpty()) emptyList()
            else skillsInput.split(",").map { it.trim() }

            Log.d(TAG, "Saving profile — bio length=${bio.length}, skills=$skillsList")

            db.collection("users").document(userId).update(
                mapOf(
                    "bio" to bio,
                    "skills" to skillsList
                )
            ).addOnSuccessListener {
                Log.d(TAG, "Profile updated successfully")
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to update profile", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Open Settings
        btnSettings.setOnClickListener {
            Log.d(TAG, "Navigating to SettingsActivity")
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        // Logout
        btnLogout.setOnClickListener {
            Log.d(TAG, "Logging out user $userId")
            auth.signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}