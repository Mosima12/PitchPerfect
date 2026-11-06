package com.pitchperfect.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SettingsActivity"
    private val auth = Firebase.auth
    private val prefs by lazy { getSharedPreferences("pitchperfect_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Log.d(TAG, "onCreate: Settings opened")

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            Log.d(TAG, "Back button pressed")
            finish()
        }

        // --- LANGUAGE SWITCHER LOGIC ---
        val btnLanguage = findViewById<Button>(R.id.btnLanguage)

        // Show current language on the button
        val currentLang = LocaleHelper.getSavedLanguage(this)
        btnLanguage.text = when (currentLang) {
            "af" -> getString(R.string.afrikaans)
            "zu" -> getString(R.string.isizulu)
            else -> getString(R.string.english)
        }

        btnLanguage.setOnClickListener {
            val languages = arrayOf(getString(R.string.english), getString(R.string.afrikaans), getString(R.string.isizulu))
            val codes = arrayOf("en", "af", "zu")

            AlertDialog.Builder(this)
                .setTitle(R.string.select_language)
                .setItems(languages) { _, which ->
                    val selectedCode = codes[which]
                    LocaleHelper.saveLanguage(this, selectedCode)
                    recreate() // Restart activity to apply the new language instantly
                }
                .show()
        }
        // -------------------------------

        val switchNotifications = findViewById<Switch>(R.id.switchNotifications)
        val switchPublicProfile = findViewById<Switch>(R.id.switchPublicProfile)
        val switchMatchAlerts = findViewById<Switch>(R.id.switchMatchAlerts)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)

        // Load saved preferences
        switchNotifications.isChecked = prefs.getBoolean("notifications", true)
        switchPublicProfile.isChecked = prefs.getBoolean("public_profile", true)
        switchMatchAlerts.isChecked = prefs.getBoolean("match_alerts", true)

        // Save preferences when toggled
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
            Log.d(TAG, "Notifications: $isChecked")
        }

        switchPublicProfile.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("public_profile", isChecked).apply()
            Log.d(TAG, "Public profile: $isChecked")
        }

        switchMatchAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("match_alerts", isChecked).apply()
            Log.d(TAG, "Match alerts: $isChecked")
        }

        // Change password — sends a reset email via Firebase
        btnChangePassword.setOnClickListener {
            val user = auth.currentUser
            if (user?.email != null) {
                Log.d(TAG, "Sending password reset email to ${user.email}")
                auth.sendPasswordResetEmail(user.email!!)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to send reset email", e)
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        // Delete account
        btnDeleteAccount.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                Log.w(TAG, "Delete account requested for ${user.email}")
                user.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Account deleted")
                        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                        prefs.edit().clear().apply()
                        auth.signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Account deletion failed", e)
                        Toast.makeText(this, "Please log in again to delete your account.", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    // This is crucial! It applies the language before the screen loads.
    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }
}