package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.HppBahanBakuList
import com.example.omsetku.ui.components.HppBiayaOperasionalList
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.data.ProductItem
import com.example.omsetku.viewmodels.HppViewModel
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.omsetku.ui.components.FormField
import com.example.omsetku.ui.components.StandardTextField
import com.example.omsetku.ui.components.MultilineTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HppScreen(
    navController: NavController,
    hppViewModel: HppViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableStateOf("HPP") }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    // State untuk mengontrol tampilan dialog hasil
    var showResultDialog by remember { mutableStateOf(false) }

    // State dari ViewModel
    val products by hppViewModel.products.collectAsState()
    val selectedProduct by hppViewModel.selectedProduct.collectAsState()
    val bahanBakuList by hppViewModel.bahanBakuList.collectAsState()
    val biayaOperasionalList by hppViewModel.biayaOperasionalList.collectAsState()
    val targetPorsi by hppViewModel.targetPorsi.collectAsState()
    val marginProfit by hppViewModel.marginProfit.collectAsState()
    val isSaving by hppViewModel.isSaving.collectAsState()

    // Efek untuk memuat data produk saat screen dibuka
    LaunchedEffect(Unit) {
        hppViewModel.loadProducts()
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> navController.navigate(Routes.HOME)
                        "Cashier" -> navController.navigate(Routes.CASHIER)
                        "Transaction" -> navController.navigate(Routes.TRANSACTION)
                        "HPP" -> { /* Sudah di layar HPP */ }
                        "Report" -> navController.navigate(Routes.REPORT)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Judul seperti Transaction
            Text(
                text = "Hitung HPP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Informasi Produk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Dropdown Pilih Produk (ExposedDropdownMenuBox, style konsisten)
                    FormField(label = "Pilih Produk") {
                        var expanded by remember { mutableStateOf(false) }
                        val selectedText = selectedProduct?.name ?: "Pilih Produk"
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StandardTextField(
                                value = selectedText,
                                onValueChange = {},
                                label = "Pilih Produk", // Label ada di dalam floating
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                products.forEach { product ->
                                    DropdownMenuItem(
                                        text = { Text(product.name, fontFamily = Poppins) },
                                        onClick = {
                                            hppViewModel.selectProduct(product)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Biaya Operasional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Biaya Operasional",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        biayaOperasionalList.forEachIndexed { index, biaya ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StandardTextField(
                                    value = biaya.nama,
                                    onValueChange = { hppViewModel.updateBiayaOperasionalNama(index, it) },
                                    label = "Nama Biaya",
                                    modifier = Modifier.weight(1f)
                                )
                                StandardTextField(
                                    value = biaya.jumlah,
                                    onValueChange = { hppViewModel.updateBiayaOperasionalHarga(index, it) },
                                    label = "Jumlah",
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { hppViewModel.removeBiayaOperasional(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                                }
                            }
                        }
                        Button(onClick = { hppViewModel.addBiayaOperasional() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))) {
                            Text("Tambah Biaya", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bahan Baku
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Bahan Baku",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        bahanBakuList.forEachIndexed { index, bahan ->
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                StandardTextField(
                                    value = bahan.nama,
                                    onValueChange = { hppViewModel.updateBahanBakuNama(index, it) },
                                    label = "Nama Bahan Baku",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StandardTextField(
                                        value = bahan.hargaPerUnit,
                                        onValueChange = { hppViewModel.updateBahanBakuHarga(index, it) },
                                        label = "Harga per Unit",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StandardTextField(
                                        value = bahan.jumlahDigunakan,
                                        onValueChange = { hppViewModel.updateBahanBakuJumlah(index, it) },
                                        label = "Jumlah",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StandardTextField(
                                        value = bahan.satuan,
                                        onValueChange = { hppViewModel.updateBahanBakuSatuan(index, it) },
                                        label = "Satuan",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                IconButton(onClick = { hppViewModel.removeBahanBaku(index) }, modifier = Modifier.align(Alignment.End)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                                }
                            }
                        }
                        Button(onClick = { hppViewModel.addBahanBaku() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))) {
                            Text("Tambah Bahan Baku", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Target Porsi dan Margin Profit di dalam Card terpisah
             Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Perhitungan HPP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black
                    )
                    // Target Porsi
                    StandardTextField(
                        value = targetPorsi,
                        onValueChange = { hppViewModel.updateTargetPorsi(it) },
                        label = "Target Porsi",
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Margin Profit
                    StandardTextField(
                        value = marginProfit,
                        onValueChange = { hppViewModel.updateMarginProfit(it) },
                        label = "Margin Profit (%)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Button Hitung HPP di luar Card
            Button(
                onClick = {
                    showResultDialog = true
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                Text(
                    text = "Hitung HPP",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }

    // Dialog Hasil Perhitungan
    if (showResultDialog) {
        Dialog(onDismissRequest = { showResultDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Hasil Perhitungan HPP",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Tampilkan Hasil HPP (dari HppViewModel)
                    Text(
                        text = "HPP per Porsi: Rp ${hppViewModel.hitungHpp().toInt()}",
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showResultDialog = false },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
                    ) {
                        Text("Tutup", fontFamily = Poppins)
                    }
                }
            }
        }
    }
}