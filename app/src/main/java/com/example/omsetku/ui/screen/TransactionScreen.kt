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
import com.example.omsetku.ui.components.FormField
import com.example.omsetku.ui.components.StandardTextField
import com.example.omsetku.ui.components.MultilineTextField
import com.example.omsetku.firebase.FirestoreRepository

enum class TransactionType {
    INCOME, EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel<TransactionViewModel>()
) {
    var selectedItem by remember { mutableStateOf("Transaction") }
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategoriPengeluaran by remember { mutableStateOf("Usaha") }
    var kategoriLainnya by remember { mutableStateOf("") }
    var kategoriPemasukan by remember { mutableStateOf("Usaha") }
    var kategoriPemasukanLainnya by remember { mutableStateOf("") }

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
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
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

            FormField(label = "Tanggal Transaksi") {
                DatePickerField(
                    value = tanggal,
                    onDateSelected = { tanggal = it }
                )
            }

            FormField(label = "Nominal") {
                StandardTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    placeholder = "Rp",
                    isRupiah = true
                )
            }

            // Bungkus seluruh bagian Kategori dengan FormField
            FormField(label = "Kategori") {
                if (selectedType == TransactionType.EXPENSE) {
                    // Dropdown kategori: Usaha, Lainnya
                    var expanded by remember { mutableStateOf(false) }
                    val kategoriList = listOf("Usaha", "Lainnya")
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StandardTextField(
                            value = kategoriPengeluaran,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            kategoriList.forEach { kategori ->
                                DropdownMenuItem(
                                    text = { Text(kategori, fontFamily = Poppins) },
                                    onClick = {
                                        kategoriPengeluaran = kategori
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (kategoriPengeluaran == "Lainnya") {
                        Spacer(modifier = Modifier.height(8.dp))
                        FormField(label = "Nama Kategori Lainnya") {
                            StandardTextField(
                                value = kategoriLainnya,
                                onValueChange = { kategoriLainnya = it },
                                placeholder = "Masukkan nama kategori"
                            )
                        }
                    }
                } else {
                    // Dropdown kategori: Usaha, Lainnya (untuk Pemasukan)
                    var expanded by remember { mutableStateOf(false) }
                    val kategoriList = listOf("Usaha", "Lainnya")
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StandardTextField(
                            value = kategoriPemasukan,
                    onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            kategoriList.forEach { kategori ->
                                DropdownMenuItem(
                                    text = { Text(kategori, fontFamily = Poppins) },
                                    onClick = {
                                        kategoriPemasukan = kategori
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (kategoriPemasukan == "Lainnya") {
                        Spacer(modifier = Modifier.height(8.dp))
                        FormField(label = "Nama Kategori Lainnya") {
                            StandardTextField(
                                value = kategoriPemasukanLainnya,
                                onValueChange = { kategoriPemasukanLainnya = it },
                                placeholder = "Masukkan nama kategori"
                            )
                        }
                    }
                }
            }

            FormField(label = "Deskripsi") {
                MultilineTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    placeholder = "Masukkan deskripsi transaksi..."
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.weight(1f))

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

                    // Tentukan kategori yang akan dikirim
                    val kategoriFinal = if (selectedType == TransactionType.EXPENSE) {
                        if (kategoriPengeluaran == "Lainnya") kategoriLainnya else kategoriPengeluaran
                    } else {
                        if (kategoriPemasukan == "Lainnya") kategoriPemasukanLainnya else "Pendapatan ${kategoriPemasukan}"
                    }

                    // Simpan transaksi
                    transactionViewModel.saveTransaction(
                        type = if (selectedType == TransactionType.INCOME) "INCOME" else "EXPENSE",
                        amount = amount,
                        date = tanggal,
                        description = deskripsi,
                        category = kategoriFinal
                    )

                    // Reset form jika berhasil
                    if (transactionViewModel.error.value == null) {
                        tanggal = ""
                        nominal = ""
                        deskripsi = ""
                        kategoriPengeluaran = "Usaha"
                        kategoriLainnya = ""
                        kategoriPemasukan = "Usaha"
                        kategoriPemasukanLainnya = ""
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
        TransactionSuccessDialog(
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
            .height(48.dp),
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
fun TransactionSuccessDialog(onDismiss: () -> Unit) {
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

