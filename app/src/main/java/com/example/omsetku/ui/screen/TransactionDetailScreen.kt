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
    // Mengambil data dari ViewModel
    val cartItems by cartViewModel.cartItems.collectAsState()
    val taxSettings by taxViewModel.taxSettings.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val transactionSuccess by cartViewModel.transactionSuccess.collectAsState()
    val error by cartViewModel.error.collectAsState()
    
    // Debug log untuk melihat isi cart
    LaunchedEffect(cartItems) {
        println("DEBUG: CartItems di TransactionDetailScreen: ${cartItems.size} items")
        cartItems.forEach { item ->
            println("DEBUG: Item: ${item.name}, Quantity: ${item.quantity}, Price: ${item.price}, Subtotal: ${item.subtotal}")
        }
    }
    
    // Perhitungan total
    val subtotal = cartViewModel.getSubtotal()
    val taxRate = if (taxSettings.enabled) taxSettings.rate else 0
    val tax = if (taxSettings.enabled) (subtotal * taxRate / 100) else 0
    val total = subtotal + tax
    
    // Format tanggal dan waktu
    val currentDate = remember { System.currentTimeMillis() }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")) }
    val timeFormat = remember { SimpleDateFormat("HH.mm", Locale("id", "ID")) }
    val formattedDate = remember { dateFormat.format(Date(currentDate)) }
    val formattedTime = remember { timeFormat.format(Date(currentDate)) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Effect untuk handle sukses transaksi
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
                            imageVector = Icons.Default.ArrowBack,
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
            // Tanggal dan Waktu
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Tanggal: $formattedDate",
                    fontSize = 15.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Waktu: $formattedTime",
                    fontSize = 15.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
            }
            
            // Product List
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keranjang kosong",
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                cartItems.forEach { item ->
                    ProductDetailItem(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Payment Summary
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ringkasan Pembayaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal",
                            fontSize = 15.sp,
                            fontFamily = Poppins,
                            color = Color.DarkGray
                        )
                        
                        Text(
                            text = "Rp $subtotal",
                            fontSize = 15.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (taxSettings.enabled) "Pajak (${taxSettings.rate}%)" else "Pajak (0%)",
                            fontSize = 15.sp,
                            fontFamily = Poppins,
                            color = Color.DarkGray
                        )
                        
                        Text(
                            text = "Rp $tax",
                            fontSize = 15.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        
                        Text(
                            text = "Rp $total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                }
            }
            
            // Error message
            if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Confirmation Button
            Button(
                onClick = { 
                    // Simpan transaksi ke Firestore
                    cartViewModel.saveTransaction(
                        taxEnabled = taxSettings.enabled,
                        taxRate = taxSettings.rate
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryVariant
                ),
                enabled = cartItems.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Konfirmasi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.White
                    )
                }
            }
        }
    }
    
    // Success Dialog
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Product Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${item.quantity} Item",
                fontSize = 14.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal:",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
                
                Text(
                    text = "Rp ${item.subtotal}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    color = Color.Black
                )
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = PrimaryVariant
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Transaksi Berhasil!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Transaksi anda telah tercatat.",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
} 