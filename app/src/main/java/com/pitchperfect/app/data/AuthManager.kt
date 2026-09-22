package com.pitchperfect.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthManager {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val TAG = "AuthManager"

    fun registerUser(
        name: String,
        university: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (name.isEmpty() || university.isEmpty() || email.isEmpty() || password.isEmpty()) {
            onResult(false, "All fields are required")
            return
        }
        if (password.length < 6) {
            onResult(false, "Password must be at least 6 characters")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onResult(false, "Please enter a valid email address")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = auth.currentUser?.uid
                    val userMap = hashMapOf(
                        "name" to name,
                        "university" to university,
                        "email" to email,
                        "skills" to listOf<String>(),
                        "bio" to "",
                        "points" to 0
                    )

                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Log.d(TAG, "User profile saved to Firestore")
                                onResult(true, null)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error saving profile", e)
                                onResult(false, "Account created, but profile failed to save")
                            }
                    } else {
                        onResult(false, "User ID not found")
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onResult(false, task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, "Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    onResult(true, null)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    onResult(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
        Log.d(TAG, "User logged out")
    }
}