package com.example.omsetku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    placeholder: String = "Pilih tanggal",
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Format tanggal
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    
    // Kalender untuk menyimpan tanggal yang dipilih
    val calendar = remember { Calendar.getInstance() }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )
    
    // Menampilkan date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Mengambil tanggal yang dipilih
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.timeInMillis = millis
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
            },
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
    
    // Field tanggal yang dapat diklik
    OutlinedTextField(
        value = value,
        onValueChange = { /* Readonly, tidak perlu implementasi */ },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { showDatePicker = true },
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
                    .clickable { showDatePicker = true },
                tint = Color(0xFF5ED0C5)
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF5ED0C5)
        )
    )
} 