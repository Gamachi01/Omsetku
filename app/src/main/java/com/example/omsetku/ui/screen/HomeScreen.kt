package com.example.omsetku.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.TransactionItem
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.data.Transaction
import com.example.omsetku.ui.theme.*
import com.example.omsetku.viewmodels.TransactionViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("Home") }
    val scrollState = rememberScrollState()

    // State dari ViewModel
    val transactions by transactionViewModel.transactions.collectAsState()
    val incomeAmount by transactionViewModel.incomeAmount.collectAsState()
    val expenseAmount by transactionViewModel.expenseAmount.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()
    val error by transactionViewModel.error.collectAsState()
    
    // Effect untuk memuat data transaksi saat screen dibuka
    LaunchedEffect(Unit) {
        transactionViewModel.loadTransactions()
    }

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
                        .background(
                            color = PrimaryLight,
                            shape = CircleShape
                        )
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
                    
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Rp %,d".format(incomeAmount - expenseAmount).replace(',', '.'),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryColor,
                            fontFamily = Poppins
                        )
                    }
                    
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

            // Text "Transaksi Terakhir"
            Text(
                text = "Transaksi Terakhir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Left
            )
            
            // Loading state
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } 
            // Error message
            else if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEFEF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error ?: "",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            // Empty state
            else if (transactions.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum ada transaksi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            fontFamily = Poppins,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Catat transaksi pertamamu di menu Transaksi",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = Poppins,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { navController.navigate(Routes.TRANSACTION) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor
                            )
                        ) {
                            Text(
                                text = "Catat Transaksi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }
            // Transactions list
            else {
                // Menampilkan daftar transaksi tanpa panel ekspansi
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
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

