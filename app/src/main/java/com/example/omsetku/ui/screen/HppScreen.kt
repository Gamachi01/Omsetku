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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins

enum class HppTab {
    STOK, BAHAN_BAKU
}

@Composable
fun HppScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("HPP") }
    var selectedTab by remember { mutableStateOf(HppTab.STOK) }
    val scrollState = rememberScrollState()

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
                .padding(16.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hitung HPP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Tab Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                HppTabButton(
                    text = "Stok",
                    isSelected = selectedTab == HppTab.STOK,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = HppTab.STOK },
                    shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                )
                HppTabButton(
                    text = "Bahan Baku",
                    isSelected = selectedTab == HppTab.BAHAN_BAKU,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = HppTab.BAHAN_BAKU },
                    shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Info Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedTab == HppTab.STOK) 
                            "Hitung HPP dari stok produk yang terjual." 
                          else 
                            "Hitung HPP dari bahan resep dan jumlah pemakaian.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
            
            if (selectedTab == HppTab.STOK) {
                HppStokContent()
            } else {
                HppBahanBakuContent()
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tombol Hitung
            Button(
                onClick = { /* TODO: Implementasi perhitungan HPP */ },
                modifier = Modifier
                    .width(353.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                Text(
                    "Hitung", 
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
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
            .height(32.dp),
        contentPadding = PaddingValues(vertical = 0.dp),
        shape = shape,
        border = BorderStroke(1.dp, selectedColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = Color.Black
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
fun HppStokContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        HppLabeledFieldBox(label = "Pilih Produk") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                trailingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Persediaan Awal dan Akhir
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                HppLabeledFieldBox(label = "Persediaan Awal") {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                        placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                HppLabeledFieldBox(label = "Persediaan Akhir") {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                        placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
        
        HppLabeledFieldBox(label = "Pembelian Bersih") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp)
            )
        }
        
        // Biaya Operasional
        var biayaOperasionalList by remember { mutableStateOf(listOf(1, 2)) }
        
        biayaOperasionalList.forEachIndexed { index, _ ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    HppLabeledFieldBox(label = "Biaya Operasional") {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    HppLabeledFieldBox(label = "Nominal") {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                            placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = if (index > 0) {
                                {
                                    IconButton(onClick = {
                                        biayaOperasionalList = biayaOperasionalList.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }) {
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
            }
        }
        
        // Tombol Tambah Biaya Operasional
        Button(
            onClick = {
                biayaOperasionalList = biayaOperasionalList + 1
            },
            modifier = Modifier
                .width(353.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray),
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
        
        HppLabeledFieldBox(label = "Estimasi Jumlah Produk Terjual per Bulan") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun HppBahanBakuContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        HppLabeledFieldBox(label = "Pilih Produk") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                trailingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bahan Baku List
        var bahanBakuList by remember { mutableStateOf(listOf(1, 2)) }
        
        bahanBakuList.forEachIndexed { index, bahan ->
            Card(
                modifier = Modifier
                    .width(353.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEBFBF9)
                ),
                border = BorderStroke(1.dp, Color(0xFF5ED0C5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                onClick = {
                                    bahanBakuList = bahanBakuList.toMutableList().apply {
                                        removeAt(index)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Nama Bahan dan Harga per Satuan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Harga per Satuan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Jumlah Digunakan dan Total Harga
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Total Harga",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                                placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tombol Tambah Bahan Baku
        Button(
            onClick = {
                bahanBakuList = bahanBakuList + 1
            },
            modifier = Modifier
                .width(353.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray),
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
        var biayaOperasionalList by remember { mutableStateOf(listOf(1, 2)) }
        
        biayaOperasionalList.forEachIndexed { index, _ ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    HppLabeledFieldBox(label = "Biaya Operasional") {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    HppLabeledFieldBox(label = "Nominal") {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                            placeholder = { Text("Rp", fontSize = 14.sp, fontFamily = Poppins) },
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = if (index > 0) {
                                {
                                    IconButton(onClick = {
                                        biayaOperasionalList = biayaOperasionalList.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }) {
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
            }
        }
        
        // Tombol Tambah Biaya Operasional
        Button(
            onClick = {
                biayaOperasionalList = biayaOperasionalList + 1
            },
            modifier = Modifier
                .width(353.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray),
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
        
        HppLabeledFieldBox(label = "Estimasi terjual dalam bulanan") {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                shape = RoundedCornerShape(8.dp)
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
            .padding(vertical = 8.dp)
            .width(353.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = Poppins
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(353.dp)
                .heightIn(min = 56.dp),
        ) {
            content()
        }
    }
} 