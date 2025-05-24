package com.example.omsetku

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class OmsetkuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inisialisasi Firebase dengan penanganan error
        try {
            if (!FirebaseApp.getApps(this).isEmpty()) {
                // Firebase sudah diinisialisasi sebelumnya
                Log.d("OmsetkuApplication", "Firebase already initialized")
            } else {
                // Inisialisasi Firebase
                FirebaseApp.initializeApp(this)
                Log.d("OmsetkuApplication", "Firebase initialized successfully")
            }
        } catch (e: Exception) {
            // Tangkap semua exception saat inisialisasi Firebase
            Log.e("OmsetkuApplication", "Failed to initialize Firebase: ${e.message}", e)
        }
    }
} 