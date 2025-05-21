package com.example.omsetku.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxSettingsScreen(navController: NavController) {
    var taxEnabled by remember { mutableStateOf(false) }
    var taxRate by remember { mutableStateOf("") }
    var isTaxSaved by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = "Pengaturan Pajak",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        
        // Status Pajak - Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status Pajak",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
            
            Switch(
                checked = taxEnabled,
                onCheckedChange = { 
                    taxEnabled = it
                    if (!it) {
                        isTaxSaved = false
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryVariant,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
        
        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Atur pengenaan pajak untuk setiap transaksi.",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (taxEnabled) {
            if (isTaxSaved) {
                // Tampilkan bagian pajak aktif
                Text(
                    text = "Pajak Aktif",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Item pajak dengan rate
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tarif Pajak",
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$taxRate%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                        
                        IconButton(
                            onClick = { isTaxSaved = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.go_icon),
                                contentDescription = "Edit",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            } else {
                // Tarif Pajak input
                Text(
                    text = "Tarif Pajak",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = taxRate,
                    onValueChange = { 
                        // Hanya menerima angka
                        if (it.isEmpty() || it.matches(Regex("^\\d+\$")) && it.length <= 2) {
                            taxRate = it
                        }
                    },
                    placeholder = { Text("Contoh: 10", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = TextStyle(
                        fontFamily = Poppins,
                        fontSize = 14.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryVariant,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    trailingIcon = {
                        Text(
                            text = "%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Tombol Simpan
                Button(
                    onClick = { 
                        if (taxRate.isNotEmpty()) {
                            isTaxSaved = true
                        }
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
                        text = "Simpan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
} 