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

enum class FilterPeriode {
    HARIAN, MINGGUAN, BULANAN, TAHUNAN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Report") }
    val scrollState = rememberScrollState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var periodeText by remember { mutableStateOf("01 Maret 2025 - 31 Maret 2025") }
    var selectedPeriode by remember { mutableStateOf(FilterPeriode.BULANAN) }

    Scaffold(
        topBar = {
            OmsetkuTopBar(
                title = "Laporan Keuangan",
                onBackClick = { navController.navigateUp() }
            )
        },
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
            // Tanggal periode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                    )
                )
            }
            
            // Filter dan Download Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Filter
                OmsetkuButton(
                    text = "Filter",
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.weight(0.3f),
                    isOutlined = true
                )
                
                // Tombol Download Laporan
                OmsetkuButton(
                    text = "Download Laporan",
                    onClick = { /* TODO: Implementasi download laporan */ },
                    modifier = Modifier.weight(0.7f)
                )
            }
            
            // Filter Dialog
            if (showFilterDialog) {
                FilterDialog(
                    onDismiss = { showFilterDialog = false },
                    selectedPeriode = selectedPeriode,
                    onPeriodeSelected = { selectedPeriode = it },
                    onApply = {
                        // Update periode text berdasarkan selectedPeriode
                        periodeText = when (selectedPeriode) {
                            FilterPeriode.HARIAN -> "12 April 2025"
                            FilterPeriode.MINGGUAN -> "06 April 2025 - 12 April 2025"
                            FilterPeriode.BULANAN -> "01 April 2025 - 30 April 2025"
                            FilterPeriode.TAHUNAN -> "2025"
                        }
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
                OmsetkuCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE8F7F5))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Pendapatan",
                            style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.DarkGray
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp 103.193.000",
                            style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF08C39F)
                            )
                        )
                    }
                }
                
                // Card Total Pengeluaran
                OmsetkuCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFDEDED))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Pengeluaran",
                            style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.DarkGray
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp 7.902.646",
                            style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE74C3C)
                            )
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
    onApply: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // State untuk nilai tanggal
    var selectedDate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var startDateTimestamp by remember { mutableStateOf<Long?>(null) }

    OmsetkuDialog(
        title = "Filter",
        onDismiss = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Pilih Periode Transaksi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
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
                            .selectable(
                                selected = selectedOption == periode,
                                onClick = { 
                                    selectedOption = periode
                                    selectedDate = ""
                                    startDate = ""
                                    endDate = ""
                                    selectedMonth = ""
                                    selectedYear = ""
                                    startDateTimestamp = null
                                },
                                role = Role.RadioButton
                            )
                    ) {
                        RadioButton(
                            selected = selectedOption == periode,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = Color.Gray
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Input fields based on selected period
                when (selectedOption) {
                    FilterPeriode.HARIAN -> {
                        Text(
                            text = "Pilih Tanggal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DatePickerField(
                            value = selectedDate,
                            onDateSelected = { selectedDate = it },
                            placeholder = "Pilih tanggal",
                            mode = DatePickerMode.DAILY
                        )
                    }
                    FilterPeriode.MINGGUAN -> {
                        Text(
                            text = "Pilih Tanggal Awal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DatePickerField(
                            value = startDate,
                            onDateSelected = { 
                                startDate = it
                                startDateTimestamp = SimpleDateFormat("dd MMMM yyyy", Locale("id")).parse(it)?.time
                            },
                            placeholder = "Pilih tanggal awal",
                            mode = DatePickerMode.DAILY
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Pilih Tanggal Akhir",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DatePickerField(
                            value = endDate,
                            onDateSelected = { endDate = it },
                            placeholder = "Pilih tanggal akhir",
                            mode = DatePickerMode.WEEKLY_END,
                            startDate = startDateTimestamp
                        )
                    }
                    FilterPeriode.BULANAN -> {
                        Text(
                            text = "Pilih Bulan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DatePickerField(
                            value = selectedMonth,
                            onDateSelected = { selectedMonth = it },
                            placeholder = "Pilih bulan",
                            mode = DatePickerMode.MONTHLY
                        )
                    }
                    FilterPeriode.TAHUNAN -> {
                        Text(
                            text = "Pilih Tahun",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DatePickerField(
                            value = selectedYear,
                            onDateSelected = { selectedYear = it },
                            placeholder = "Pilih tahun",
                            mode = DatePickerMode.YEARLY
                        )
                    }
                }
            }
        },
        buttons = {
            OmsetkuButton(
                text = "Terapkan",
                onClick = {
                    onPeriodeSelected(selectedOption)
                    onApply()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
} 