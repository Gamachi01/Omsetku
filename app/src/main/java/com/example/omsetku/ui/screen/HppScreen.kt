package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar

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
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Tab Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                TabButton(
                    text = "Stok",
                    isSelected = selectedTab == HppTab.STOK,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = HppTab.STOK }
                )
                TabButton(
                    text = "Bahan Baku",
                    isSelected = selectedTab == HppTab.BAHAN_BAKU,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = HppTab.BAHAN_BAKU }
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
                    color = Color.Gray
                )
            }
            
            if (selectedTab == HppTab.STOK) {
                HppStokContent()
            } else {
                HppBahanBakuContent()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Implementasi perhitungan HPP */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5ED0C5)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Hitung",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(
                if (text == "Stok") RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                else RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
            )
            .ButtonDefaults.indicatorColor(
                if (isSelected) Color(0xFF5ED0C5) else Color.White
            )
            .ButtonDefaults.elevatedButtonColors()
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFF5ED0C5) else Color.White,
                contentColor = if (isSelected) Color.White else Color.Black
            ),
            shape = RoundedCornerShape(0.dp),
            elevation = null
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun HppStokContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Pilih Produk",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Dropdown untuk pilih produk
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { 
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(24.dp)
                )
            },
            shape = RoundedCornerShape(8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Persediaan Awal dan Akhir
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Persediaan Awal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rp") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Persediaan Akhir",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rp") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pembelian Bersih
        Text(
            text = "Pembelian Bersih",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rp") },
            shape = RoundedCornerShape(8.dp)
        )
        
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
                    Text(
                        text = "Biaya Operasional",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nominal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rp") },
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
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Tombol Tambah Biaya Operasional
        OutlinedButton(
            onClick = {
                biayaOperasionalList = biayaOperasionalList + 1
            },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Gray),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambah Biaya Operasional")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estimasi Jumlah Produk Terjual
        Text(
            text = "Estimasi Jumlah Produk Terjual per Bulan",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun HppBahanBakuContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Pilih Produk",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Dropdown untuk pilih produk
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { 
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(24.dp)
                )
            },
            shape = RoundedCornerShape(8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bahan Baku List
        var bahanBakuList by remember { mutableStateOf(listOf(1, 2)) }
        
        bahanBakuList.forEachIndexed { index, bahan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
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
                            color = Color.Black
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
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Harga per Satuan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Rp") },
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
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Total Harga",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Rp") },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tombol Tambah Bahan Baku
        OutlinedButton(
            onClick = {
                bahanBakuList = bahanBakuList + 1
            },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Gray),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambah Bahan Baku")
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
                    Text(
                        text = "Biaya Operasional",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nominal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rp") },
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
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Tombol Tambah Biaya Operasional
        OutlinedButton(
            onClick = {
                biayaOperasionalList = biayaOperasionalList + 1
            },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Gray),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambah Biaya Operasional")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estimasi Terjual
        Text(
            text = "Estimasi terjual dalam bulanan",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
} 