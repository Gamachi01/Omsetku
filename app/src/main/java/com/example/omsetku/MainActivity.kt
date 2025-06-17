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
import com.example.omsetku.navigation.AppNavigation
import com.example.omsetku.navigation.AppNavGraph
import com.example.omsetku.ui.theme.OmsetkuTheme
import com.example.omsetku.viewmodels.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.omsetku.viewmodels.TaxViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmsetkuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val cartViewModel: CartViewModel = viewModel()
                    val taxViewModel: TaxViewModel = viewModel()
                    
                    AppNavGraph(
                        navController = navController,
                        cartViewModel = cartViewModel,
                        taxViewModel = taxViewModel
                    )
                }
            }
        }
    }
}