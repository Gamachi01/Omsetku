package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.components.DatePickerField
import com.example.omsetku.ui.components.DatePickerMode
import java.text.SimpleDateFormat
import java.util.*
import com.example.omsetku.ui.components.ReportFilterDialog
import com.example.omsetku.ui.components.FilterResult
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.omsetku.viewmodels.TransactionViewModel
import com.example.omsetku.firebase.FirestoreRepository
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.net.Uri
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

enum class FilterPeriode {
    HARIAN, MINGGUAN, BULANAN, TAHUNAN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, transactionViewModel: TransactionViewModel = viewModel()) {
    var selectedItem by remember { mutableStateOf("Report") }
    val scrollState = rememberScrollState()
    var showFilterDialog by remember { mutableStateOf(false) }

    // Ambil transaksi dari ViewModel
    val transactions by transactionViewModel.transactions.collectAsState()

    val context = LocalContext.current

    // State dan CoroutineScope untuk Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Effect untuk memuat data transaksi saat screen dibuka
    LaunchedEffect(key1 = Unit) {
        try {
            // Muat transaksi untuk periode default (misal: Hari ini)
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
            val todayStart = calendar.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
            val todayEnd = calendar.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999) }.timeInMillis

            transactionViewModel.loadTransactions(startDate = todayStart, endDate = todayEnd)
        } catch (e: Exception) {
            // Tangkap exception disini untuk mencegah crash
        }
    }

    // Hitung Pendapatan Usaha (Kasir)
    val pendapatanUsaha = transactions.filter {
        it.type.equals("INCOME", true) && (it.description.contains("Penjualan", true) || it.description.contains("Kasir", true))
    }.sumOf { it.amount }

    // Hitung Pendapatan Lainnya (Transaksi Manual INCOME selain Penjualan)
    val pendapatanLainnya = transactions.filter {
        it.type.equals("INCOME", true) && !(it.description.contains("Penjualan", true) || it.description.contains("Kasir", true))
    }.sumOf { it.amount }

    // Beban Usaha (sekarang menggunakan kategori "Usaha" untuk tipe EXPENSE)
    val bebanUsaha = transactions.filter {
        it.type.equals("EXPENSE", true) && it.category.equals("Usaha", true)
    }.sumOf { it.amount }

    // Beban Lainnya (Transaksi Manual EXPENSE SELAIN kategori "Usaha")
    val bebanLainnya = transactions.filter {
        it.type.equals("EXPENSE", true) && !it.category.equals("Usaha", true)
    }.sumOf { it.amount }

    val total_pendapatan = pendapatanUsaha + pendapatanLainnya
    val total_beban = bebanUsaha + bebanLainnya
    val laba_kotor = total_pendapatan - total_beban
    val pajak_umkm = (laba_kotor * 0.005).toInt()
    val laba_bersih = laba_kotor - pajak_umkm

    fun formatRupiah(value: Int): String {
        return "Rp " + String.format("%,d", value).replace(',', '.')
    }

    // --- Bagian Filter, Download, Ringkasan (KEMBALIKAN) ---
    // Menggunakan tanggal sekarang sebagai default
    val today = Calendar.getInstance()
    val defaultDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
    val defaultMonthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
    val defaultYearFormat = SimpleDateFormat("yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }

    // Default text awal bulan ini sampai akhir bulan
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val lastDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }

    var periodeText by remember {
        mutableStateOf(defaultDateFormat.format(today.time))
    }

    // State untuk filter dialog
    var selectedPeriode by remember { mutableStateOf(FilterPeriode.HARIAN) }

    // State untuk nilai tanggal dari dialog
    var selectedDate by remember { mutableStateOf(defaultDateFormat.format(today.time)) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf(defaultMonthFormat.format(today.time)) }
    var selectedYear by remember { mutableStateOf(defaultYearFormat.format(today.time)) }

    // --- END Bagian Filter, Download, Ringkasan ---

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                fontFamily = Poppins,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Tanggal periode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.transactioncalender),
                    contentDescription = "Tanggal",
                    modifier = Modifier
                        .size(18.dp)
                        .offset(y = (-2.5).dp),
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = periodeText,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter dan Download Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Filter
                OutlinedButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier
                        .weight(0.3f)
                        .height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryVariant
                    ),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(
                        "Filter",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }

                // Tombol Download Laporan
                Button(
                    onClick = {
                        // Memicu Activity Result Launcher untuk memilih lokasi dan nama file
                        // Karena launcher dikomentari, tombol ini tidak akan melakukan apa-apa dulu.
                        // Anda bisa tambahkan Toast sementara di sini jika perlu.
                        // val fileName = "Laporan_Keuangan_${periodeText.replace(" ", "_")}.csv"
                        // createDocumentLauncher.launch(fileName)

                        // TODO: Implementasi download laporan (saat build environment sudah stabil)

                    },
                    modifier = Modifier
                        .weight(0.7f)
                        .height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryVariant
                    )
                ) {
                    Text(
                        "Download Laporan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }

            // Filter Dialog
            if (showFilterDialog) {
                ReportFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    selectedPeriode = selectedPeriode,
                    onApplyFilter = { result ->
                        // Memperbarui periode dan text berdasarkan hasil filter
                        selectedPeriode = result.periode
                        periodeText = result.displayText
                        showFilterDialog = false

                        // Hitung tanggal awal dan akhir berdasarkan hasil filter
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
                        var startTimestamp: Long? = null
                        var endTimestamp: Long? = null

                        when (result.periode) {
                            FilterPeriode.HARIAN -> {
                                try {
                                    val date = dateFormat.parse(result.selectedDate)
                                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta")).apply {
                                        time = date ?: Date() // Set ke tanggal yang dipilih (awal hari)
                                        // Set waktu ke akhir hari (23:59:59.999)
                                        set(Calendar.HOUR_OF_DAY, 23)
                                        set(Calendar.MINUTE, 59)
                                        set(Calendar.SECOND, 59)
                                        set(Calendar.MILLISECOND, 999)
                                    }
                                    startTimestamp = date?.time // Tanggal awal tetap awal hari
                                    endTimestamp = calendar.timeInMillis // Tanggal akhir menjadi akhir hari
                                } catch (e: Exception) { /* Handle error */ }
                            }
                            FilterPeriode.MINGGUAN -> {
                                try {
                                    val startDate = dateFormat.parse(result.startDate)
                                    val endDate = dateFormat.parse(result.endDate)
                                    startTimestamp = startDate?.time
                                    endTimestamp = endDate?.time // Akhir hari terakhir
                                } catch (e: Exception) { /* Handle error */ }
                            }
                            FilterPeriode.BULANAN -> {
                                try {
                                    // result.selectedMonth dalam format MMMM yyyy
                                    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
                                    val date = monthYearFormat.parse(result.selectedMonth)
                                    calendar.time = date ?: Date()
                                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                                    startTimestamp = calendar.timeInMillis
                                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                                    endTimestamp = calendar.timeInMillis
                                } catch (e: Exception) { /* Handle error */ }
                            }
                            FilterPeriode.TAHUNAN -> {
                                try {
                                    // result.selectedYear dalam format yyyy
                                    val yearFormat = SimpleDateFormat("yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
                                    val date = yearFormat.parse(result.selectedYear)
                                    calendar.time = date ?: Date()
                                    calendar.set(Calendar.DAY_OF_YEAR, 1) // Awal tahun
                                    startTimestamp = calendar.timeInMillis
                                    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) // Akhir tahun
                                    endTimestamp = calendar.timeInMillis
                                } catch (e: Exception) { /* Handle error */ }
                            }
                        }

                        // Panggil loadTransactions dengan tanggal yang baru
                        if (startTimestamp != null && endTimestamp != null) {
                            transactionViewModel.loadTransactions(startDate = startTimestamp, endDate = endTimestamp)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cards untuk Total Pendapatan dan Pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Total Pendapatan
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F7F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
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
                            text = formatRupiah(total_pendapatan),
                            fontSize = 16.sp,
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
                        .height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFDEDED)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
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
                            text = formatRupiah(total_beban),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color(0xFFE74C3C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Laba Bersih
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F7F5)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
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
                        text = formatRupiah(laba_bersih),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF2F7E68)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LAPORAN KEUANGAN DIGITAL (DUA KOLOM) dibungkus Card putih, rounded, shadow, margin ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)) {
                    // Section 1: Pemasukan
                    Text(
                        text = "Pemasukan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pendapatan Usaha", fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(pendapatanUsaha), fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pendapatan Lainnya", fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(pendapatanLainnya), fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pemasukan", fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(total_pendapatan), fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Section 2: Pengeluaran
                    Text(
                        text = "Pengeluaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Beban Usaha", fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(bebanUsaha), fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Beban Lainnya", fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(bebanLainnya), fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pengeluaran", fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(total_beban), fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 3: Laba (Rugi) Kotor
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Laba (Rugi) Kotor", fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(laba_kotor), fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 4: Pajak UMKM
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pajak Penghasilan UMKM (0,5%)", fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(pajak_umkm), fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 5: Laba (Rugi) Bersih
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Laba (Rugi) Bersih", fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black)
                        Text(formatRupiah(laba_bersih), fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Color.Black, textAlign = TextAlign.End)
                    }
                }
            }
        }
    }
}

