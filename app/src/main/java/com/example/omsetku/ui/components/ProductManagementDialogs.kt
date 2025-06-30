package com.example.omsetku.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.omsetku.R
import com.example.omsetku.models.Product
import com.example.omsetku.ui.theme.PrimaryVariant
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import com.example.omsetku.ui.components.CustomImageCropper

fun saveCroppedBitmapToFile(context: android.content.Context, croppedBitmap: Bitmap): String {
    val fileName = "product_${System.currentTimeMillis()}.png"
    val file = java.io.File(context.filesDir, fileName)
    val outputStream = java.io.FileOutputStream(file)
    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return file.absolutePath
}

@Composable
fun ProductDialog(
    isNewProduct: Boolean,
    initialProduct: Product?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: String, imagePath: String?) -> Unit
) {
    var productName by remember { mutableStateOf(initialProduct?.name ?: "") }
    var productPrice by remember { mutableStateOf(initialProduct?.price?.toString() ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var croppedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var showImageCropper by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showImageCropper = true
        }
    }

    if (showImageCropper && selectedImageUri != null) {
        CustomImageCropper(
            imageUri = selectedImageUri!!,
            cropSizeDp = 140.dp,
            onCrop = { bmp ->
                croppedBitmap = bmp
                showImageCropper = false
            },
            onCancel = {
                showImageCropper = false
            }
        )
        return
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .widthIn(min = 320.dp, max = 500.dp)
                .heightIn(min = 560.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isNewProduct) "Tambah Produk Baru" else "Edit Produk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5))
                        .align(Alignment.CenterHorizontally)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        croppedBitmap != null -> {
                            Image(
                                bitmap = croppedBitmap!!.asImageBitmap(),
                                contentDescription = "Cropped Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        selectedImageUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Selected Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        initialProduct?.imageUrl?.isNotEmpty() == true -> {
                            Image(
                                painter = rememberAsyncImagePainter(initialProduct.imageUrl),
                                contentDescription = "Product Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_image_placeholder),
                                contentDescription = "Placeholder",
                                tint = Color.LightGray,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Nama Produk",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = Poppins
                    )
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Harga",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                StandardTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    modifier = Modifier.fillMaxWidth(),
                    isRupiah = true,
                    placeholder = "Rp"
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Batal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Button(
                        onClick = {
                            val imagePath = if (croppedBitmap != null) {
                                saveCroppedBitmapToFile(context, croppedBitmap!!)
                            } else if (selectedImageUri != null) {
                                val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                                val fileName = "product_${System.currentTimeMillis()}.png"
                                val file = java.io.File(context.filesDir, fileName)
                                val outputStream = java.io.FileOutputStream(file)
                                inputStream?.copyTo(outputStream)
                                inputStream?.close()
                                outputStream.close()
                                file.absolutePath
                            } else initialProduct?.imageUrl
                            onConfirm(productName, productPrice, imagePath)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = productName.isNotBlank() && productPrice.isNotBlank()
                    ) {
                        Text(
                            text = if (isNewProduct) "Tambah Produk" else "Simpan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
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