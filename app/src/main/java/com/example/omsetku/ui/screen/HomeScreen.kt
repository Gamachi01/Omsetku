package com.example.omsetku.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.TransactionList
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.data.Transaction
import com.example.omsetku.ui.theme.PrimaryColor
import com.example.omsetku.ui.theme.PrimaryLight
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.IncomeColor
import com.example.omsetku.ui.theme.ExpenseColor
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf("Home") }
    val scrollState = rememberScrollState()

    // data dummy
    val transactions = remember {
        listOf(
            Transaction("Pemasukan", "Penjualan Produk", 150000, "11 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 75000, "11 April 2025"),
            Transaction("Pengeluaran", "Beli Bahan Baku", 500000, "10 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 80000, "10 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 120000, "10 April 2025"),
            Transaction("Pengeluaran", "Gaji Karyawan", 500000, "09 April 2025"),
            Transaction("Pengeluaran", "Bayar Listrik", 200000, "09 April 2025"),
            Transaction("Pengeluaran", "Beli Kardus", 100000, "08 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 170000, "08 April 2025"),
            Transaction("Pemasukan", "Penjualan Online", 200000, "07 April 2025"),
            Transaction("Pengeluaran", "Beli Plastik", 75000, "07 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 95000, "06 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 110000, "06 April 2025"),
            Transaction("Pengeluaran", "Biaya Transportasi", 65000, "06 April 2025"),
            Transaction("Pengeluaran", "Beli Kertas", 30000, "05 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 135000, "05 April 2025"),
            Transaction("Pemasukan", "Penjualan Online", 155000, "04 April 2025"),
            Transaction("Pengeluaran", "Bayar Air", 100000, "04 April 2025"),
            Transaction("Pengeluaran", "Beli Stiker", 45000, "03 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 125000, "03 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 185000, "02 April 2025"),
            Transaction("Pengeluaran", "Gaji Karyawan", 500000, "02 April 2025"),
            Transaction("Pengeluaran", "Beli Box", 95000, "01 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 150000, "01 April 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 90000, "31 Maret 2025"),
            Transaction("Pengeluaran", "Biaya Iklan", 120000, "31 Maret 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 80000, "30 Maret 2025"),
            Transaction("Pengeluaran", "Bayar Internet", 180000, "30 Maret 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 160000, "29 Maret 2025"),
            Transaction("Pengeluaran", "Beli Lakban", 30000, "29 Maret 2025"),
            Transaction("Pemasukan", "Penjualan Produk", 140000, "28 Maret 2025")
        )
    }

    var incomeAmount by remember { mutableStateOf(2500000) }
    var expenseAmount by remember { mutableStateOf(1200000) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> { /* Sudah di layar Home */ }
                        "Cashier" -> navController.navigate(Routes.CASHIER)
                        "Transaction" -> navController.navigate(Routes.TRANSACTION)
                        "HPP" -> navController.navigate(Routes.HPP)
                        "Report" -> navController.navigate(Routes.REPORT)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header: Omsetku dan icon profil
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Omsetku",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryVariant,
                    fontFamily = Poppins
                )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                        .clickable { navController.navigate(Routes.PROFILE) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_icon),
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Saldo Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Saldo Saat Ini",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        fontFamily = Poppins
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = "Rp %,d".format(incomeAmount - expenseAmount).replace(',', '.'),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryColor,
                        fontFamily = Poppins
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Pemasukan",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            Text(
                                text = "Rp %,d".format(incomeAmount).replace(',', '.'),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeColor,
                                fontFamily = Poppins
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Pengeluaran",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            Text(
                                text = "Rp %,d".format(expenseAmount).replace(',', '.'),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseColor,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }

            // Hapus text "Transaksi Terbaru" dan langsung tampilkan TransactionList
            TransactionList(transactions = transactions)
        }
    }
}

@Composable
fun BalanceCard(
    title: String,
    amount: Int,
    color: Color,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp %,d".format(amount).replace(',', '.'),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontFamily = Poppins
                )
            }
        }
    }
}

