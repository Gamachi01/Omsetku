package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.PrimaryLight
import androidx.compose.foundation.verticalScroll

data class ProductItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int,
    var quantity: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf("Cashier") }
    var searchQuery by remember { mutableStateOf("") }
    
    // Menambahkan data produk dummy untuk demo
    var productList by remember {
        mutableStateOf<List<ProductItem>>(
            listOf(
                ProductItem(1, "Cappucino", 25000, R.drawable.logo),
                ProductItem(2, "Americano", 20000, R.drawable.logo),
                ProductItem(3, "Espresso", 15000, R.drawable.logo),
                ProductItem(4, "Brown Sugar Latte", 15000, R.drawable.logo)
            )
        )
    }
    
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProductDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ProductItem?>(null) }
    var productToDelete by remember { mutableStateOf<ProductItem?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    
    val totalItems = productList.sumOf { it.quantity }
    val hasSelectedItems = totalItems > 0
    val selectedProducts = productList.filter { it.quantity > 0 }

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
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                            tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = PrimaryVariant
                    ),
                    singleLine = true
                )
                
                // Button Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button Tambah Produk
                    Button(
                        onClick = { showAddProductDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryVariant
                        )
                    ) {
                        Text(
                            text = "Tambah Produk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Button Atur Produk
                    OutlinedButton(
                        onClick = { 
                            if (productList.isNotEmpty()) {
                                isEditMode = !isEditMode
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isEditMode) Color.White else Color(0xFF62DCC8),
                            containerColor = if (isEditMode) Color(0xFF62DCC8) else Color.White
                        ),
                        border = BorderStroke(1.dp, Color(0xFF62DCC8)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.point_of_sale),
                            contentDescription = "Atur Produk",
                            tint = if (isEditMode) Color.White else Color(0xFF62DCC8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Atur Produk",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = if (isEditMode) Color.White else Color(0xFF62DCC8)
                        )
                    }
                }
                
                if (productList.isEmpty()) {
                    // Empty State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Oops! Belum ada produk.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "Tambahkan produk terlebih dahulu.",
                                fontSize = 14.sp,
                                fontFamily = Poppins,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Product Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = if (hasSelectedItems) 80.dp else 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productList) { product ->
                            ProductCard(
                                product = product,
                                isEditMode = isEditMode,
                                onQuantityChanged = { newQuantity ->
                                    productList = productList.map {
                                        if (it.id == product.id) it.copy(quantity = newQuantity) else it
                                    }
                                },
                                onEdit = {
                                    selectedProduct = product
                                    showEditProductDialog = true
                                },
                                onDelete = {
                                    productToDelete = product
                                    showDeleteConfirmDialog = true
                                }
                            )
                        }
                    }
                }
            }
            
            // Process Transaction Button
            if (hasSelectedItems) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(30.dp),
                    color = PrimaryVariant,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .clickable {
                                navController.navigate(Routes.TRANSACTION_DETAIL)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Proses Transaksi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        "$totalItems",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryVariant,
                                        fontFamily = Poppins
                                    )
                                }
                            }
                            
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
    
    // Add Product Dialog
    if (showAddProductDialog) {
        ProductDialog(
            isNewProduct = true,
            initialProduct = null,
            onDismiss = { showAddProductDialog = false },
            onConfirm = { name, price ->
                val newId = if (productList.isEmpty()) 1 else productList.maxOf { it.id } + 1
                productList = productList + ProductItem(
                    id = newId,
                    name = name,
                    price = price.toInt(),
                    imageRes = R.drawable.logo
                )
                showAddProductDialog = false
            }
        )
    }
    
    // Edit Product Dialog
    if (showEditProductDialog && selectedProduct != null) {
        ProductDialog(
            isNewProduct = false,
            initialProduct = selectedProduct,
            onDismiss = { showEditProductDialog = false },
            onConfirm = { name, price ->
                productList = productList.map {
                    if (it.id == selectedProduct!!.id) {
                        it.copy(name = name, price = price.toInt())
                    } else {
                        it
                    }
                }
                showEditProductDialog = false
                selectedProduct = null
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog && productToDelete != null) {
        DeleteConfirmationDialog(
            productName = productToDelete!!.name,
            onDismiss = { 
                showDeleteConfirmDialog = false
                productToDelete = null
            },
            onConfirm = {
                productList = productList.filter { it.id != productToDelete!!.id }
                showDeleteConfirmDialog = false
                productToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    isNewProduct: Boolean,
    initialProduct: ProductItem?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: String) -> Unit
) {
    var productName by remember { mutableStateOf(initialProduct?.name ?: "") }
    var productPrice by remember { mutableStateOf(initialProduct?.price?.toString() ?: "") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isNewProduct) "Tambah Produk Baru" else "Edit Produk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Image Upload Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { /* TODO: Handle image upload */ }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_up),
                            contentDescription = "Upload",
                            tint = Color(0xFF62DCC8),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Klik untuk mengupload gambar produk",
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Support: JPG, JPEG, PNG",
                            fontSize = 10.sp,
                            fontFamily = Poppins,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Product Name
                Text(
                    text = "Nama Produk",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Product Price
                Text(
                    text = "Harga",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    leadingIcon = { 
                        Text(
                            text = "Rp",
                            fontFamily = Poppins,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Batal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Button(
                        onClick = { onConfirm(productName, productPrice) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF62DCC8)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isNewProduct) "Tambah Produk" else "Simpan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductItem,
    isEditMode: Boolean = false,
    onQuantityChanged: (Int) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Name
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = Poppins
            )
            
            // Product Price
            Text(
                text = "Rp ${product.price}",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PrimaryVariant,
                fontFamily = Poppins
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quantity Controls atau Edit/Delete buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isEditMode) {
                    // Edit button (pengganti tombol minus)
                    Surface(
                        shape = CircleShape,
                        color = PrimaryVariant,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable { onEdit() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    // Delete button (pengganti tombol plus)
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE74C3C), // Warna merah untuk delete
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable { onDelete() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // Normal quantity controls
                    Surface(
                        shape = CircleShape,
                        color = PrimaryVariant,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable { 
                                if (product.quantity > 0) {
                                    onQuantityChanged(product.quantity - 1)
                                }
                            }
                        ) {
                            Text(
                                text = "-",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                        color = PrimaryVariant,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable { onQuantityChanged(product.quantity + 1) }
                        ) {
                            Text(
                                text = "+",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    productName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x80000000)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Hapus Produk?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Apakah Anda yakin ingin menghapus produk \"$productName\"?",
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Batal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.Black
                            )
                        }
                        
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE74C3C)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Hapus",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}