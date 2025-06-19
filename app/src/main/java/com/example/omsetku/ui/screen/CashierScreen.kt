package com.example.omsetku.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.omsetku.navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.components.StandardTextField
import com.example.omsetku.ui.theme.PrimaryVariant
import androidx.compose.foundation.verticalScroll
import com.example.omsetku.models.Product
import com.example.omsetku.viewmodels.ProductViewModel
import com.example.omsetku.viewmodels.CartViewModel
import com.example.omsetku.viewmodels.HppViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import com.example.omsetku.firebase.FirestoreRepository
import kotlinx.coroutines.launch
import com.example.omsetku.ui.components.ProductDialog
import com.example.omsetku.ui.components.DeleteConfirmationDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.ripple.rememberRipple
import com.example.omsetku.ui.components.ProfitAlertDialog
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CashierScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    hppViewModel: HppViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("Cashier") }
    var searchQuery by remember { mutableStateOf("") }
    var showImageCropper by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Mengambil data produk dari ViewModel
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    // Mengambil data keranjang dari CartViewModel
    val cartItems by cartViewModel.cartItems.collectAsState()

    // Mengambil margin profit dari HppViewModel
    val marginProfit by hppViewModel.marginProfit.collectAsState()

    // Filter produk berdasarkan pencarian
    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProductDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var isAddProductSelected by remember { mutableStateOf(false) }
    var isManageProductSelected by remember { mutableStateOf(false) }

    // Selalu update totalItems ketika cartItems berubah
    val totalItems = remember(cartItems) { cartViewModel.getTotalItems() }
    val hasSelectedItems = totalItems > 0

    // Tambahkan state untuk dialog profit
    var showProfitAlert by remember { mutableStateOf(false) }
    var transactionProfit by remember { mutableStateOf(0.0) }

    // Efek untuk memuat produk saat pertama kali
    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    // Tambahkan efek untuk reset button Tambah Produk setelah dialog ditutup
    LaunchedEffect(showAddProductDialog) {
        if (!showAddProductDialog) {
            isAddProductSelected = false
        }
    }

    // Tambahkan efek untuk reset button Atur Produk setelah mode edit selesai
    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            isManageProductSelected = false
        }
    }

    // Efek untuk menampilkan profit alert setelah transaksi sukses
    LaunchedEffect(cartViewModel.transactionSuccess) {
        if (cartViewModel.transactionSuccess.value) {
            val marginProfitValue = marginProfit.toDoubleOrNull() ?: 0.0
            val totalProfit = cartViewModel.calculateTotalProfit(products, marginProfitValue)
            if (totalProfit > 0) {
                transactionProfit = totalProfit
                showProfitAlert = true
            }
            cartViewModel.resetTransactionSuccess() // Reset flag transaksi sukses
        }
    }

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
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            androidx.compose.material3.IconButton(
                                onClick = { searchQuery = "" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Color.Gray
                                )
                            }
                        }
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
                        onClick = { 
                            showAddProductDialog = true
                            isAddProductSelected = true
                            isManageProductSelected = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddProductSelected) Color.White else PrimaryVariant
                        ),
                        border = if (isAddProductSelected) BorderStroke(1.dp, PrimaryVariant) else null
                    ) {
                        Text(
                            text = "Tambah Produk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isAddProductSelected) PrimaryVariant else Color.White
                        )
                    }

                    // Button Atur Produk
                    Button(
                        onClick = {
                            if (products.isNotEmpty()) {
                                isEditMode = !isEditMode
                            }
                            isManageProductSelected = true
                            isAddProductSelected = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isManageProductSelected) Color.White else PrimaryVariant
                        ),
                        border = if (isManageProductSelected) BorderStroke(1.dp, PrimaryVariant) else null,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Atur Produk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isManageProductSelected) PrimaryVariant else Color.White
                        )
                    }
                }

                // Loading Indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryVariant)
                    }
                } else if (filteredProducts.isEmpty()) {
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = if (hasSelectedItems) 80.dp else 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    isEditMode = isEditMode,
                                    onQuantityChanged = { newQuantity ->
                                        if (newQuantity > 0) {
                                            cartViewModel.updateQuantity(product.firestoreId, newQuantity)
                                        } else {
                                            cartViewModel.removeFromCart(product.firestoreId)
                                        }
                                    },
                                    onEdit = {
                                        selectedProduct = product
                                        showEditProductDialog = true
                                    },
                                    onDelete = {
                                        productToDelete = product
                                        showDeleteConfirmDialog = true
                                    },
                                    cartViewModel = cartViewModel
                                )
                            }
                        }
                    }
                }
            }

            // Error Dialog
            if (error != null) {
                AlertDialog(
                    onDismissRequest = { productViewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(error ?: "") },
                    confirmButton = {
                        TextButton(
                            onClick = { productViewModel.clearError() }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            // Process Transaction Button
            if (hasSelectedItems) {
                Button(
                    onClick = { navController.navigate(Routes.TRANSACTION_DETAIL) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryVariant),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = totalItems.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryVariant,
                                    modifier = Modifier.offset(y = (-1).dp)
                                )
                            }
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
            onDismiss = {
                showAddProductDialog = false
                isAddProductSelected = false
            },
            onConfirm = { name, price, _ ->
                productViewModel.addProduct(name, price.toInt())
                showAddProductDialog = false
                isAddProductSelected = false
            }
        )
    }

    // Edit Product Dialog
    if (showEditProductDialog && selectedProduct != null) {
        ProductDialog(
            isNewProduct = false,
            initialProduct = selectedProduct,
            onDismiss = {
                showEditProductDialog = false
                isEditMode = false
                isManageProductSelected = false
            },
            onConfirm = { name, price, _ ->
                productViewModel.editProduct(selectedProduct!!.id, name, price.toInt())
                showEditProductDialog = false
                selectedProduct = null
                isEditMode = false
                isManageProductSelected = false
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
                productViewModel.deleteProduct(productToDelete!!.id)
                showDeleteConfirmDialog = false
                productToDelete = null
            }
        )
    }

    if (showProfitAlert) {
        ProfitAlertDialog(
            profit = transactionProfit,
            onDismiss = { showProfitAlert = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProductCard(
    product: Product,
    isEditMode: Boolean = false,
    onQuantityChanged: (Int) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    cartViewModel: CartViewModel
) {
    // State untuk quantity lokal
    var quantity by remember { mutableStateOf(0) }

    // Update state lokal dari keranjang
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartItem = cartItems.find { it.productId == product.firestoreId }

    // Efek untuk update quantity dari keranjang
    LaunchedEffect(cartItems) {
        quantity = cartItem?.quantity ?: 0
    }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    // Tampilkan gambar dari URL
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(product.imageUrl)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Tampilkan gambar default
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryVariant,
                fontFamily = Poppins,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedContent(
                    targetState = isEditMode,
                    transitionSpec = {
                        fadeIn(tween(300)) with fadeOut(tween(300))
                    }
                ) { editMode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Slot kiri
                        if (editMode) {
                        androidx.compose.material3.IconButton(
                            onClick = onEdit,
                                modifier = Modifier.size(48.dp).clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = PrimaryVariant.copy(alpha = 0.2f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = PrimaryVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                        androidx.compose.material3.IconButton(
                            onClick = {
                                if (quantity > 0) {
                                    quantity--
                                    cartViewModel.updateQuantity(product.firestoreId, quantity)
                                }
                            },
                                modifier = Modifier.size(48.dp).clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (quantity > 0) PrimaryVariant else Color.LightGray
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_remove),
                                contentDescription = "Decrease",
                                tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                            )
                            }
                        }

                        // Slot tengah
                        if (editMode) {
                            Spacer(modifier = Modifier.width(40.dp))
                        } else {
                        Text(
                            text = quantity.toString(),
                                fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                                modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        }

                        // Slot kanan
                        if (editMode) {
                            androidx.compose.material3.IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(48.dp).clip(CircleShape),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.Red.copy(alpha = 0.2f)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                        androidx.compose.material3.IconButton(
                            onClick = {
                                quantity++
                                if (quantity == 1) {
                                    cartViewModel.addToCart(product, 1)
                                } else {
                                    cartViewModel.updateQuantity(product.firestoreId, quantity)
                                }
                                onQuantityChanged(quantity)
                            },
                                modifier = Modifier.size(48.dp).clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                            )
                            }
                        }
                    }
                }
            }
        }
    }
}