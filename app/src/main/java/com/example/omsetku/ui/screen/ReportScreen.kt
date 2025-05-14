package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins

@Composable
fun ReportScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Report") }
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> navController.navigate(Routes.HOME)
                        "Cashier" -> navController.navigate(Routes.CASHIER)
                        "Transaction" -> navController.navigate(Routes.TRANSACTION)
                        "HPP" -> navController.navigate(Routes.HPP)
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Laporan Keuangan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tanggal periode
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Tanggal",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "01 Maret 2025 - 31 Maret 2025",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filter dan Download Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tombol Filter
                Button(
                    onClick = { /* TODO: Implementasi filter */ },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5ED0C5)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5ED0C5))
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color(0xFF5ED0C5)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Filter",
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    )
                }
                
                // Tombol Download Laporan
                Button(
                    onClick = { /* TODO: Implementasi download laporan */ },
                    modifier = Modifier
                        .height(40.dp)
                        .width(240.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5ED0C5)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Download Laporan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cards untuk Total Pendapatan dan Pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Card Total Pendapatan
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F7F5)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5ED0C5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Pendapatan",
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        Text(
                            text = "Rp 12.000.000",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color(0xFF2F7E68)
                        )
                    }
                }
                
                // Card Total Pengeluaran
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFDEDED)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE74C3C))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Pengeluaran",
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        Text(
                            text = "Rp 7.700.000",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color(0xFFE74C3C)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card Laba Bersih
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F7F5)
                ),
                border = BorderStroke(1.dp, Color(0xFF5ED0C5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Laba Bersih",
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        color = Color.Black
                    )
                    Text(
                        text = "Rp 500.000",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF2F7E68)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tabel Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Deskripsi",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Saldo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Pendapatan
            TableSectionHeader(text = "Pendapatan")
            TableItem(description = "Kopi", amount = "Rp 4.000.000")
            TableItem(description = "Jus Jeruk", amount = "Rp 5.500.000")
            TableItem(description = "Roti Tawar", amount = "Rp 2.500.000")
            
            // Total Pendapatan
            TableSectionTotal(label = "Total Pendapatan", amount = "Rp 12.000.000")
            
            // Harga Pokok Penjualan
            TableSectionHeader(text = "Harga Pokok Penjualan")
            TableItem(description = "Kopi", amount = "Rp 1.200.000")
            TableItem(description = "Jus Jeruk", amount = "Rp 2.100.000")
            TableItem(description = "Roti Tawar", amount = "Rp 1.200.000")
            
            // Total HPP
            TableSectionTotal(label = "Total Harga Pokok Penjualan", amount = "Rp 4.500.000")
            
            // Laba Kotor
            TableSectionTotal(label = "Laba Kotor", amount = "Rp 7.500.000", isHighlighted = true)
            
            // Biaya Operasional
            TableSectionHeader(text = "Biaya Operasional")
            TableItem(description = "Sewa", amount = "Rp 2.000.000")
            TableItem(description = "Gaji Karyawan", amount = "Rp 4.500.000")
            TableItem(description = "Listrik & Air", amount = "Rp 500.000")
            
            // Total Biaya Operasional
            TableSectionTotal(label = "Total Biaya Operasional", amount = "Rp 7.000.000")
            
            // Laba Bersih & Pajak
            TableSectionTotal(label = "Laba Bersih", amount = "Rp 500.000", isHighlighted = true)
            TableItem(description = "Pajak Penghasilan UMKM (0,5%)", amount = "Rp 2.500")
            TableSectionTotal(label = "Laba Bersih setelah Pajak", amount = "Rp 497.500", isHighlighted = true)
            
            Spacer(modifier = Modifier.height(80.dp)) // Untuk memberikan space di bagian bawah
        }
    }
}

@Composable
fun TableSectionHeader(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEEEEE))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black
        )
    }
}

@Composable
fun TableItem(description: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = amount,
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TableSectionTotal(label: String, amount: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHighlighted) Color(0xFFEEEEEE) else Color.White)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = if (isHighlighted) Color(0xFF2F7E68) else Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
} 