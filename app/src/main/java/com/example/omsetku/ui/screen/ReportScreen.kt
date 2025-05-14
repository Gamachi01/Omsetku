package com.example.omsetku.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun ReportScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Report") }
    var selectedPeriod by remember { mutableStateOf("Mingguan") }

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
                        "HPP" -> navController.navigate("hpp")
                        "Report" -> { /* Sudah di layar Report */ }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Text(
                text = "Laporan Keuangan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F7E68)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = selectedPeriod == "Harian",
                    onClick = { selectedPeriod = "Harian" },
                    label = { Text("Harian") }
                )
                FilterChip(
                    selected = selectedPeriod == "Mingguan",
                    onClick = { selectedPeriod = "Mingguan" },
                    label = { Text("Mingguan") }
                )
                FilterChip(
                    selected = selectedPeriod == "Bulanan",
                    onClick = { selectedPeriod = "Bulanan" },
                    label = { Text("Bulanan") }
                )
                FilterChip(
                    selected = selectedPeriod == "Tahunan",
                    onClick = { selectedPeriod = "Tahunan" },
                    label = { Text("Tahunan") }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ringkasan Laporan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pemasukan")
                        Text("Rp 2.500.000", fontWeight = FontWeight.Bold, color = Color(0xFF08C39F))
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pengeluaran")
                        Text("Rp 1.200.000", fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C))
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Laba Bersih", fontWeight = FontWeight.Bold)
                        Text("Rp 1.300.000", fontWeight = FontWeight.Bold, color = Color(0xFF2F7E68))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Implementasi ekspor laporan */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F7E68)
                )
            ) {
                Text("Ekspor Laporan")
            }
        }
    }
} 