package com.example.omsetku.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.components.ReportFilterDialog
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.PrimaryColor
import com.example.omsetku.ui.theme.PrimaryLight
import com.example.omsetku.ui.theme.ExpenseLightColor
import com.example.omsetku.viewmodels.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.omsetku.models.Transaction
import androidx.compose.ui.graphics.vector.ImageVector

enum class FilterPeriode {
    HARIAN, MINGGUAN, BULANAN, TAHUNAN
}

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val amount: Long = 0L,
    val date: Long = 0L,
    val category: String = "",
    val description: String = "",
    val createdAt: Long = 0L
)

fun formatRupiah(value: Int): String {
    return "Rp " + String.format("%,d", value).replace(',', '.')
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("Report") }
    val scrollState = rememberScrollState()
    var showFilterDialog by remember { mutableStateOf(false) }

    // Ambil transaksi dari ViewModel
    val transactions by transactionViewModel.transactions.collectAsState()

    LocalContext.current

    // State dan CoroutineScope untuk Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    rememberCoroutineScope()

    // Effect untuk memuat data transaksi saat screen dibuka
    LaunchedEffect(key1 = Unit) {
        try {
            // Muat transaksi untuk periode default (misal: Hari ini)
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
            val todayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(
                Calendar.MINUTE,
                0
            ); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val todayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(
                Calendar.SECOND,
                59
            ); set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            transactionViewModel.loadTransactions(startDate = todayStart, endDate = todayEnd)
        } catch (e: Exception) {
            // Tangkap exception disini untuk mencegah crash
        }
    }

    // Hitung Pendapatan Usaha (Kasir)
    val pendapatanUsaha = transactions.filter {
        it.type.equals("INCOME", true) && (it.description.contains(
            "Penjualan",
            true
        ) || it.description.contains("Kasir", true))
    }.sumOf { it.amount }

    // Hitung Pendapatan Lainnya (Transaksi Manual INCOME selain Penjualan)
    val pendapatanLainnya = transactions.filter {
        it.type.equals("INCOME", true) && !(it.description.contains(
            "Penjualan",
            true
        ) || it.description.contains("Kasir", true))
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
    val pajak_umkm = if (laba_kotor > 0) (laba_kotor * 0.005).toInt() else 0
    val laba_bersih = laba_kotor - pajak_umkm

    val insightAI by transactionViewModel.insightAI.collectAsState()
    val isLoadingInsightAI by transactionViewModel.isLoadingInsightAI.collectAsState()
    val errorInsightAI by transactionViewModel.errorInsightAI.collectAsState()
    var isInsightRequested by remember { mutableStateOf(false) }
    
    // Trigger state untuk AI Insight
    var aiInsightTrigger by remember { mutableStateOf(0) }

    // --- Bagian Filter, Download, Ringkasan (KEMBALIKAN) ---
    // Menggunakan tanggal sekarang sebagai default
    val today = Calendar.getInstance()
    val defaultDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }
    val defaultMonthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }
    val defaultYearFormat = SimpleDateFormat("yyyy", Locale("id", "ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }

    // Default text awal bulan ini sampai akhir bulan
    Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }
    Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }

    var periodeText by remember {
        mutableStateOf(defaultDateFormat.format(today.time))
    }

    // State untuk filter dialog
    var selectedPeriode by remember { mutableStateOf(FilterPeriode.HARIAN) }

    // State untuk nilai tanggal dari dialog

    // State untuk bottom sheet
    rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                        "Report" -> { /* Sudah di layar Report */
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Konten utama
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
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
                            .offset(y = (-2).dp),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Filter dan Download Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tombol Filter
                    Button(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier
                            .weight(0.3f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showFilterDialog) Color.White else PrimaryVariant
                        ),
                        border = if (showFilterDialog) BorderStroke(1.dp, PrimaryVariant) else null
                    ) {
                        Text(
                            "Filter",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = if (showFilterDialog) PrimaryVariant else Color.White
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
                            .height(48.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

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
                                text = formatRupiah(total_pendapatan.toInt()),
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
                                text = formatRupiah(total_beban.toInt()),
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
                            text = "Laba (Rugi) Bersih",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupiah(laba_bersih.toInt()),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color(0xFF2F7E68)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- LAPORAN KEUANGAN DIGITAL (DUA KOLOM) dibungkus Card putih, rounded, shadow, margin ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        // Section: Pemasukan
                        SectionHeader(title = "Pemasukan", icon = Icons.Default.TrendingUp, accentColor = PrimaryColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        var pendapatanUsahaExpanded by remember { mutableStateOf(false) }
                        var pendapatanLainnyaExpanded by remember { mutableStateOf(false) }
                        RowItem(
                            icon = null,
                            label = "Pendapatan Usaha",
                            value = formatRupiah(pendapatanUsaha.toInt()),
                            expanded = pendapatanUsahaExpanded,
                            onClick = { pendapatanUsahaExpanded = !pendapatanUsahaExpanded },
                            accentColor = PrimaryColor,
                            showArrow = true
                        )
                        AnimatedVisibility(
                            visible = pendapatanUsahaExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            DetailList(transactions.filter {
                                it.type.equals("INCOME", true) && (it.description.contains("Penjualan", true) || it.description.contains("Kasir", true))
                            })
                        }
                        RowItem(
                            icon = null,
                            label = "Pendapatan Lainnya",
                            value = formatRupiah(pendapatanLainnya.toInt()),
                            expanded = pendapatanLainnyaExpanded,
                            onClick = { pendapatanLainnyaExpanded = !pendapatanLainnyaExpanded },
                            accentColor = PrimaryColor,
                            showArrow = true
                        )
                        AnimatedVisibility(
                            visible = pendapatanLainnyaExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            DetailList(transactions.filter {
                                it.type.equals("INCOME", true) && !(it.description.contains("Penjualan", true) || it.description.contains("Kasir", true))
                            })
                        }
                        TotalRow(label = "Total Pemasukan", value = formatRupiah(total_pendapatan.toInt()), accentColor = PrimaryColor)
                        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        // Section: Pengeluaran
                        SectionHeader(title = "Pengeluaran", icon = Icons.Default.TrendingDown, accentColor = Color(0xFFE74C3C))
                        Spacer(modifier = Modifier.height(8.dp))
                        var bebanUsahaExpanded by remember { mutableStateOf(false) }
                        var bebanLainnyaExpanded by remember { mutableStateOf(false) }
                        RowItem(
                            icon = null,
                            label = "Beban Usaha",
                            value = formatRupiah(bebanUsaha.toInt()),
                            expanded = bebanUsahaExpanded,
                            onClick = { bebanUsahaExpanded = !bebanUsahaExpanded },
                            accentColor = Color.Black,
                            showArrow = true
                        )
                        AnimatedVisibility(
                            visible = bebanUsahaExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            DetailList(transactions.filter {
                                it.type.equals("EXPENSE", true) && it.category.equals("Usaha", true)
                            })
                        }
                        RowItem(
                            icon = null,
                            label = "Beban Lainnya",
                            value = formatRupiah(bebanLainnya.toInt()),
                            expanded = bebanLainnyaExpanded,
                            onClick = { bebanLainnyaExpanded = !bebanLainnyaExpanded },
                            accentColor = Color.Black,
                            showArrow = true
                        )
                        AnimatedVisibility(
                            visible = bebanLainnyaExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            DetailList(transactions.filter {
                                it.type.equals("EXPENSE", true) && !it.category.equals("Usaha", true)
                            })
                        }
                        TotalRow(label = "Total Pengeluaran", value = formatRupiah(total_beban.toInt()), accentColor = Color.Black)
                        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        // Section: Laba (Rugi)
                        SectionHeader(title = "Laba (Rugi)", icon = Icons.Default.BarChart, accentColor = PrimaryVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            TotalRow(label = "Laba (Rugi) Kotor", value = formatRupiah(laba_kotor.toInt()), accentColor = PrimaryVariant)
                            TotalRow(label = "Pajak Penghasilan UMKM (0,5%)", value = formatRupiah(pajak_umkm.toInt()), accentColor = PrimaryVariant)
                            TotalRow(label = "Laba (Rugi) Bersih", value = formatRupiah(laba_bersih.toInt()), accentColor = PrimaryVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Insight Section - moved to bottom
                AIInsightSection(
                    insightAI = insightAI,
                    isLoadingInsightAI = isLoadingInsightAI,
                    errorInsightAI = errorInsightAI,
                    onGetAIInsight = {
                        isInsightRequested = true
                        aiInsightTrigger++ // Trigger LaunchedEffect
                    }
                )

                // LaunchedEffect untuk AI Insight di level Composable
                LaunchedEffect(aiInsightTrigger) {
                    if (aiInsightTrigger > 0) {
                        val rincianPendapatan = transactions.filter { it.type.equals("INCOME", true) }
                            .groupBy { it.description }
                            .map { (desc, items) -> desc to items.sumOf { it.amount.toInt() } }
                        val rincianPengeluaran = transactions.filter { it.type.equals("EXPENSE", true) }
                            .groupBy { it.description }
                            .map { (desc, items) -> desc to items.sumOf { it.amount.toInt() } }
                        
                        transactionViewModel.getAIInsightReport(
                            totalPendapatan = total_pendapatan.toInt(),
                            totalPengeluaran = total_beban.toInt(),
                            labaKotor = laba_kotor.toInt(),
                            pajakUmkm = pajak_umkm.toInt(),
                            labaBersih = laba_bersih.toInt(),
                            rincianPendapatan = rincianPendapatan,
                            rincianPengeluaran = rincianPengeluaran
                        )
                    }
                }

                // Tambahkan Spacer di akhir agar tidak overlap dengan navbar
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Filter Dialog
            if (showFilterDialog) {
                ReportFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    selectedPeriode = selectedPeriode,
                    onApplyFilter = { result ->
                        selectedPeriode = result.periode
                        periodeText = result.displayText
                        showFilterDialog = false
                        // ... logic filter tanggal seperti sebelumnya ...
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                        val dateFormat =
                            SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).apply {
                                timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                            }
                        var startTimestamp: Long? = null
                        var endTimestamp: Long? = null
                        when (result.periode) {
                            FilterPeriode.HARIAN -> {
                                try {
                                    val date = dateFormat.parse(result.selectedDate)
                                    val calendar =
                                        Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                                            .apply {
                                                time = date ?: Date()
                                                set(Calendar.HOUR_OF_DAY, 23)
                                                set(Calendar.MINUTE, 59)
                                                set(Calendar.SECOND, 59)
                                                set(Calendar.MILLISECOND, 999)
                                            }
                                    startTimestamp = date?.time
                                    endTimestamp = calendar.timeInMillis
                                } catch (e: Exception) {
                                }
                            }

                            FilterPeriode.MINGGUAN -> {
                                try {
                                    val startDate = dateFormat.parse(result.startDate)
                                    val endDate = dateFormat.parse(result.endDate)
                                    startTimestamp = startDate?.time
                                    endTimestamp = endDate?.time
                                } catch (e: Exception) {
                                }
                            }

                            FilterPeriode.BULANAN -> {
                                try {
                                    val monthYearFormat = SimpleDateFormat(
                                        "MMMM yyyy",
                                        Locale("id", "ID")
                                    ).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
                                    val date = monthYearFormat.parse(result.selectedMonth)
                                    calendar.time = date ?: Date()
                                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                                    startTimestamp = calendar.timeInMillis
                                    calendar.set(
                                        Calendar.DAY_OF_MONTH,
                                        calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                    )
                                    endTimestamp = calendar.timeInMillis
                                } catch (e: Exception) {
                                }
                            }

                            FilterPeriode.TAHUNAN -> {
                                try {
                                    val yearFormat = SimpleDateFormat(
                                        "yyyy",
                                        Locale("id", "ID")
                                    ).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
                                    val date = yearFormat.parse(result.selectedYear)
                                    calendar.time = date ?: Date()
                                    calendar.set(Calendar.DAY_OF_YEAR, 1)
                                    startTimestamp = calendar.timeInMillis
                                    calendar.set(
                                        Calendar.DAY_OF_YEAR,
                                        calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                                    )
                                    endTimestamp = calendar.timeInMillis
                                } catch (e: Exception) {
                                }
                            }
                        }
                        if (startTimestamp != null && endTimestamp != null) {
                            transactionViewModel.loadTransactions(
                                startDate = startTimestamp,
                                endDate = endTimestamp
                            )
                        }
                    }
                )
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
fun AIInsightSection(
    insightAI: com.example.omsetku.data.AIPricingService.AIInsightResult?,
    isLoadingInsightAI: Boolean,
    errorInsightAI: String?,
    onGetAIInsight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryLight
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header dengan icon AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Insight",
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Insight Laporan AI",
                    fontFamily = Poppins,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoadingInsightAI) {
                // Loading state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Menganalisis laporan dengan AI...",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = PrimaryColor
                    )
                }
            } else if (errorInsightAI != null) {
                // Error state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorInsightAI,
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }
                }
            } else if (insightAI != null) {
                // AI Insight content
                AIInsightContent(insightAI = insightAI)
            } else {
                // Get AI insight button
                Button(
                    onClick = onGetAIInsight,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dapatkan Insight AI",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AIInsightContent(
    insightAI: com.example.omsetku.data.AIPricingService.AIInsightResult,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(12.dp))

        // Expandable sections
        var showDetails by remember { mutableStateOf(false) }

        Button(
            onClick = { showDetails = !showDetails },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryVariant
            ),
            border = BorderStroke(1.dp, PrimaryColor)
        ) {
            Text(
                text = if (showDetails) "Sembunyikan Detail" else "Lihat Detail Insight",
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = PrimaryColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedVisibility(
            visible = showDetails,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                // Highlight
                if (insightAI.highlight.isNotBlank()) {
                    InsightDetailSection(
                        title = "Highlight",
                        content = insightAI.highlight,
                        icon = Icons.Default.Lightbulb,
                        color = PrimaryLight
                    )
                }

                // Saran
                if (insightAI.saran.isNotBlank()) {
                    InsightDetailSection(
                        title = "Saran",
                        content = insightAI.saran,
                        icon = Icons.Default.TipsAndUpdates,
                        color = PrimaryVariant
                    )
                }

                // Narasi
                if (insightAI.narasi.isNotBlank()) {
                    InsightDetailSection(
                        title = "Narasi",
                        content = insightAI.narasi,
                        icon = Icons.Default.Description,
                        color = ExpenseLightColor
                    )
                }

                // Anomali
                if (insightAI.anomali.isNotBlank()) {
                    InsightDetailSection(
                        title = "Anomali Terdeteksi",
                        content = insightAI.anomali,
                        icon = Icons.Default.Warning,
                        color = Color(0xFFFFEBEE),
                        textColor = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightDetailSection(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    textColor: Color = Color.DarkGray,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = textColor,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun RowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    label: String,
    value: String,
    expanded: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    showArrow: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.End
        )
        if (showArrow) {
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TotalRow(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun DetailList(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        transactions.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = it.description,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = formatRupiah(it.amount.toInt()),
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}