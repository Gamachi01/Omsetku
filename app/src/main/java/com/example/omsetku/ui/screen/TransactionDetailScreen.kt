package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.models.CartItem
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.viewmodels.CartViewModel
import com.example.omsetku.viewmodels.TaxViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    taxViewModel: TaxViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val taxSettings by taxViewModel.taxSettings.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val transactionSuccess by cartViewModel.transactionSuccess.collectAsState()
    val error by cartViewModel.error.collectAsState()

    val subtotal = cartViewModel.getSubtotal()
    val taxRate = if (taxSettings.enabled) taxSettings.rate else 0
    val tax = if (taxSettings.enabled) (subtotal * taxRate / 100) else 0
    val total = subtotal + tax

    val currentDate = remember { System.currentTimeMillis() }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")) }
    val timeFormat = remember { SimpleDateFormat("HH.mm", Locale("id", "ID")) }
    val formattedDate = remember { dateFormat.format(Date(currentDate)) }
    val formattedTime = remember { timeFormat.format(Date(currentDate)) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionSuccess) {
        if (transactionSuccess) {
            showSuccessDialog = true
            cartViewModel.resetTransactionSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detail Transaksi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text("Tanggal: $formattedDate", fontSize = 15.sp, fontFamily = Poppins, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Waktu: $formattedTime", fontSize = 15.sp, fontFamily = Poppins, color = Color.DarkGray)
            }

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang kosong", fontSize = 16.sp, fontFamily = Poppins, color = Color.Gray)
                }
            } else {
                cartItems.forEach { item ->
                    ProductDetailItem(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ringkasan Pembayaran", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 15.sp, fontFamily = Poppins, color = Color.DarkGray)
                        Text("Rp $subtotal", fontSize = 15.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            if (taxSettings.enabled) "Pajak (${taxSettings.rate}%)" else "Pajak (0%)",
                            fontSize = 15.sp,
                            fontFamily = Poppins,
                            color = Color.DarkGray
                        )
                        Text("Rp $tax", fontSize = 15.sp, fontFamily = Poppins, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                        Text("Rp $total", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                    }
                }
            }

            if (error != null) {
                Text(error ?: "", color = Color.Red, fontSize = 14.sp, fontFamily = Poppins)
            }

            Button(
                onClick = {
                    cartViewModel.saveTransaction(taxSettings.enabled, taxSettings.rate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryVariant),
                enabled = cartItems.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Konfirmasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.White)
                }
            }
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun ProductDetailItem(item: CartItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${item.quantity} Item", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", fontSize = 14.sp, fontFamily = Poppins, color = Color.DarkGray)
                Text("Rp ${item.subtotal}", fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = Poppins)
            }
        }
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = PrimaryVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Transaksi Berhasil!", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Transaksi anda telah tercatat.", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
