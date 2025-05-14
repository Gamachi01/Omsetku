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
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.PrimaryLight
import com.example.omsetku.ui.theme.Background

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
                .background(Background)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Kasir",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = Poppins
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                            tint = PrimaryVariant
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryVariant,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = PrimaryVariant
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tambah Produk Button
                    ElevatedButton(
                        onClick = { /* TODO: Tambah produk */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = PrimaryVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Produk",
                            tint = PrimaryVariant
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
                            .height(52.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = PrimaryVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Atur Produk",
                            tint = PrimaryVariant
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Product Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(productList) { product ->
                        ProductCard(product = product, onQuantityChanged = { newQuantity ->
                            productList = productList.map {
                                if (it.id == product.id) it.copy(quantity = newQuantity) else it
                            }
                        })
                    }
                }
            }
            
            // Checkout Button
            if (hasSelectedItems) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryVariant,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$totalItems produk",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = Poppins
                            )
                            Text(
                                text = "Rp %,d".format(productList.sumOf { it.price * it.quantity }).replace(',', '.'),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = Poppins
                            )
                        }
                        
                        Button(
                            onClick = { /* TODO: Process checkout */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Checkout",
                                color = PrimaryVariant,
                                fontWeight = FontWeight.Bold,
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
fun ProductCard(
    product: ProductItem,
    onQuantityChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryLight)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Rp %,d".format(product.price).replace(',', '.'),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryVariant,
                fontFamily = Poppins
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quantity controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease button
                IconButton(
                    onClick = { 
                        if (product.quantity > 0) {
                            onQuantityChanged(product.quantity - 1)
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (product.quantity > 0) PrimaryLight else Color.LightGray)
                ) {
                    Text(
                        text = "-",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (product.quantity > 0) PrimaryVariant else Color.Gray
                    )
                }
                
                // Quantity
                Text(
                    text = "${product.quantity}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
                
                // Increase button
                IconButton(
                    onClick = { onQuantityChanged(product.quantity + 1) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                ) {
                    Text(
                        text = "+",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryVariant
                    )
                }
            }
        }
    }
}