package com.example.trackfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val signup = findViewById<TextView>(R.id.textView6)
        signup.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val usernameEditText = findViewById<EditText>(R.id.usernamenya)
        val emailEditText = findViewById<EditText>(R.id.emailnya)
        val passwordEditText = findViewById<EditText>(R.id.passET)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmpassET)
        val button2 = findViewById<Button>(R.id.buttonsignup)
        button2.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Enter a valid email"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 1) {
                passwordEditText.error = "Password harus minimal 1 karakter"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordEditText.error = "Passwords berbeda"
                confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            registerUser(username, email, password)
        }

    }
    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userId = user?.uid

                val userMap = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )

                if (userId != null) {
                    // Tambahkan data pengguna ke Firestore
                    db.collection("users").document(userId).set(userMap).addOnSuccessListener {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java).apply {
                            putExtra("userId", userId)
                        }
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}