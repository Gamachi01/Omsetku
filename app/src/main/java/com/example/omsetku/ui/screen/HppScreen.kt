package com.example.omsetku.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.ui.components.BottomNavBar

@Composable
fun HppScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("HPP") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> navController.navigate("home")
                        "Cashier" -> navController.navigate("cashier")
                        "Transaction" -> navController.navigate("transaction")
                        "HPP" -> { /* Sudah di layar HPP */ }
                        "Report" -> navController.navigate("report")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Harga Pokok Produksi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F7E68)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Fitur ini akan membantu Anda menghitung harga pokok produksi",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { /* TODO: Implementasi perhitungan HPP */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F7E68)
                )
            ) {
                Text("Hitung HPP Produk Baru")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Implementasi melihat daftar HPP */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F7E68)
                )
            ) {
                Text("Lihat Daftar HPP Produk")
            }
        }
    }
} 