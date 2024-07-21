package com.example.trackfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class Intro : AppCompatActivity() {
    private val splashScreenDuration: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        Handler(Looper.getMainLooper()).postDelayed({
            // Setelah durasi berakhir, pindah ke MainActivity
            startActivity(Intent(this, Intro1::class.java))
            finish() // Hentikan SplashActivity agar tidak kembali saat tombol back ditekan
        }, splashScreenDuration)
    }
}