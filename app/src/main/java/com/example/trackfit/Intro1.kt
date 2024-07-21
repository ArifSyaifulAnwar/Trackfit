package com.example.trackfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Intro1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro1)
        val button1 = findViewById<Button>(R.id.buttonnext)

        // Menetapkan OnClickListener setelah mendapatkan referensi tombol
        button1.setOnClickListener{
            val intent1 = Intent(this, Pilihan::class.java)
            startActivity(intent1)
        }
        val button2 = findViewById<Button>(R.id.button)

        // Menetapkan OnClickListener setelah mendapatkan referensi tombol
        button2.setOnClickListener{
            val intent1 = Intent(this, Intro::class.java)
            startActivity(intent1)
        }
    }
}