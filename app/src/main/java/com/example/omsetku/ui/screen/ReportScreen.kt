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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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

enum class FilterPeriode {
    HARIAN, MINGGUAN, BULANAN, TAHUNAN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Report") }
    val scrollState = rememberScrollState()
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // Menggunakan tanggal sekarang sebagai default
    val today = Calendar.getInstance()
    val defaultDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val defaultMonthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    val defaultYearFormat = SimpleDateFormat("yyyy", Locale("id", "ID"))
    
    // Default text awal bulan ini sampai akhir bulan
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val lastDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }
    
    var periodeText by remember { 
        mutableStateOf("${defaultDateFormat.format(firstDayOfMonth.time)} - ${defaultDateFormat.format(lastDayOfMonth.time)}") 
    }
    
    // State untuk filter dialog
    var selectedPeriode by remember { mutableStateOf(FilterPeriode.BULANAN) }
    
    // State untuk nilai tanggal dari dialog
    var selectedDate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf(defaultMonthFormat.format(today.time)) }
    var selectedYear by remember { mutableStateOf(defaultYearFormat.format(today.time)) }

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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
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
            
            Spacer(modifier = Modifier.height(8.dp))

            // Tanggal periode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.transactioncalender),
                    contentDescription = "Tanggal",
                    modifier = Modifier.size(18.dp),
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
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
                
                // Tombol Download Laporan
                Button(
                    onClick = { /* TODO: Implementasi download laporan */ },
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
                        fontWeight = FontWeight.Medium,
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
                            text = "Rp 103.193.000",
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
                            text = "Rp 7.902.646",
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
                        text = "Rp 500.000",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF2F7E68)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tabel Laporan Keuangan
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFFF5F5F5)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header tabel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Deskripsi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Gray
                        )
                        
                        Text(
                            text = "Saldo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Gray
                        )
                    }
                    
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    
                    // Laba kotor dengan highlight background
                    Surface(
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Laba Kotor",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                            
                            Text(
                                text = "Rp 7.500.000",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                        }
                    }
                    
                    // Judul Biaya Operasional
                    Text(
                        text = "Biaya Operasional",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                    
                    // Item biaya operasional
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sewa",
                            fontSize = 13.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        
                        Text(
                            text = "Rp 2.000.000",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Gaji Karyawan",
                            fontSize = 13.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        
                        Text(
                            text = "Rp 4.500.000",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Listrik & Air",
                            fontSize = 13.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        
                        Text(
                            text = "Rp 500.000",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                    
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    
                    // Total Biaya Operasional dengan highlight background
                    Surface(
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Biaya Operasional",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                            
                            Text(
                                text = "Rp 7.000.000",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                        }
                    }
                    
                    Divider(thickness = 1.dp, color = Color.LightGray)
                    
                    // Laba Bersih dengan highlight background
                    Surface(
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Laba Bersih",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                            
                            Text(
                                text = "Rp 500.000",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                        }
                    }
                    
                    // Pajak dengan padding yang sama dengan item biaya
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pajak Penghasilan UMKM (0,5%)",
                            fontSize = 13.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        
                        Text(
                            text = "Rp 2.500",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                    
                    // Laba Bersih setelah pajak dengan highlight background
                    Surface(
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Laba Bersih setelah Pajak",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                            
                            Text(
                                text = "Rp 497.500",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

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
                                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                val parsedDate = dateFormat.parse(date)
                                startDateTimestamp = parsedDate?.time
                                
                                // Otomatis mengatur tanggal akhir seminggu setelah tanggal awal
                                if (startDateTimestamp != null) {
                                    val calendar = Calendar.getInstance()
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
                
                // Menambahkan spasi sebelum tombol Terapkan
                Spacer(modifier = Modifier.height(32.dp))
                
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