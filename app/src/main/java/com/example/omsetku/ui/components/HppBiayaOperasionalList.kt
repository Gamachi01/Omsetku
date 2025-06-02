package com.example.omsetku.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.omsetku.viewmodels.HppViewModel.BiayaOperasional
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.omsetku.ui.components.Poppins
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun HppBiayaOperasionalList(
    biayaOperasionalList: List<BiayaOperasional>,
    onBiayaOperasionalNamaChanged: (Int, String) -> Unit,
    onBiayaOperasionalHargaChanged: (Int, String) -> Unit,
    onAddBiayaOperasional: () -> Unit,
    onRemoveBiayaOperasional: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Biaya Operasional",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        biayaOperasionalList.forEachIndexed { index, biaya ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Biaya ${index + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        if (biayaOperasionalList.size > 1) {
                            IconButton(
                                onClick = { onRemoveBiayaOperasional(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = biaya.nama,
                        onValueChange = { onBiayaOperasionalNamaChanged(index, it) },
                        label = { Text("Nama Biaya", fontFamily = Poppins) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5ED0C5),
                            focusedLabelColor = Color(0xFF5ED0C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = Poppins,
                            fontSize = 14.sp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = biaya.jumlah,
                        onValueChange = { onBiayaOperasionalHargaChanged(index, it) },
                        label = { Text("Jumlah Biaya", fontFamily = Poppins) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5ED0C5),
                            focusedLabelColor = Color(0xFF5ED0C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = Poppins,
                            fontSize = 14.sp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        Button(
            onClick = onAddBiayaOperasional,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5ED0C5)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Tambah Biaya Operasional",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
} 