// Fungsi placeholder untuk menyimpan data CSV
// Tambahkan parameter untuk nilai ringkasan
// COMMENT OUT THIS ENTIRE FUNCTION
/*
fun saveCsvToFile(
    context: Context,
    uri: Uri,
    transactions: List<com.example.omsetku.data.Transaction>,
    periodeText: String,
    totalPendapatan: Int,
    totalPengeluaran: Int,
    labaKotor: Int,
    pajakUmkm: Int,
    labaBersih: Int,
    onFileSaved: (Boolean) -> Unit // Callback untuk memberi tahu Composable hasil penyimpanan
) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val writer = outputStream.bufferedWriter()

            // Tulis Header Laporan
            writer.write("Laporan Keuangan")
            writer.newLine()
            writer.write("Periode: ${periodeText}")
            writer.newLine()
            writer.newLine()

            // Tulis Ringkasan Laporan
            writer.write("Ringkasan Laporan")
            writer.newLine()
            writer.write("Total Pendapatan,${totalPendapatan}")
            writer.newLine()
            writer.write("Total Pengeluaran,${totalPengeluaran}")
            writer.newLine()
            writer.write("Laba Kotor,${labaKotor}")
            writer.newLine()
            writer.write("Pajak UMKM (0.5%),${pajakUmkm}")
            writer.newLine()
            writer.write("Laba Bersih,${labaBersih}")
            writer.newLine()
            writer.newLine()

            // Tulis Header Tabel Transaksi
            writer.write("Tipe,Deskripsi,Nominal,Tanggal,Kategori")
            writer.newLine()

            // Tulis Data Transaksi
            transactions.forEach {
                // Hapus koma di deskripsi/kategori agar tidak merusak CSV
                val cleanedDescription = it.description.replace(",", "")
                val cleanedCategory = it.category.replace(",", "")
                writer.write("${it.type},${cleanedDescription},${it.amount},${it.date},${cleanedCategory}")
                writer.newLine()
            }

            writer.flush()
        }
        onFileSaved(true) // Beri tahu Composable bahwa penyimpanan berhasil
    } catch (e: Exception) {
        e.printStackTrace()
        onFileSaved(false) // Beri tahu Composable bahwa penyimpanan gagal
    }
}
*/

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

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    selectedPeriode: FilterPeriode,
    onPeriodeSelected: (FilterPeriode) -> Unit,
    onApply: () -> Unit,
    selectedDate: String,
    startDate: String,
    endDate: String,
    selectedMonth: String,
    selectedYear: String,
    onSelectedDateChanged: (String) -> Unit,
    onStartDateChanged: (String) -> Unit,
    onEndDateChanged: (String) -> Unit,
    onSelectedMonthChanged: (String) -> Unit,
    onSelectedYearChanged: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // State untuk menyimpan timestamp tanggal awal (untuk mode mingguan)
    var startDateTimestamp by remember { mutableStateOf<Long?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.heightIn(max = 550.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Header dengan tombol close (X)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() },
                        tint = Color.Black
                    )

                    Text(
                        text = "Filter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = Poppins
                    )

                    // Spacer untuk menjaga agar title tetap di tengah
                    Spacer(modifier = Modifier.width(24.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pilih Periode Transaksi
                Text(
                    text = "Pilih Periode Transaksi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Radio button options
                val options = listOf(
                    FilterPeriode.HARIAN to "Harian",
                    FilterPeriode.MINGGUAN to "Mingguan",
                    FilterPeriode.BULANAN to "Bulanan",
                    FilterPeriode.TAHUNAN to "Tahunan"
                )

                var selectedOption by remember { mutableStateOf(selectedPeriode) }

                // Radio button group
                options.forEach { (periode, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedOption = periode
                                onPeriodeSelected(periode)
                            }
                    ) {
                        RadioButton(
                            selected = selectedOption == periode,
                            onClick = {
                                selectedOption = periode
                                onPeriodeSelected(periode)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryVariant,
                                unselectedColor = Color.Gray
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input fields based on selected period
                when (selectedOption) {
                    FilterPeriode.HARIAN -> {
                        Text(
                            text = "Pilih Tanggal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontFamily = Poppins
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Menggunakan DatePickerField dengan mode DAILY
                        DatePickerField(
                            value = selectedDate,
                            onDateSelected = { onSelectedDateChanged(it) },
                            placeholder = "Pilih tanggal",
                            mode = DatePickerMode.DAILY
                        )
                    }
                    FilterPeriode.MINGGUAN -> {
                        // Tanggal Awal
                        Text(
                            text = "Tanggal Awal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontFamily = Poppins
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Menggunakan DatePickerField dengan mode WEEKLY_START
                        DatePickerField(
                            value = startDate,
                            onDateSelected = { date ->
                                onStartDateChanged(date)
                                // Menyimpan timestamp untuk digunakan pada tanggal akhir
                                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
                                val parsedDate = dateFormat.parse(date)
                                startDateTimestamp = parsedDate?.time

                                // Otomatis mengatur tanggal akhir seminggu setelah tanggal awal
                                if (startDateTimestamp != null) {
                                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                                    calendar.timeInMillis = startDateTimestamp!!
                                    calendar.add(Calendar.DAY_OF_MONTH, 7)
                                    onEndDateChanged(dateFormat.format(calendar.time))
                                }
                            },
                            placeholder = "Pilih tanggal awal",
                            mode = DatePickerMode.WEEKLY_START
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tanggal Akhir
                        Text(
                            text = "Tanggal Akhir",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontFamily = Poppins
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Menggunakan DatePickerField dengan mode WEEKLY_END
                        // Tanggal akhir otomatis diisi seminggu setelah tanggal awal
                        DatePickerField(
                            value = endDate,
                            onDateSelected = { onEndDateChanged(it) },
                            placeholder = "Tanggal akhir (otomatis seminggu setelah tanggal awal)",
                            mode = DatePickerMode.WEEKLY_END,
                            startDate = startDateTimestamp
                        )
                    }
                    FilterPeriode.BULANAN -> {
                        Text(
                            text = "Pilih Bulan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontFamily = Poppins
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Menggunakan DatePickerField dengan mode MONTHLY
                        DatePickerField(
                            value = selectedMonth,
                            onDateSelected = { onSelectedMonthChanged(it) },
                            placeholder = "Pilih bulan",
                            mode = DatePickerMode.MONTHLY
                        )
                    }
                    FilterPeriode.TAHUNAN -> {
                        Text(
                            text = "Pilih Tahun",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontFamily = Poppins
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Menggunakan DatePickerField dengan mode YEARLY
                        DatePickerField(
                            value = selectedYear,
                            onDateSelected = { onSelectedYearChanged(it) },
                            placeholder = "Pilih tahun",
                            mode = DatePickerMode.YEARLY
                        )
                    }
                }

                // Tombol Terapkan
                Button(
                    onClick = {
                        onApply()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryVariant
                    )
                ) {
                    Text(
                        text = "Terapkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
}