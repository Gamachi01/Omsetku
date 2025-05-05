package com.example.omsetku.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.TransactionList
import com.example.omsetku.data.Transaction

@Composable
fun HomeScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Beranda") }

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


    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> navController.navigate(Routes.HOME)
                        "Cashier" -> {  }
                        "Transaction" -> navController.navigate(Routes.TRANSACTION)
                        "HPP" -> { /* TODO */ }
                        "Report" -> { /* TODO */ }
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
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Omsetku",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F7E68)
                )
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { navController.navigate(Routes.PROFILE) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            var incomeAmount by remember { mutableStateOf(2500000) }
            var expenseAmount by remember { mutableStateOf(1200000) }

            Text(
                text = "Your Balance",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Text(
                text = "Rp %,d".format(incomeAmount - expenseAmount).replace(',', '.'),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceCard(
                    title = "Pemasukan",
                    amount = incomeAmount,
                    color = Color(0xFF08C39F),
                    iconRes =  R.drawable.arrow_up,
                    onClick = { incomeAmount += 50000 }
                )
                BalanceCard(
                    title = "Pengeluaran",
                    amount = expenseAmount,
                    color = Color(0xFFE74C3C),
                    iconRes =  R.drawable.arrow_down,
                    onClick = { expenseAmount += 25000 }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(80.dp)
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Rp ${amount / 1_000}.${(amount % 1_000).toString().padStart(3, '0')}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

            }

        }
    }
}

