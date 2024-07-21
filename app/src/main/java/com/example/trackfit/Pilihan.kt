package com.example.trackfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Pilihan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilihan)
        val button1 = findViewById<Button>(R.id.SignIn)

        // Menetapkan OnClickListener setelah mendapatkan referensi tombol
        button1.setOnClickListener{
            val intent1 = Intent(this, Login::class.java)
            startActivity(intent1)
        }
        val button2 = findViewById<Button>(R.id.SignUp)

        // Menetapkan OnClickListener setelah mendapatkan referensi tombol
        button2.setOnClickListener{
            val intent1 = Intent(this, Register::class.java)
            startActivity(intent1)
        }
    }
}