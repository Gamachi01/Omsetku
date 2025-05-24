package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.DatePickerField
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.components.TransactionList
import com.example.omsetku.viewmodels.TransactionViewModel

enum class TransactionType {
    INCOME, EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("Transaction") }
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    
    // Status pesan sukses dan loading
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // State dari ViewModel
    val transactions by transactionViewModel.transactions.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()
    val error by transactionViewModel.error.collectAsState()

    val scrollState = rememberScrollState()
    
    // Effect untuk reset form setelah sukses menyimpan
    LaunchedEffect(showSuccessDialog) {
        if (showSuccessDialog) {
            // Reset form setelah 2 detik
            kotlinx.coroutines.delay(2000)
            showSuccessDialog = false
        }
    }
    
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
                        "Home" -> navController.navigate(Routes.HOME)
                        "Cashier" -> navController.navigate(Routes.CASHIER)
                        "Transaction" -> { /* Sudah di layar Transaction */ }
                        "HPP" -> navController.navigate(Routes.HPP)
                        "Report" -> navController.navigate(Routes.REPORT)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp)
                .padding(paddingValues)
        ) {
            Text(
                text = "Catat Transaksi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                TransactionButton(
                    text = "Pemasukan",
                    isSelected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                    modifier = Modifier.weight(1f)
                )
                TransactionButton(
                    text = "Pengeluaran",
                    isSelected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LabeledFieldBox(label = "Tanggal Transaksi") {
                DatePickerField(
                    value = tanggal,
                    onDateSelected = { tanggal = it }
                )
            }

            LabeledFieldBox(label = "Nominal") {
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    placeholder = { 
                        Text(
                            "Rp", 
                            fontSize = 14.sp, 
                            fontFamily = Poppins,
                            color = Color.Gray
                        ) 
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    )
                )
            }

            LabeledFieldBox(label = "Kategori") {
                OutlinedTextField(
                    value = if (selectedType == TransactionType.INCOME) "Pemasukan" else "Pengeluaran",
                    onValueChange = {},
                    enabled = false,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5),
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.Black
                    )
                )
            }

            LabeledFieldBox(label = "Deskripsi") {
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    placeholder = { 
                        Text(
                            "Masukkan deskripsi transaksi...", 
                            fontSize = 14.sp, 
                            fontFamily = Poppins,
                            color = Color.Gray
                        ) 
                    }
                )
            }

            // Error message
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    // Validasi input
                    if (tanggal.isBlank()) {
                        // Tampilkan error lokal 
                        transactionViewModel.clearError()
                        return@Button
                    }
                    
                    if (nominal.isBlank()) {
                        // Tampilkan error lokal
                        transactionViewModel.clearError()
                        return@Button
                    }
                    
                    // Konversi nominal dari string ke int dengan penanganan error yang lebih baik
                    val amount = try {
                        // Bersihkan semua karakter non-numerik (kecuali digit)
                        val cleanNominal = nominal.replace(Regex("[^0-9]"), "")
                        if (cleanNominal.isBlank()) {
                            0
                        } else {
                            cleanNominal.toInt()
                        }
                    } catch (e: Exception) {
                        // Jika gagal konversi, tampilkan error
                        transactionViewModel.clearError()
                        return@Button
                    }
                    
                    // Validasi amount
                    if (amount <= 0) {
                        // Tampilkan error lokal
                        transactionViewModel.clearError()
                        return@Button
                    }
                    
                    // Simpan transaksi
                    transactionViewModel.saveTransaction(
                        type = if (selectedType == TransactionType.INCOME) "INCOME" else "EXPENSE",
                        amount = amount,
                        date = tanggal,
                        description = deskripsi
                    )
                    
                    // Reset form jika berhasil
                    if (transactionViewModel.error.value == null) {
                        tanggal = ""
                        nominal = ""
                        deskripsi = ""
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                Text(
                    "Simpan", 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = Poppins,
                    fontSize = 16.sp
                )
            }
            }
        }
    }
    
    // Tampilkan dialog sukses
    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = { showSuccessDialog = false }
        )
    }
}

@Composable
fun TransactionButton(
    text: String,
    isSelected: Boolean,
    shape: Shape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor = Color(0xFF5ED0C5)
    val unselectedColor = Color.White

    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        contentPadding = PaddingValues(vertical = 0.dp),
        shape = shape,
        border = BorderStroke(1.dp, selectedColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = if (isSelected) Color.White else Color.Black
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins
        )
    }
}

@Composable
fun LabeledFieldBox(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5ED0C5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Transaksi Berhasil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Transaksi anda telah tercatat",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

