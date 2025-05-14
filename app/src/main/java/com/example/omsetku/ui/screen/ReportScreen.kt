package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins

@OptIn(ExperimentalMaterial3Api::class)
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
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Laporan Keuangan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tanggal periode
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.transactioncalender),
                        contentDescription = "Tanggal",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF5ED0C5)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "01 Maret 2025 - 31 Maret 2025",
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Filter dan Download Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Filter
                OutlinedButton(
                    onClick = { /* TODO: Implementasi filter */ },
                    modifier = Modifier
                        .weight(0.4f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF5ED0C5)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF5ED0C5))
                ) {
                    Text(
                        text = "⚙️",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Filter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
                
                // Tombol Download Laporan
                Button(
                    onClick = { /* TODO: Implementasi download laporan */ },
                    modifier = Modifier
                        .weight(0.6f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5ED0C5)
                    )
                ) {
                    Text(
                        text = "⬇️",
                        fontSize = 16.sp
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Ringkasan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Cards untuk Total Pendapatan dan Pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Total Pendapatan
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F7F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Pendapatan",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp 12.000.000",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color(0xFF08C39F)
                        )
                    }
                }
                
                // Card Total Pengeluaran
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFDEDED)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Pengeluaran",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp 7.700.000",
                            fontSize = 18.sp,
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
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F7F5)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Laba Bersih",
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp 4.300.000",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF2F7E68)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Rincian Transaksi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Transaksi Harian Cards
            for (i in 1..5) {
                TransactionDayCard(
                    date = "30 Maret 2025",
                    income = 1200000,
                    expense = 500000,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionDayCard(
    date: String,
    income: Int,
    expense: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    color = Color.Black
                )
                
                Text(
                    text = "Rp ${(income - expense) / 1000}k",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = if (income > expense) Color(0xFF08C39F) else Color(0xFFE74C3C)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Income Mini Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF08C39F)
                        ) { }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = "Masuk",
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Rp ${income / 1000}k",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins,
                                color = Color(0xFF08C39F)
                            )
                        }
                    }
                }
                
                // Expense Mini Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE74C3C)
                        ) { }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = "Keluar",
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Rp ${expense / 1000}k",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins,
                                color = Color(0xFFE74C3C)
                            )
                        }
                    }
                }
            }
        }
    }
} 