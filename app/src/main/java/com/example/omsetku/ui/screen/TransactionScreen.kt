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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.navigation.Routes
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.zIndex
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border

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

            // Sliding green indicator tab
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                val tabCount = 2
                val selectedIndex = if (selectedType == TransactionType.INCOME) 0 else 1
                val tabWidth = maxWidth / tabCount
                val indicatorOffset = tabWidth * selectedIndex
                val animatedOffset by animateDpAsState(
                    targetValue = indicatorOffset,
                    animationSpec = tween(durationMillis = 300), label = "indicator"
                )
                // Sliding green indicator (background only)
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF5ED0C5))
                        .zIndex(1f)
                )
                // Teks tab di atas indikator
                Row(modifier = Modifier.fillMaxSize().zIndex(2f)) {
                    // Tab Pemasukan
                    val pemasukanActive = selectedType == TransactionType.INCOME
                    val pemasukanTextColor by animateColorAsState(
                        targetValue = if (pemasukanActive) Color.White else Color(0xFF5ED0C5),
                        animationSpec = tween(durationMillis = 1000),
                        label = "pemasukanTextColor"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedType = TransactionType.INCOME },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pemasukan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = pemasukanTextColor,
                            maxLines = 1,
                            modifier = if (pemasukanActive) Modifier.zIndex(3f) else Modifier
                        )
                    }
                    // Tab Pengeluaran
                    val pengeluaranActive = selectedType == TransactionType.EXPENSE
                    val pengeluaranTextColor by animateColorAsState(
                        targetValue = if (pengeluaranActive) Color.White else Color(0xFF5ED0C5),
                        animationSpec = tween(durationMillis = 1000),
                        label = "pengeluaranTextColor"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedType = TransactionType.EXPENSE },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pengeluaran",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = pengeluaranTextColor,
                            maxLines = 1,
                            modifier = if (pengeluaranActive) Modifier.zIndex(3f) else Modifier
                        )
                    }
                }
                // Border outline
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, Color(0xFF5ED0C5)), RoundedCornerShape(8.dp))
                        .zIndex(4f)
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
                            0L
                        } else {
                            cleanNominal.toLong()
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
                        date = System.currentTimeMillis(),
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
fun AnimatedTabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF5ED0C5) else Color.White,
        label = "tabBgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF5ED0C5),
        label = "tabTextColor"
    )
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        elevation = null,
        border = BorderStroke(1.dp, Color(0xFF5ED0C5))
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            maxLines = 1
        )
    }
}

@Composable
fun TransactionSuccessDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Text(
                    text = "Transaksi Berhasil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Transaksi anda telah tercatat",
                    fontSize = 15.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

