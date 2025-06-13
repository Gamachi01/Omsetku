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
import com.example.omsetku.viewmodels.ProductViewModel
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
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HppScreen(
    navController: NavController,
    hppViewModel: HppViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableStateOf("HPP") }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var showResultDialog by remember { mutableStateOf(false) }

    // State dari ViewModel
    val products by hppViewModel.products.collectAsState()
    val selectedProduct by hppViewModel.selectedProduct.collectAsState()
    val bahanBakuList by hppViewModel.bahanBakuList.collectAsState()
    val biayaOperasionalList by hppViewModel.biayaOperasionalList.collectAsState()
    val targetPorsi by hppViewModel.targetPorsi.collectAsState()
    val marginProfit by hppViewModel.marginProfit.collectAsState()
    val isSaving by hppViewModel.isSaving.collectAsState()

    // Tambahkan daftar satuan
    val satuanList = listOf(
        "kg", "gram", "mg", "ons", "pon", "liter", "ml", "cc", "pcs", "butir", "lembar", "bungkus", "pack", "botol", "kaleng", "sachet", "buah", "ekor", "batang", "siung", "sendok", "gelas", "mangkok", "porsi", "kWh", "jam", "menit", "hari", "bulan"
    )

    // State untuk bottom sheet satuan bahan baku
    var showSatuanSheetForIndex by remember { mutableStateOf<Int?>(null) }
    val satuanSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State untuk dropdown satuan bahan baku
    var expandedSatuanIndex by remember { mutableStateOf<Int?>(null) }

    // Deklarasi variabel hasil perhitungan di luar blok if
    val hppPerPorsi = hppViewModel.hitungHpp()
    val margin = marginProfit.toDoubleOrNull() ?: 0.0
    val rekomendasiHarga = hppPerPorsi + (hppPerPorsi * margin / 100.0)

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
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Hitung HPP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            // Dropdown Pilih Produk
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

            // Bahan Baku
            Text(
                text = "Bahan Baku",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF444444),
                fontFamily = Poppins
            )

            // Pindahkan deklarasi variabel ke luar blok UI
            bahanBakuList.forEachIndexed { index, bahan ->
                val hargaBeli = bahan.hargaBeli.toDoubleOrNull() ?: 0.0
                val jumlahBeli = bahan.jumlahBeli.toDoubleOrNull() ?: 1.0
                val terpakai = bahan.terpakai.toDoubleOrNull() ?: 0.0
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
                            FormField(label = "Nama Bahan Baku") {
                                StandardTextField(
                                    value = bahan.nama,
                                    onValueChange = { hppViewModel.updateBahanBakuNama(index, it) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            FormField(label = "Harga Dibayar (total)") {
                                StandardTextField(
                                    value = bahan.hargaBeli,
                                    onValueChange = { hppViewModel.updateBahanBakuHargaBeli(index, it) },
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
                                        value = bahan.jumlahBeli,
                                        onValueChange = { hppViewModel.updateBahanBakuJumlahBeli(index, it) },
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
                                            value = bahan.satuan,
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
                                                        hppViewModel.updateBahanBakuSatuan(index, satuan)
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
                                    value = bahan.terpakai,
                                    onValueChange = { hppViewModel.updateBahanBakuTerpakai(index, it) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            // Keterangan ongkos otomatis
                            if (bahan.hargaBeli.isNotBlank() && bahan.jumlahBeli.isNotBlank() && bahan.terpakai.isNotBlank()) {
                                Text(
                                    text = "Harga: Rp ${ongkos.toInt()}",
                                    fontFamily = Poppins,
                                    color = Color(0xFF5ED0C5),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (bahanBakuList.size > 1) {
                            IconButton(
                                onClick = { hppViewModel.removeBahanBaku(index) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { hppViewModel.addBahanBaku() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                Text("Tambah Bahan Baku", fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Biaya Operasional
            Text(
                text = "Biaya Operasional",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF444444),
                fontFamily = Poppins
            )

            HppBiayaOperasionalList(
                biayaOperasionalList = biayaOperasionalList,
                onBiayaOperasionalNamaChanged = { index, value -> hppViewModel.updateBiayaOperasionalNama(index, value) },
                onBiayaOperasionalHargaBeliChanged = { index, value -> hppViewModel.updateBiayaOperasionalHargaBeli(index, value) },
                onBiayaOperasionalJumlahBeliChanged = { index, value -> hppViewModel.updateBiayaOperasionalJumlahBeli(index, value) },
                onBiayaOperasionalSatuanChanged = { index, value -> hppViewModel.updateBiayaOperasionalSatuan(index, value) },
                onBiayaOperasionalTerpakaiChanged = { index, value -> hppViewModel.updateBiayaOperasionalTerpakai(index, value) },
                onAddBiayaOperasional = { hppViewModel.addBiayaOperasional() },
                onRemoveBiayaOperasional = { index -> hppViewModel.removeBiayaOperasional(index) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Porsi
            FormField(label = "Target Porsi") {
                StandardTextField(
                    value = targetPorsi,
                    onValueChange = { hppViewModel.updateTargetPorsi(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Margin Profit
            FormField(label = "Margin Profit (%)") {
                StandardTextField(
                    value = marginProfit,
                    onValueChange = { hppViewModel.updateMarginProfit(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tombol Hitung HPP
            Button(
                onClick = {
                    showResultDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5ED0C5)
                )
            ) {
                Text(
                    text = "Hitung HPP",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Dialog Hasil Perhitungan
    if (showResultDialog) {
        Dialog(onDismissRequest = { showResultDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
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
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // HPP per Porsi
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "HPP per Porsi",
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Rp ${hppPerPorsi.toInt()}",
                                fontFamily = Poppins,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5ED0C5)
                            )
                        }
                    }

                    // Rekomendasi Harga Jual
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F7FA)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Rekomendasi Harga Jual",
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Rp ${rekomendasiHarga.toInt()}",
                                fontFamily = Poppins,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5ED0C5)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val product = selectedProduct
                            if (product != null) {
                                hppViewModel.saveHpp(product.id, hppPerPorsi)
                            }
                            showResultDialog = false
                            // Tambahkan refresh produk setelah simpan HPP
                            productViewModel.loadProducts()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5ED0C5)
                        )
                    ) {
                        Text(
                            "Simpan",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // ModalBottomSheet untuk pemilihan satuan bahan baku
    if (showSatuanSheetForIndex != null) {
        ModalBottomSheet(
            onDismissRequest = { showSatuanSheetForIndex = null },
            sheetState = satuanSheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pilih Satuan", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                satuanList.forEach { satuan ->
                    Text(
                        text = satuan,
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val idx = showSatuanSheetForIndex ?: return@clickable
                                hppViewModel.updateBahanBakuSatuan(idx, satuan)
                                showSatuanSheetForIndex = null
                            }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}