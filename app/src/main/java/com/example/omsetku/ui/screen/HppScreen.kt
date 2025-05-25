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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.data.ProductItem
import com.example.omsetku.viewmodels.HppViewModel
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

enum class HppTab {
    STOK, BAHAN_BAKU
}

@Composable
fun HppScreen(
    navController: NavController,
    hppViewModel: HppViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableStateOf("HPP") }
    var selectedTab by remember { mutableStateOf(HppTab.STOK) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    // State untuk dialog
    var showHppResultDialog by remember { mutableStateOf(false) }
    
    // Mengambil data dari ViewModel
    val products by hppViewModel.products.collectAsState()
    val selectedProduct by hppViewModel.selectedProduct.collectAsState()
    val isLoading by hppViewModel.isLoading.collectAsState()
    val error by hppViewModel.error.collectAsState()
    val totalBiaya by hppViewModel.totalBiaya.collectAsState()
    val estimasiTerjual by hppViewModel.estimasiTerjual.collectAsState()
    val persediaanAwal by hppViewModel.persediaanAwal.collectAsState()
    val persediaanAkhir by hppViewModel.persediaanAkhir.collectAsState()
    val pembelianBersih by hppViewModel.pembelianBersih.collectAsState()
    val biayaOperasionalList by hppViewModel.biayaOperasionalList.collectAsState()
    val bahanBakuList by hppViewModel.bahanBakuList.collectAsState()
    val activeTab by hppViewModel.activeTab.collectAsState()
    val isSaving by hppViewModel.isSaving.collectAsState()

    // Efek untuk memuat data produk saat screen dibuka
    LaunchedEffect(Unit) {
        hppViewModel.loadProducts()
    }
    
    // Update active tab di ViewModel ketika tab berubah
    LaunchedEffect(selectedTab) {
        hppViewModel.setActiveTab(selectedTab)
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
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Hitung HPP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            
            // Tab Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                HppTabButton(
                    text = "Stok",
                    isSelected = selectedTab == HppTab.STOK,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = HppTab.STOK },
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
                HppTabButton(
                    text = "Bahan Baku",
                    isSelected = selectedTab == HppTab.BAHAN_BAKU,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = HppTab.BAHAN_BAKU },
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
            }
            
            // Info Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedTab == HppTab.STOK) "Hitung HPP dari stok produk yang terjual." else "Hitung HPP dari bahan resep dan jumlah pemakaian.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
            
            // Spacer diperkecil agar lebih rapat
            Spacer(modifier = Modifier.height(4.dp))
            
            // Loading state
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5ED0C5))
                }
            } else {
                if (selectedTab == HppTab.STOK) {
                    HppStokContent(
                        products = products,
                        selectedProduct = selectedProduct,
                        persediaanAwal = persediaanAwal,
                        persediaanAkhir = persediaanAkhir,
                        pembelianBersih = pembelianBersih,
                        biayaOperasionalList = biayaOperasionalList,
                        estimasiTerjual = estimasiTerjual.toString(),
                        onProductSelected = { hppViewModel.selectProduct(it) },
                        onPersediaanAwalChanged = { hppViewModel.updatePersediaanAwal(it) },
                        onPersediaanAkhirChanged = { hppViewModel.updatePersediaanAkhir(it) },
                        onPembelianBersihChanged = { hppViewModel.updatePembelianBersih(it) },
                        onBiayaOperasionalNamaChanged = { index, value -> 
                            hppViewModel.updateBiayaOperasionalNama(index, value)
                        },
                        onBiayaOperasionalJumlahChanged = { index, value -> 
                            hppViewModel.updateBiayaOperasionalJumlah(index, value)
                        },
                        onAddBiayaOperasional = { hppViewModel.addBiayaOperasional() },
                        onRemoveBiayaOperasional = { hppViewModel.removeBiayaOperasional(it) },
                        onEstimasiTerjualChanged = { hppViewModel.updateEstimasiTerjual(it) }
                    )
                } else {
                    HppBahanBakuContent(
                        products = products,
                        selectedProduct = selectedProduct,
                        bahanBakuList = bahanBakuList,
                        biayaOperasionalList = biayaOperasionalList,
                        estimasiTerjual = estimasiTerjual.toString(),
                        onProductSelected = { hppViewModel.selectProduct(it) },
                        onBahanBakuNamaChanged = { index, value -> 
                            hppViewModel.updateBahanBakuNama(index, value)
                        },
                        onBahanBakuHargaPerUnitChanged = { index, value -> 
                            hppViewModel.updateBahanBakuHargaPerUnit(index, value)
                        },
                        onBahanBakuJumlahDigunakanChanged = { index, value -> 
                            hppViewModel.updateBahanBakuJumlahDigunakan(index, value)
                        },
                        onBahanBakuTotalHargaChanged = { index, value -> 
                            hppViewModel.updateBahanBakuTotalHarga(index, value)
                        },
                        onAddBahanBaku = { hppViewModel.addBahanBaku() },
                        onRemoveBahanBaku = { hppViewModel.removeBahanBaku(it) },
                        onBiayaOperasionalNamaChanged = { index, value -> 
                            hppViewModel.updateBiayaOperasionalNama(index, value)
                        },
                        onBiayaOperasionalJumlahChanged = { index, value -> 
                            hppViewModel.updateBiayaOperasionalJumlah(index, value)
                        },
                        onAddBiayaOperasional = { hppViewModel.addBiayaOperasional() },
                        onRemoveBiayaOperasional = { hppViewModel.removeBiayaOperasional(it) },
                        onEstimasiTerjualChanged = { hppViewModel.updateEstimasiTerjual(it) }
                    )
                }
            }
            
            // Error message
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFDCDC)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error ?: "",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontFamily = Poppins
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tombol Hitung
            Button(
                onClick = { 
                    hppViewModel.hitungHpp()
                    showHppResultDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                Text(
                    text = "Hitung",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }
            
            // Tampilkan hasil perhitungan
            val hpp = hppViewModel.hitungHpp()
            Text(
                text = "HPP: Rp ${String.format("%,.2f", hpp)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Tombol Simpan
            Button(
                onClick = { hppViewModel.simpanHpp() },
                enabled = !isSaving && selectedProduct != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan HPP")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Dialog Hasil HPP
        if (showHppResultDialog && selectedProduct != null) {
            Dialog(
                onDismissRequest = { showHppResultDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "HPP Produk",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = Poppins,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Produk
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Produk:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                            Text(
                                text = selectedProduct?.name ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                fontFamily = Poppins
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Metode
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Metode:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                            Text(
                                text = if (selectedTab == HppTab.STOK) "Berdasarkan Stok" else "Berdasarkan Bahan Baku",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                fontFamily = Poppins
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Divider(color = Color.LightGray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Total Biaya Produksi
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Total Biaya Produksi:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                            Text(
                                text = "Rp ${totalBiaya.toInt().toString().reversed().chunked(3).joinToString(".").reversed()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                fontFamily = Poppins
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Estimasi Terjual
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Estimasi Terjual per Bulan:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                            Text(
                                text = "$estimasiTerjual pcs",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                fontFamily = Poppins
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Divider(color = Color.LightGray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // HPP per Produk
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "HPP per Produk:",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                            
                            val hppPerProduk = if (estimasiTerjual > 0) totalBiaya / estimasiTerjual else 0.0
                            
                            Text(
                                text = "Rp ${hppPerProduk.toInt().toString().reversed().chunked(3).joinToString(".").reversed()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5ED0C5),
                                fontFamily = Poppins
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Tombol OK
                        Button(
                            onClick = { showHppResultDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5ED0C5)
                            )
                        ) {
                            Text(
                                text = "OK",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HppTabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape
) {
    val selectedColor = Color(0xFF5ED0C5)
    val unselectedColor = Color.White
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        contentPadding = PaddingValues(vertical = 0.dp),
        shape = shape,
        border = BorderStroke(1.dp, selectedColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = if (isSelected) Color.White else Color.Black
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins
        )
    }
}

@Composable
fun HppStokContent(
    products: List<ProductItem>,
    selectedProduct: ProductItem?,
    persediaanAwal: String,
    persediaanAkhir: String,
    pembelianBersih: String,
    biayaOperasionalList: List<HppViewModel.BiayaOperasional>,
    estimasiTerjual: String,
    onProductSelected: (ProductItem) -> Unit,
    onPersediaanAwalChanged: (String) -> Unit,
    onPersediaanAkhirChanged: (String) -> Unit,
    onPembelianBersihChanged: (String) -> Unit,
    onBiayaOperasionalNamaChanged: (Int, String) -> Unit,
    onBiayaOperasionalJumlahChanged: (Int, String) -> Unit,
    onAddBiayaOperasional: () -> Unit,
    onRemoveBiayaOperasional: (Int) -> Unit,
    onEstimasiTerjualChanged: (String) -> Unit
) {
    var showProductDropdown by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        HppLabeledFieldBox(label = "Pilih Produk") {
            Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                    value = selectedProduct?.name ?: "",
                onValueChange = {},
                readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showProductDropdown = true },
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    placeholder = { 
                        Text(
                            "Pilih produk",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            color = Color.Gray
                        )
                    },
                trailingIcon = { 
                    Icon(
                            painter = painterResource(id = R.drawable.go_icon),
                        contentDescription = "Dropdown",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5),
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.Black
                    ),
                    enabled = false
                )

                DropdownMenu(
                    expanded = showProductDropdown,
                    onDismissRequest = { showProductDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    products.forEach { product ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = product.name,
                                        fontSize = 14.sp,
                                        fontFamily = Poppins
                                    )
                                    Text(
                                        text = "Rp ${product.price}",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontFamily = Poppins
                                    )
                                }
                            },
                            onClick = {
                                onProductSelected(product)
                                showProductDropdown = false
                            }
                        )
                    }
                }
            }
        }
        
        // Persediaan Awal dan Akhir
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                HppLabeledFieldBox(label = "Persediaan Awal") {
                    OutlinedTextField(
                        value = persediaanAwal,
                        onValueChange = onPersediaanAwalChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                        placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF5ED0C5)
                        )
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                HppLabeledFieldBox(label = "Persediaan Akhir") {
                    OutlinedTextField(
                        value = persediaanAkhir,
                        onValueChange = onPersediaanAkhirChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                        placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF5ED0C5)
                        )
                    )
                }
            }
        }
        
        HppLabeledFieldBox(label = "Pembelian Bersih") {
            OutlinedTextField(
                value = pembelianBersih,
                onValueChange = onPembelianBersihChanged,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                )
            )
        }
        
        // Biaya Operasional
        Text(
            text = "Biaya Operasional",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        
        biayaOperasionalList.forEachIndexed { index, biaya ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = biaya.nama,
                    onValueChange = { value -> onBiayaOperasionalNamaChanged(index, value) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    placeholder = { Text("Nama biaya", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    )
                )
                
                OutlinedTextField(
                    value = biaya.jumlah,
                    onValueChange = { value -> onBiayaOperasionalJumlahChanged(index, value) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    trailingIcon = if (index > 0) {
                        {
                            IconButton(onClick = { onRemoveBiayaOperasional(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    } else null
                )
            }
        }
        
        // Tombol Tambah Biaya Operasional
        OutlinedButton(
            onClick = onAddBiayaOperasional,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF5ED0C5)
            ),
            border = BorderStroke(1.dp, Color(0xFF5ED0C5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tambah Biaya Operasional",
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HppLabeledFieldBox(label = "Estimasi Jumlah Produk Terjual per Bulan") {
            OutlinedTextField(
                value = estimasiTerjual,
                onValueChange = onEstimasiTerjualChanged,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                )
            )
        }
    }
}

@Composable
fun HppBahanBakuContent(
    products: List<ProductItem>,
    selectedProduct: ProductItem?,
    bahanBakuList: List<HppViewModel.BahanBaku>,
    biayaOperasionalList: List<HppViewModel.BiayaOperasional>,
    estimasiTerjual: String,
    onProductSelected: (ProductItem) -> Unit,
    onBahanBakuNamaChanged: (Int, String) -> Unit,
    onBahanBakuHargaPerUnitChanged: (Int, String) -> Unit,
    onBahanBakuJumlahDigunakanChanged: (Int, String) -> Unit,
    onBahanBakuTotalHargaChanged: (Int, String) -> Unit,
    onAddBahanBaku: () -> Unit,
    onRemoveBahanBaku: (Int) -> Unit,
    onBiayaOperasionalNamaChanged: (Int, String) -> Unit,
    onBiayaOperasionalJumlahChanged: (Int, String) -> Unit,
    onAddBiayaOperasional: () -> Unit,
    onRemoveBiayaOperasional: (Int) -> Unit,
    onEstimasiTerjualChanged: (String) -> Unit
) {
    var showProductDropdown by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        HppLabeledFieldBox(label = "Pilih Produk") {
            Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                    value = selectedProduct?.name ?: "",
                onValueChange = {},
                readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showProductDropdown = true },
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    placeholder = { 
                        Text(
                            "Pilih produk",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            color = Color.Gray
                        )
                    },
                trailingIcon = { 
                    Icon(
                            painter = painterResource(id = R.drawable.go_icon),
                        contentDescription = "Dropdown",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5),
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.Black
                    ),
                    enabled = false
                )

                DropdownMenu(
                    expanded = showProductDropdown,
                    onDismissRequest = { showProductDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White)
                ) {
                    products.forEach { product ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = product.name,
                                        fontSize = 14.sp,
                                        fontFamily = Poppins
                                    )
                                    Text(
                                        text = "Rp ${product.price}",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontFamily = Poppins
                                    )
                                }
                            },
                            onClick = {
                                onProductSelected(product)
                                showProductDropdown = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Bahan Baku List
        Text(
            text = "Bahan Baku",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        bahanBakuList.forEachIndexed { index, bahan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF2FBFA)
                ),
                border = BorderStroke(1.dp, Color(0xFF5ED0C5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bahan ${index + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = Poppins
                        )
                        
                        if (index > 0) {
                            IconButton(
                                onClick = { onRemoveBahanBaku(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                    
                    // Nama Bahan dan Harga per Satuan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nama Bahan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = bahan.nama,
                                onValueChange = { onBahanBakuNamaChanged(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF5ED0C5),
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White
                                )
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Harga per Satuan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = bahan.hargaPerUnit,
                                onValueChange = { onBahanBakuHargaPerUnitChanged(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF5ED0C5),
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Jumlah Digunakan dan Total Harga
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Jumlah Digunakan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = bahan.jumlahDigunakan,
                                onValueChange = { onBahanBakuJumlahDigunakanChanged(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF5ED0C5),
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White
                                )
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Total Harga",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = bahan.totalHarga,
                                onValueChange = { onBahanBakuTotalHargaChanged(index, it) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Color(0xFF5ED0C5),
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
        
        // Tombol Tambah Bahan Baku
        OutlinedButton(
            onClick = onAddBahanBaku,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF5ED0C5)
            ),
            border = BorderStroke(1.dp, Color(0xFF5ED0C5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tambah Bahan Baku",
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Biaya Operasional
        Text(
            text = "Biaya Operasional",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        
        biayaOperasionalList.forEachIndexed { index, biaya ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = biaya.nama,
                    onValueChange = { onBiayaOperasionalNamaChanged(index, it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    placeholder = { Text("Nama biaya", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    )
                )
                
                OutlinedTextField(
                    value = biaya.jumlah,
                    onValueChange = { onBiayaOperasionalJumlahChanged(index, it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins, color = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    trailingIcon = if (index > 0) {
                        {
                            IconButton(onClick = { onRemoveBiayaOperasional(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    } else null
                )
            }
        }
        
        // Tombol Tambah Biaya Operasional
        OutlinedButton(
            onClick = onAddBiayaOperasional,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF5ED0C5)
            ),
            border = BorderStroke(1.dp, Color(0xFF5ED0C5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tambah Biaya Operasional",
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HppLabeledFieldBox(label = "Estimasi terjual dalam bulanan") {
            OutlinedTextField(
                value = estimasiTerjual,
                onValueChange = onEstimasiTerjualChanged,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                )
            )
        }
    }
}

@Composable
fun HppLabeledFieldBox(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
} 