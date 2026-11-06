package com.pitchperfect.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class CreateIdeaActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private var selectedImageUri: Uri? = null

    // This handles the result of the image picker
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedImageUri = data?.data
            // Display the selected image
            findViewById<ImageView>(R.id.ivIdeaImage).setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_idea)

        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val etIdeaName = findViewById<TextInputEditText>(R.id.etIdeaName)
        val etProblem = findViewById<TextInputEditText>(R.id.etProblem)
        val etSolution = findViewById<TextInputEditText>(R.id.etSolution)
        val etTargetUsers = findViewById<TextInputEditText>(R.id.etTargetUsers)
        val etSkills = findViewById<TextInputEditText>(R.id.etSkills)
        val btnPublish = findViewById<MaterialButton>(R.id.btnPublish)
        val btnSelectImage = findViewById<MaterialButton>(R.id.btnSelectImage)

        // Handle Image Selection Click
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        btnPublish.setOnClickListener {
            val title = etIdeaName.text.toString().trim()
            val problem = etProblem.text.toString().trim()
            val solution = etSolution.text.toString().trim()
            val targetUsers = etTargetUsers.text.toString().trim()
            val skillsString = etSkills.text.toString().trim()

            if (title.isEmpty() || problem.isEmpty() || solution.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val skillsList = if (skillsString.isNotEmpty()) {
                skillsString.split(",").map { it.trim() }
            } else {
                emptyList()
            }

            btnPublish.isEnabled = false
            btnPublish.text = "Publishing..."

            // Check if an image was selected
            if (selectedImageUri != null) {
                uploadImageAndSaveIdea(title, problem, solution, targetUsers, skillsList, btnPublish)
            } else {
                saveIdeaToFirestore(title, problem, solution, targetUsers, skillsList, null, btnPublish)
            }
        }
    }

    private fun uploadImageAndSaveIdea(
        title: String, problem: String, solution: String, targetUsers: String, skillsList: List<String>, btnPublish: MaterialButton
    ) {
        // Create a unique name for the image
        val imageRef = storage.reference.child("idea_images/${UUID.randomUUID()}.jpg")

        // Upload the file to Firebase Storage (Blob Storage)
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                // Get the download URL after successful upload
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Save the idea with the image URL
                    saveIdeaToFirestore(title, problem, solution, targetUsers, skillsList, imageUrl, btnPublish)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                btnPublish.isEnabled = true
                btnPublish.text = "PUBLISH IDEA"
            }
    }

    private fun saveIdeaToFirestore(
        title: String, problem: String, solution: String, targetUsers: String, skillsList: List<String>, imageUrl: String?, btnPublish: MaterialButton
    ) {
        val userId = auth.currentUser?.uid ?: "anonymous"
        val creatorName = auth.currentUser?.displayName ?: "Student Entrepreneur"

        val newIdea = hashMapOf(
            "creatorId" to userId,
            "creatorName" to creatorName,
            "title" to title,
            "problem" to problem,
            "solution" to solution,
            "targetUsers" to targetUsers,
            "requiredSkills" to skillsList,
            "imageUrl" to imageUrl, // Storing the Blob URL in NoSQL
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("ideas").add(newIdea)
            .addOnSuccessListener {
                Toast.makeText(this, "Idea published successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnPublish.isEnabled = true
                btnPublish.text = "PUBLISH IDEA"
            }
    }
}