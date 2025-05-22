package com.example.omsetku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    placeholder: String = "Pilih tanggal",
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Fungsi untuk menampilkan date picker sebenarnya memerlukan implementasi
    // native Android DatePickerDialog yang tidak mudah ditambahkan di sini.
    // Untuk demo, kita akan menggunakan tanggal saat ini yang diformat.
    val selectDate = {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val formattedDate = dateFormat.format(calendar.time)
        onDateSelected(formattedDate)
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = { /* Readonly, tidak perlu implementasi */ },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { selectDate() },
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
                    .clickable { selectDate() },
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