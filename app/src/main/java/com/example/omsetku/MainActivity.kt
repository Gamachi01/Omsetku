package com.example.omsetku

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.example.omsetku.Navigation.AppNavigation
import com.example.omsetku.Navigation.AppNavGraph
import com.example.omsetku.ui.theme.OmsetkuTheme
import com.example.omsetku.viewmodels.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.omsetku.viewmodels.TaxViewModel

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

        // Gunakan try-catch hanya untuk debug, bukan di sekitar fungsi composable
        try {
            setContent {
                OmsetkuTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        // Buat ViewModel sekali di sini dan bagikan ke seluruh navigasi
                        val cartViewModel: CartViewModel = viewModel()
                        val taxViewModel: TaxViewModel = viewModel()

                        // Gunakan AppNavGraph jika user sudah login, jika tidak gunakan AppNavigation
                        AppNavGraph(
                            navController = navController,
                            cartViewModel = cartViewModel,
                            taxViewModel = taxViewModel
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in setting content: ${e.message}", e)
        }
    }
}