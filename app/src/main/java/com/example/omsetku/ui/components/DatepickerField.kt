package com.example.omsetku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.omsetku.R
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background
import java.util.concurrent.TimeUnit

enum class DatePickerMode {
    DAILY, // Pilih tanggal normal
    WEEKLY_START, // Tanggal awal minggu
    WEEKLY_END, // Tanggal akhir minggu (otomatis seminggu setelah tanggal awal)
    MONTHLY, // Hanya pilih bulan dan tahun
    YEARLY // Hanya pilih tahun
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    placeholder: String = "Pilih tanggal",
    modifier: Modifier = Modifier,
    mode: DatePickerMode = DatePickerMode.DAILY,
    startDate: Long? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    // Format tanggal berdasarkan mode
    val dateFormat = when (mode) {
        DatePickerMode.DAILY, DatePickerMode.WEEKLY_START, DatePickerMode.WEEKLY_END ->
            SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        DatePickerMode.MONTHLY ->
            SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        DatePickerMode.YEARLY ->
            SimpleDateFormat("yyyy", Locale("id", "ID"))
    }

    // Gunakan TimeZone default sistem
    val currentTimeZone = TimeZone.getDefault()

    // Kalender untuk menyimpan tanggal yang dipilih - gunakan TimeZone default
    val calendar = remember { Calendar.getInstance() }

    // Jika mode WEEKLY_END, gunakan startDate sebagai tanggal minimum
    val initialDate = when {
        mode == DatePickerMode.WEEKLY_END && startDate != null -> startDate
        else -> calendar.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    // Fungsi untuk membuka dialog picker yang sesuai
    val openPicker = {
        when (mode) {
            DatePickerMode.DAILY, DatePickerMode.WEEKLY_START, DatePickerMode.WEEKLY_END ->
                showDatePicker = true
            DatePickerMode.MONTHLY ->
                showMonthPicker = true
            DatePickerMode.YEARLY ->
                showYearPicker = true
        }
    }

    // Menampilkan date picker dialog untuk mode DAILY, WEEKLY_START, dan WEEKLY_END
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Gunakan Calendar dengan TimeZone Indonesia
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.timeInMillis = millis

                        // Selalu atur waktu ke siang hari untuk menghindari masalah
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, 12)
                        selectedCalendar.set(Calendar.MINUTE, 0)
                        selectedCalendar.set(Calendar.SECOND, 0)
                        selectedCalendar.set(Calendar.MILLISECOND, 0)

                        // Tambahkan 1 hari untuk mengatasi masalah timezone di Indonesia
                        selectedCalendar.add(Calendar.DAY_OF_MONTH, 1)

                        // Jika mode WEEKLY_END, pastikan tanggal yang dipilih adalah seminggu setelah startDate
                        if (mode == DatePickerMode.WEEKLY_END && startDate != null) {
                            val startCalendar = Calendar.getInstance()
                            startCalendar.timeInMillis = startDate
                            if (selectedCalendar.timeInMillis >= startCalendar.timeInMillis) {
                                startCalendar.add(Calendar.DAY_OF_MONTH, 7)
                                selectedCalendar.timeInMillis = startCalendar.timeInMillis
                            }
                        }

