package com.example.omsetku

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.example.omsetku.Navigation.AppNavGraph
import com.example.omsetku.ui.theme.OmsetkuThemeComposable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pastikan Firebase sudah diinisialisasi sebelum melanjutkan
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MainActivity", "Firebase initialized in MainActivity")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to initialize Firebase: ${e.message}", e)
        }
        
        setContent {
            try {
                OmsetkuThemeComposable {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        val navController = rememberNavController()
                        AppNavGraph(navController = navController)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in setting content: ${e.message}", e)
                // Jika terjadi error, coba tampilkan UI minimal
                OmsetkuThemeComposable {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        // Tampilkan UI sederhana jika terjadi error
                    }
                }
            }
        }
    }
}