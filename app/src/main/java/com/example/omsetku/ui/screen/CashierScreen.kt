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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.BottomNavBar
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant
import androidx.compose.foundation.verticalScroll
import com.example.omsetku.ui.data.ProductItem
import com.example.omsetku.viewmodels.ProductViewModel
import com.example.omsetku.viewmodels.CartViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    var selectedItem by remember { mutableStateOf("Cashier") }
    var searchQuery by remember { mutableStateOf("") }

    // Mengambil data produk dari ViewModel
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    // Mengambil data keranjang dari CartViewModel
    val cartItems by cartViewModel.cartItems.collectAsState()

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
    var selectedProduct by remember { mutableStateOf<ProductItem?>(null) }
    var productToDelete by remember { mutableStateOf<ProductItem?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var isAddProductSelected by remember { mutableStateOf(false) }
    var isManageProductSelected by remember { mutableStateOf(false) }

    // Selalu update totalItems ketika cartItems berubah
    val totalItems = remember(cartItems) { cartViewModel.getTotalItems() }
    val hasSelectedItems = totalItems > 0

    // Efek untuk memuat produk saat pertama kali
    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
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
                            IconButton(
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
                                        // Implementasi yang benar untuk update quantity
                                        if (newQuantity > 0) {
                                            cartViewModel.updateQuantity(product.id.toString(), newQuantity)
                                        } else {
                                            cartViewModel.removeFromCart(product.id.toString())
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

                        Surface(
                            modifier = Modifier
                                .size(24.dp),
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
                                    color = PrimaryVariant
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
            onDismiss = { showAddProductDialog = false },
            onConfirm = { name, price, _ ->
                productViewModel.addProduct(name, price.toInt())
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
            onConfirm = { name, price, _ ->
                productViewModel.editProduct(selectedProduct!!.id, name, price.toInt())
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
                productViewModel.deleteProduct(productToDelete!!.id)
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
    onConfirm: (name: String, price: String, imageUri: Uri?) -> Unit
) {
    var productName by remember { mutableStateOf(initialProduct?.name ?: "") }
    var productPrice by remember { mutableStateOf(initialProduct?.price?.toString() ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageCropper by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showImageCropper = true
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(1.10f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
                    .verticalScroll(rememberScrollState())
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
                        .height(180.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        // Tampilkan gambar yang dipilih
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay untuk edit
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Klik untuk mengubah gambar",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Poppins
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_up),
                                contentDescription = "Upload",
                                tint = PrimaryVariant,
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
                    onValueChange = {
                        // Hanya menerima angka
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            productPrice = it
                        }
                    },
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
                        onClick = { onConfirm(productName, productPrice, selectedImageUri) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = productName.isNotBlank() && productPrice.isNotBlank()
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

    if (showImageCropper && selectedImageUri != null) {
        ImageCropperDialog(
            imageUri = selectedImageUri!!,
            onDismiss = { showImageCropper = false },
            onCropComplete = { croppedUri ->
                selectedImageUri = croppedUri
                showImageCropper = false
            }
        )
    }
}

@Composable
fun ImageCropperDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onCropComplete: (Uri) -> Unit
) {
    val context = LocalContext.current

    // State untuk transformasi gambar
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }

    // State transformable untuk gesture
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        offset += offsetChange
        rotation += rotationChange
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sesuaikan Gambar",
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

                // Image cropper area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(imageUri)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Crop Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                                rotationZ = rotation
                            }
                            .transformable(state = state),
                        contentScale = ContentScale.Fit
                    )

                    // Crop overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                    )
                }

                // Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismiss,
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
                        onClick = {
                            // Dalam implementasi nyata, kita akan menyimpan gambar yang sudah di-crop
                            // Untuk contoh ini, kita hanya meneruskan URI yang sama
                            onCropComplete(imageUri)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Terapkan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins,
                            color = Color.White
                        )
                    }
                }

                // Instruksi
                Text(
                    text = "Geser, cubit untuk zoom, dan putar untuk menyesuaikan gambar",
                    fontSize = 12.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
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
    onDelete: () -> Unit = {},
    cartViewModel: CartViewModel
) {
    // State untuk quantity lokal
    var quantity by remember { mutableStateOf(0) }

    // Update state lokal dari keranjang
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartItem = cartItems.find { it.productId == product.id.toString() }

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
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isEditMode) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Edit Button
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(32.dp)
                                .background(PrimaryVariant.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = PrimaryVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Delete Button
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Red.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Minus Button
                        IconButton(
                            onClick = {
                                if (quantity > 0) {
                                    quantity--
                                    cartViewModel.updateQuantity(product.id.toString(), quantity)
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = if (quantity > 0) PrimaryVariant else Color.LightGray,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_remove),
                                contentDescription = "Decrease",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Quantity
                        Text(
                            text = quantity.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            modifier = Modifier.width(35.dp),
                            textAlign = TextAlign.Center
                        )

                        // Plus Button
                        IconButton(
                            onClick = {
                                quantity++
                                if (quantity == 1) {
                                    // Jika baru ditambahkan, gunakan addToCart
                                    cartViewModel.addToCart(
                                        com.example.omsetku.models.Product(
                                            id = product.id.toString(),
                                            name = product.name,
                                            price = product.price.toLong(),
                                            imageRes = product.imageRes
                                        ),
                                        1
                                    )
                                } else {
                                    // Jika sudah ada, update quantity
                                    cartViewModel.updateQuantity(product.id.toString(), quantity)
                                }

                                // Panggil callback untuk memberitahu parent
                                onQuantityChanged(quantity)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(PrimaryVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
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