package com.pitchperfect.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pitchperfect.app.data.AuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private var isRegisterMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager()

        // Auto-redirect if already logged in
        if (authManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        val etName = findViewById<EditText>(R.id.etName)
        val etUniversity = findViewById<EditText>(R.id.etUniversity)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val tabRegister = findViewById<TextView>(R.id.tabRegister)
        val tabLogin = findViewById<TextView>(R.id.tabLogin)
        val registerFields = findViewById<LinearLayout>(R.id.registerFields)
        val tvFormTitle = findViewById<TextView>(R.id.tvFormTitle)
        val tvFormSubtitle = findViewById<TextView>(R.id.tvFormSubtitle)

        fun updateTabs() {
            if (isRegisterMode) {
                tabRegister.setBackgroundResource(R.drawable.bg_tab_active)
                tabRegister.setTextColor(getColor(R.color.white))
                tabLogin.setBackgroundResource(R.drawable.bg_tab_inactive)
                tabLogin.setTextColor(getColor(R.color.text_gray))
                registerFields.visibility = View.VISIBLE
                tvFormTitle.text = "Create your account"
                tvFormSubtitle.text = "Use your university email to get started."
                btnSubmit.text = "Create Account"
            } else {
                tabLogin.setBackgroundResource(R.drawable.bg_tab_active)
                tabLogin.setTextColor(getColor(R.color.white))
                tabRegister.setBackgroundResource(R.drawable.bg_tab_inactive)
                tabRegister.setTextColor(getColor(R.color.text_gray))
                registerFields.visibility = View.GONE
                tvFormTitle.text = "Welcome back"
                tvFormSubtitle.text = "Login to continue building."
                btnSubmit.text = "Login"
            }
        }

        tabRegister.setOnClickListener { isRegisterMode = true; updateTabs() }
        tabLogin.setOnClickListener { isRegisterMode = false; updateTabs() }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (isRegisterMode) {
                val name = etName.text.toString().trim()
                val university = etUniversity.text.toString().trim()

                authManager.registerUser(name, university, email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Registration Successful! Please Login.", Toast.LENGTH_LONG).show()
                        isRegisterMode = false
                        updateTabs()
                    } else {
                        Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                authManager.loginUser(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}