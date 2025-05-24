package com.example.omsetku

import android.app.Application
import com.google.firebase.FirebaseApp

class OmsetkuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)
    }
} 