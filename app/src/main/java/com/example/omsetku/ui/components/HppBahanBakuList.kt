package com.example.omsetku.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.omsetku.viewmodels.HppViewModel.BahanBaku
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.omsetku.ui.components.Poppins
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun HppBahanBakuList(
    bahanBakuList: List<BahanBaku>,
    onBahanBakuNamaChanged: (Int, String) -> Unit,
    onBahanBakuHargaChanged: (Int, String) -> Unit,
    onBahanBakuJumlahChanged: (Int, String) -> Unit,
    onBahanBakuSatuanChanged: (Int, String) -> Unit,
    onAddBahanBaku: () -> Unit,
    onRemoveBahanBaku: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Bahan Baku",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        bahanBakuList.forEachIndexed { index, bahan ->
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
                            text = "Bahan ${index + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                        if (bahanBakuList.size > 1) {
                            IconButton(
                                onClick = { onRemoveBahanBaku(index) },
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
                        value = bahan.nama,
                        onValueChange = { onBahanBakuNamaChanged(index, it) },
                        label = { Text("Nama Bahan", fontFamily = Poppins) },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = bahan.hargaPerUnit,
                            onValueChange = { onBahanBakuHargaChanged(index, it) },
                            label = { Text("Harga/Unit", fontFamily = Poppins) },
                            modifier = Modifier.weight(1f),
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

                        OutlinedTextField(
                            value = bahan.jumlahDigunakan,
                            onValueChange = { onBahanBakuJumlahChanged(index, it) },
                            label = { Text("Jumlah", fontFamily = Poppins) },
                            modifier = Modifier.weight(1f),
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

                        OutlinedTextField(
                            value = bahan.satuan,
                            onValueChange = { onBahanBakuSatuanChanged(index, it) },
                            label = { Text("Satuan", fontFamily = Poppins) },
                            modifier = Modifier.weight(1f),
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
        }

        Button(
            onClick = onAddBahanBaku,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5ED0C5)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Tambah Bahan Baku",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
} 