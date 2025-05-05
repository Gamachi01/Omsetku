package com.example.omsetku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Langsung pindah ke MainActivity setelah Splash Screen
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

