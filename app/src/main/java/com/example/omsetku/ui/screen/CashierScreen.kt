package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins

data class ProductItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int,
    var quantity: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Cashier") }
    var searchQuery by remember { mutableStateOf("") }
    var productList by remember { 
        mutableStateOf(
            listOf(
                ProductItem(1, "Cappucino", 25000, R.drawable.logo, 1),
                ProductItem(2, "Americano", 20000, R.drawable.logo, 1),
                ProductItem(3, "Espresso", 15000, R.drawable.logo, 1),
                ProductItem(4, "Brown Sugar Latte", 15000, R.drawable.logo, 1),
                ProductItem(5, "Matcha Latte", 15000, R.drawable.logo, 0),
                ProductItem(6, "Nasi Goreng", 15000, R.drawable.logo, 0),
                ProductItem(7, "Nasi Goreng", 15000, R.drawable.logo, 0),
                ProductItem(8, "Nasi Goreng", 15000, R.drawable.logo, 0)
            )
        ) 
    }
    
    val totalItems = productList.sumOf { it.quantity }
    val hasSelectedItems = totalItems > 0

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> navController.navigate(Routes.HOME)
                        "Cashier" -> { /* Sudah di layar Cashier */ }
                        "Transaction" -> navController.navigate(Routes.TRANSACTION)
                        "HPP" -> navController.navigate(Routes.HPP)
                        "Report" -> navController.navigate(Routes.REPORT)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Kasir",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = Poppins
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            "Cari Produk", 
                            fontFamily = Poppins,
                            color = Color.Gray
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF5ED0C5)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5ED0C5),
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color(0xFF5ED0C5)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tambah Produk Button
                    ElevatedButton(
                        onClick = { /* TODO: Tambah produk */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF5ED0C5)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Produk",
                            tint = Color(0xFF5ED0C5)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tambah Produk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                    }
                    
                    // Atur Produk Button
                    ElevatedButton(
                        onClick = { /* TODO: Atur produk */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF5ED0C5)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Atur Produk",
                            tint = Color(0xFF5ED0C5)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Atur Produk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Product Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = if(hasSelectedItems) 80.dp else 16.dp)
                ) {
                    items(productList) { product ->
                        ProductCard(
                            product = product,
                            onIncrement = { 
                                productList = productList.map { 
                                    if (it.id == product.id) it.copy(quantity = it.quantity + 1)
                                    else it
                                }
                            },
                            onDecrement = {
                                if (product.quantity > 0) {
                                    productList = productList.map { 
                                        if (it.id == product.id) it.copy(quantity = it.quantity - 1)
                                        else it
                                    }
                                }
                            }
                        )
                    }
                }
            }
            
            // Bottom Transaction Button
            if (hasSelectedItems) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    color = Color(0xFF5ED0C5),
                    shape = RoundedCornerShape(30.dp),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Proses Transaksi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White
                            ) {
                                Text(
                                    "$totalItems",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5ED0C5),
                                    fontFamily = Poppins,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_down),
                                contentDescription = "Arrow",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Product Name
            Text(
                text = product.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = Poppins
            )
            
            // Product Price
            Text(
                text = "Rp ${product.price / 1000}k",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF5ED0C5),
                fontFamily = Poppins
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (product.quantity > 0) Color(0xFFE8F7F5) else Color(0xFFF0F0F0),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.clickable { onDecrement() }
                    ) {
                        Text(
                            text = "-",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.quantity > 0) Color(0xFF5ED0C5) else Color.Gray
                        )
                    }
                }
                
                Text(
                    text = "${product.quantity}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
                
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE8F7F5),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.clickable { onIncrement() }
                    ) {
                        Text(
                            text = "+",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5ED0C5)
                        )
                    }
                }
            }
        }
    }
}