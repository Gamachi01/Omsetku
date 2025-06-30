package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.domain.model.CartItem
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.MediumText
import com.example.omsetku.ui.theme.PrimaryLight
import com.example.omsetku.viewmodels.CartViewModel
import com.example.omsetku.viewmodels.ProductViewModel
import com.example.omsetku.viewmodels.TaxViewModel
import com.example.omsetku.viewmodels.HppViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.omsetku.ui.components.ProfitAlertDialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    taxViewModel: TaxViewModel = viewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    hppViewModel: HppViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val taxSettings by taxViewModel.taxSettings.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val transactionSuccess by cartViewModel.transactionSuccess.collectAsState()
    val error by cartViewModel.error.collectAsState()
    val products by productViewModel.products.collectAsState()
    val marginProfit by hppViewModel.marginProfit.collectAsState()

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
    var showProfitAlert by remember { mutableStateOf(false) }
    var transactionProfit by remember { mutableStateOf(0.0) }

    // Efek untuk memuat produk saat pertama kali
    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    // Efek untuk menangani transaksi sukses
    LaunchedEffect(transactionSuccess) {
        if (transactionSuccess) {
            showSuccessDialog = true
            val marginProfitValue = marginProfit.toDoubleOrNull() ?: 0.0
            val totalProfit = cartViewModel.calculateTotalProfit(products, marginProfitValue)
            if (totalProfit > 0) {
                transactionProfit = totalProfit
            }
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
                    val marginProfitValue = marginProfit.toDoubleOrNull() ?: 0.0
                    cartViewModel.saveTransaction(taxSettings.enabled, taxSettings.rate, products, marginProfitValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryVariant),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    "Konfirmasi Transaksi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.White
                )
            }
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                if (transactionProfit > 0) {
                    showProfitAlert = true
                } else {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                    cartViewModel.clearCartAfterSuccess()
                }
            }
        )
    }

    if (showProfitAlert) {
        ProfitAlertDialog(
            profit = transactionProfit,
            onDismiss = {
                showProfitAlert = false
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
                cartViewModel.clearCartAfterSuccess()
            }
        )
    }
}

@Composable
fun ProductDetailItem(item: CartItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val painter = if (item.imageUrl != null && item.imageUrl.isNotEmpty()) {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build()
            )
        } else {
            painterResource(id = R.drawable.ic_image_placeholder)
        }
        Image(
            painter = painter,
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Check in green circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5ED0C5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Title
                Text(
                    text = "Transaksi Berhasil!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = PrimaryVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Transaksi anda telah tercatat.",
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    color = MediumText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Lanjutkan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.White
                    )
                }
            }
        }
    }
}
