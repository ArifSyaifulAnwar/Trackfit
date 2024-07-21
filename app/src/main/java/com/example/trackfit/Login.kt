package com.example.trackfit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val signup = findViewById<TextView>(R.id.textView6)
        signup.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.buttonsignin)
        val emailEditText = findViewById<EditText>(R.id.emailnya)
        val passwordEditText = findViewById<EditText>(R.id.passET)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty()) {
                emailEditText.error = "Email tidak boleh kosong"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Password tidak boleh kosong"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, navigate to the main activity or dashboard
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed, show appropriate error message
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        // Incorrect password
                        Toast.makeText(this, "Password salah. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                    } catch (e: FirebaseAuthInvalidUserException) {
                        // Invalid email
                        Toast.makeText(this, "Email tidak ditemukan. Silakan daftar.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // Other errors
                        Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
