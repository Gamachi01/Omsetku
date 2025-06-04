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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HppBiayaOperasionalList(
    biayaOperasionalList: List<BiayaOperasional>,
    onBiayaOperasionalNamaChanged: (Int, String) -> Unit,
    onBiayaOperasionalHargaBeliChanged: (Int, String) -> Unit,
    onBiayaOperasionalJumlahBeliChanged: (Int, String) -> Unit,
    onBiayaOperasionalSatuanChanged: (Int, String) -> Unit,
    onBiayaOperasionalTerpakaiChanged: (Int, String) -> Unit,
    onAddBiayaOperasional: () -> Unit,
    onRemoveBiayaOperasional: (Int) -> Unit
) {
    // Daftar satuan sama seperti di HppScreen
    val satuanList = listOf(
        "kg", "gram", "mg", "ons", "pon", "liter", "ml", "cc", "pcs", "butir", "lembar", "bungkus", "pack", "botol", "kaleng", "sachet", "buah", "ekor", "batang", "siung", "sendok", "gelas", "mangkok", "porsi", "kWh", "jam", "menit", "hari", "bulan"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        biayaOperasionalList.forEachIndexed { index, biaya ->
            val hargaBeli = biaya.hargaBeli.toDoubleOrNull() ?: 0.0
            val jumlahBeli = biaya.jumlahBeli.toDoubleOrNull() ?: 1.0
            val terpakai = biaya.terpakai.toDoubleOrNull() ?: 0.0
            val ongkos = if (jumlahBeli > 0) (terpakai / jumlahBeli) * hargaBeli else 0.0
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FormField(label = "Nama Biaya") {
                            StandardTextField(
                                value = biaya.nama,
                                onValueChange = { onBiayaOperasionalNamaChanged(index, it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        FormField(label = "Harga Dibayar (total)") {
                            StandardTextField(
                                value = biaya.hargaBeli,
                                onValueChange = { onBiayaOperasionalHargaBeliChanged(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                                isRupiah = true,
                                placeholder = "Rp"
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FormField(label = "Jumlah Beli", modifier = Modifier.weight(1f)) {
                                StandardTextField(
                                    value = biaya.jumlahBeli,
                                    onValueChange = { onBiayaOperasionalJumlahBeliChanged(index, it) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            FormField(label = "Satuan", modifier = Modifier.weight(1f)) {
                                val expanded = remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expanded.value,
                                    onExpandedChange = { expanded.value = !expanded.value },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    StandardTextField(
                                        value = biaya.satuan,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                                        modifier = Modifier.menuAnchor().fillMaxWidth().clickable { expanded.value = true }
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded.value,
                                        onDismissRequest = { expanded.value = false }
                                    ) {
                                        satuanList.forEach { satuan ->
                                            DropdownMenuItem(
                                                text = { Text(satuan, fontFamily = Poppins) },
                                                onClick = {
                                                    onBiayaOperasionalSatuanChanged(index, satuan)
                                                    expanded.value = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        FormField(label = "Terpakai") {
                            StandardTextField(
                                value = biaya.terpakai,
                                onValueChange = { onBiayaOperasionalTerpakaiChanged(index, it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        // Keterangan ongkos otomatis
                        if (biaya.hargaBeli.isNotBlank() && biaya.jumlahBeli.isNotBlank() && biaya.terpakai.isNotBlank()) {
                            Text(
                                text = "Harga: Rp ${ongkos.toInt()}",
                                fontFamily = Poppins,
                                color = Color(0xFF5ED0C5),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (biayaOperasionalList.size > 1) {
                        IconButton(
                            onClick = { onRemoveBiayaOperasional(index) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onAddBiayaOperasional,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
        ) {
            Text(
                text = "Tambah Biaya Operasional",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
} 