                        val formattedDate = dateFormat.format(selectedCalendar.time)
                        onDateSelected(formattedDate)
                    }
                    showDatePicker = false
                }) {
                    Text(
                        text = "OK",
                        color = Color(0xFF5ED0C5),
                        fontFamily = Poppins
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "Batal",
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    headlineContentColor = Color.Black,
                    weekdayContentColor = Color.Gray,
                    subheadContentColor = Color(0xFF5ED0C5),
                    yearContentColor = Color.Black,
                    currentYearContentColor = Color(0xFF5ED0C5),
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Color(0xFF5ED0C5),
                    dayContentColor = Color.Black,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF5ED0C5),
                    todayContentColor = Color(0xFF5ED0C5),
                    todayDateBorderColor = Color(0xFF5ED0C5)
                )
            )
        }
    }

    // Menampilkan month picker dialog untuk mode MONTHLY
    if (showMonthPicker) {
        MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month, year ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, 15) // Gunakan tanggal tengah bulan

                // Set waktu ke tengah hari
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 12)
                selectedCalendar.set(Calendar.MINUTE, 0)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)

                val formattedDate = dateFormat.format(selectedCalendar.time)
                onDateSelected(formattedDate)
                showMonthPicker = false
            }
        )
    }

    // Menampilkan year picker dialog untuk mode YEARLY
    if (showYearPicker) {
        YearPickerDialog(
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, 6) // Gunakan bulan tengah tahun (Juli)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, 1)

                // Set waktu ke tengah hari
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 12)
                selectedCalendar.set(Calendar.MINUTE, 0)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)

                val formattedDate = dateFormat.format(selectedCalendar.time)
                onDateSelected(formattedDate)
                showYearPicker = false
            }
        )
    }

    // Buat Box container dengan clickable untuk seluruh area
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { openPicker() }
    ) {
        // Field tanggal
        OutlinedTextField(
            value = value,
            onValueChange = { /* Readonly, tidak perlu implementasi */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            readOnly = true,
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.transactioncalender),
                    contentDescription = "Kalender",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { openPicker() },
                    tint = Color(0xFF5ED0C5)
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF5ED0C5)
            ),
            enabled = false // Agar tidak muncul cursor dan background default disabled
        )
    }
}

@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (month: Int, year: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)

    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }

    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text(
                    text = "Pilih Bulan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Year selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Text(text = "<", fontSize = 20.sp)
                    }

                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(onClick = { selectedYear++ }) {
                        Text(text = ">", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month grid
                Column {
                    for (i in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (j in 0 until 3) {
                                val month = i * 3 + j
                                Box(
                                    modifier = Modifier
                                        .size(80.dp, 40.dp)
                                        .clickable {
                                            selectedMonth = month
                                            onMonthSelected(month, selectedYear)
                                        }
                                        .background(
                                            if (month == selectedMonth && selectedYear == currentYear)
                                                Color(0xFF5ED0C5)
                                            else
                                                Color.Transparent,
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = monthNames[month],
                                        color = if (month == selectedMonth && selectedYear == currentYear)
                                            Color.White
                                        else
                                            Color.Black,
                                        fontFamily = Poppins
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Batal",
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                    }

                    TextButton(
                        onClick = {
                            onMonthSelected(selectedMonth, selectedYear)
                        }
                    ) {
                        Text(
                            text = "OK",
                            color = Color(0xFF5ED0C5),
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YearPickerDialog(
    onDismiss: () -> Unit,
    onYearSelected: (year: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    var selectedYear by remember { mutableStateOf(currentYear) }
    var startYear by remember { mutableStateOf(currentYear - 10) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text(
                    text = "Pilih Tahun",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Year navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { startYear -= 10 }) {
                        Text(text = "<<", fontSize = 16.sp)
                    }

                    Text(
                        text = "${startYear} - ${startYear + 9}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(onClick = { startYear += 10 }) {
                        Text(text = ">>", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Year grid
                Column {
                    for (i in 0 until 5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (j in 0 until 2) {
                                val year = startYear + i * 2 + j
                                Box(
                                    modifier = Modifier
                                        .size(80.dp, 40.dp)
                                        .clickable {
                                            selectedYear = year
                                            onYearSelected(year)
                                        }
                                        .background(
                                            if (year == selectedYear)
                                                Color(0xFF5ED0C5)
                                            else
                                                Color.Transparent,
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = year.toString(),
                                        color = if (year == selectedYear)
                                            Color.White
                                        else
                                            Color.Black,
                                        fontFamily = Poppins
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Batal",
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                    }

                    TextButton(
                        onClick = { onYearSelected(selectedYear) }
                    ) {
                        Text(
                            text = "OK",
                            color = Color(0xFF5ED0C5),
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}