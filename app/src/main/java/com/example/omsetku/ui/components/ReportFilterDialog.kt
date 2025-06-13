package com.example.omsetku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.omsetku.ui.screen.FilterPeriode
import com.example.omsetku.ui.theme.PrimaryVariant
import java.text.SimpleDateFormat
import java.util.*

data class FilterResult(
    val periode: FilterPeriode,
    val displayText: String,
    val selectedDate: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val selectedMonth: String = "",
    val selectedYear: String = ""
)

@Composable
fun ReportFilterDialog(
    onDismiss: () -> Unit,
    selectedPeriode: FilterPeriode,
    onApplyFilter: (FilterResult) -> Unit
) {
    val scrollState = rememberScrollState()

    // State untuk filter
    var currentPeriode by remember { mutableStateOf(selectedPeriode) }
    var selectedDate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }

    // State untuk timestamp tanggal awal (untuk mode mingguan)
    var startDateTimestamp by remember { mutableStateOf<Long?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .heightIn(max = 550.dp)
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

                // Radio button group
                options.forEach { (periode, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                currentPeriode = periode
                                // Reset nilai input ketika periode berubah
                                selectedDate = ""
                                startDate = ""
                                endDate = ""
                                selectedMonth = ""
                                selectedYear = ""
                                startDateTimestamp = null
                            }
                    ) {
                        RadioButton(
                            selected = currentPeriode == periode,
                            onClick = {
                                currentPeriode = periode
                                // Reset nilai input ketika periode berubah
                                selectedDate = ""
                                startDate = ""
                                endDate = ""
                                selectedMonth = ""
                                selectedYear = ""
                                startDateTimestamp = null
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

                // Input fields berdasarkan periode yang dipilih
                when (currentPeriode) {
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
                            onDateSelected = { selectedDate = it },
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
                                startDate = date
                                // Menyimpan timestamp untuk digunakan pada tanggal akhir
                                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                val parsedDate = dateFormat.parse(date)
                                startDateTimestamp = parsedDate?.time

                                // Otomatis mengatur tanggal akhir seminggu setelah tanggal awal
                                if (startDateTimestamp != null) {
                                    val calendar = Calendar.getInstance()
                                    calendar.timeInMillis = startDateTimestamp!!
                                    calendar.add(Calendar.DAY_OF_MONTH, 7)
                                    endDate = dateFormat.format(calendar.time)
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
                        DatePickerField(
                            value = endDate,
                            onDateSelected = { endDate = it },
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
                            onDateSelected = { selectedMonth = it },
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
                            onDateSelected = { selectedYear = it },
                            placeholder = "Pilih tahun",
                            mode = DatePickerMode.YEARLY
                        )
                    }
                }

                // Menambahkan spasi sebelum tombol Terapkan
                Spacer(modifier = Modifier.height(28.dp))

                // Tombol Terapkan
                Button(
                    onClick = {
                        // Membuat display text berdasarkan filter yang dipilih
                        val displayText = when (currentPeriode) {
                            FilterPeriode.HARIAN -> {
                                selectedDate
                            }
                            FilterPeriode.MINGGUAN -> {
                                if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                                    "$startDate - $endDate"
                                } else {
                                    "Periode Mingguan"
                                }
                            }
                            FilterPeriode.BULANAN -> {
                                if (selectedMonth.isNotEmpty()) {
                                    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                    try {
                                        val date = monthFormat.parse(selectedMonth)
                                        if (date != null) {
                                            val calendar = Calendar.getInstance()
                                            calendar.time = date

                                            // Hari pertama bulan
                                            calendar.set(Calendar.DAY_OF_MONTH, 1)
                                            val firstDay = dateFormat.format(calendar.time)

                                            // Hari terakhir bulan
                                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                                            val lastDay = dateFormat.format(calendar.time)

                                            "$firstDay - $lastDay"
                                        } else {
                                            selectedMonth
                                        }
                                    } catch (e: Exception) {
                                        selectedMonth
                                    }
                                } else {
                                    "Periode Bulanan"
                                }
                            }
                            FilterPeriode.TAHUNAN -> {
                                if (selectedYear.isNotEmpty()) {
                                    "01 Januari $selectedYear - 31 Desember $selectedYear"
                                } else {
                                    "Periode Tahunan"
                                }
                            }
                        }

                        // Mengembalikan hasil filter
                        onApplyFilter(
                            FilterResult(
                                periode = currentPeriode,
                                displayText = displayText,
                                selectedDate = selectedDate,
                                startDate = startDate,
                                endDate = endDate,
                                selectedMonth = selectedMonth,
                                selectedYear = selectedYear
                            )
                        )
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