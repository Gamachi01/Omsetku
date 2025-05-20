package com.example.omsetku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.omsetku.ui.theme.PrimaryVariant

@Composable
fun KasirScreen(
    onNavigateBack: () -> Unit
) {
    var imageValue by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Form Input
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Input
            OutlinedTextField(
                value = imageValue,
                onValueChange = { newValue -> imageValue = newValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text(text = "Tambah Gambar") },
                readOnly = true,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryVariant,
                    unfocusedBorderColor = Color.Gray
                ),
                textStyle = LocalTextStyle.current.copy(
                    // Perbesar padding kiri-kanan dengan spasi manual
                    // Atau gunakan fontSize lebih besar jika ingin
                )
            )
            
            // Other form content here
        }
    }
} 