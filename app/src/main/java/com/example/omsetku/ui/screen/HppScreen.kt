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
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins
import kotlinx.coroutines.launch

enum class HppTab {
    STOK, BAHAN_BAKU
}

// Model data produk
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String = ""
)

@Composable
fun HppScreen(
    modifier: Modifier = Modifier,
    productRepository: ProductRepository = DummyProductRepository()
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val products = remember { productRepository.getAllProducts() }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 0 },
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
            )
            HppTabButton(
                text = "Bahan Baku",
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 1 },
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
                    text = if (selectedTab == 0) "Hitung HPP dari stok produk yang terjual." else "Hitung HPP dari bahan resep dan jumlah pemakaian.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
        
        // Spacer diperkecil agar lebih rapat
        Spacer(modifier = Modifier.height(2.dp))
        
        when (selectedTab) {
            0 -> HppStokContent(
                products = products,
                selectedProduct = selectedProduct,
                onProductSelected = { selectedProduct = it }
            )
            1 -> HppBahanBakuContent(
                products = products,
                selectedProduct = selectedProduct,
                onProductSelected = { selectedProduct = it }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tombol Hitung
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
        
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedProduct?.name ?: "",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Metode: ${if (selectedTab == 0) "Stock" else "Bahan Baku"}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Total Biaya Produksi: Rp 1.000.000",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Estimasi Penjualan per Bulan: 100 pcs",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "HPP per Produk: Rp 10.000",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Tutup")
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
    products: List<Product>,
    selectedProduct: Product?,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedProduct?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilih Produk") },
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    "dropdown arrow"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            products.forEach { product ->
                DropdownMenuItem(
                    onClick = {
                        onProductSelected(product)
                        expanded = false
                    }
                ) {
                    Text(product.name)
                }
            }
        }
    }
}

@Composable
fun HppBahanBakuContent(
    products: List<Product>,
    selectedProduct: Product?,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedProduct?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilih Produk") },
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    "dropdown arrow"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            products.forEach { product ->
                DropdownMenuItem(
                    onClick = {
                        onProductSelected(product)
                        expanded = false
                    }
                ) {
                    Text(product.name)
                }
            }
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