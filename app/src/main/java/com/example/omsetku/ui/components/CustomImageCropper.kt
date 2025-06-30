package com.example.omsetku.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CustomImageCropper(
    imageUri: Uri,
    cropSizeDp: Dp = 140.dp,
    onCrop: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Load bitmap from URI (asynchronously)
    LaunchedEffect(imageUri) {
        loading = true
        val req = ImageRequest.Builder(context)
            .data(imageUri)
            .allowHardware(false)
            .build()
        val result = context.imageLoader.execute(req)
        bitmap = (result as? SuccessResult)?.drawable?.toBitmap()
        loading = false
    }

    // State for zoom and pan
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Ukuran crop persegi dalam px
    val cropSizePx = with(density) { cropSizeDp.toPx() }

    // Ukuran cropper box (dialog) - 80% dari lebar layar
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels
    val boxWidthDp = with(density) { (screenWidth * 0.85f).toDp() }
    val boxHeightDp = with(density) { (screenHeight * 0.55f).toDp() }
    val boxWidthPx = with(density) { boxWidthDp.toPx() }
    val boxHeightPx = with(density) { boxHeightDp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(max = boxWidthDp)
                .background(Color.Transparent)
        ) {
            Text(
                "Atur posisi & ukuran gambar",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            )
            Box(
                modifier = Modifier
                    .width(boxWidthDp)
                    .height(boxHeightDp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    // Area cropper
                    Box(
                        modifier = Modifier
                            .size(boxHeightDp)
                            .pointerInput(bitmap) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 6f)
                                    offset += pan
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Gambar yang bisa di-zoom dan pan
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                        )
                        // Overlay area crop
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val w = size.width
                            val h = size.height
                            val crop = cropSizePx
                            val left = (w - crop) / 2f
                            val top = (h - crop) / 2f
                            // Area luar crop (gelap)
                            drawRect(Color.Black.copy(alpha = 0.55f), size = androidx.compose.ui.geometry.Size(w, top)) // Atas
                            drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(0f, top + crop), size = androidx.compose.ui.geometry.Size(w, h - (top + crop))) // Bawah
                            drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(0f, top), size = androidx.compose.ui.geometry.Size(left, crop)) // Kiri
                            drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(left + crop, top), size = androidx.compose.ui.geometry.Size(w - (left + crop), crop)) // Kanan
                            // Border persegi crop
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(left, top),
                                size = androidx.compose.ui.geometry.Size(crop, crop),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                            )
                        }
                    }
                } else if (loading) {
                    Text("Memuat gambar...", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Text("Batal", style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    enabled = bitmap != null,
                    onClick = {
                        bitmap?.let { src ->
                            // Hitung crop rect di bitmap asli
                            val cropRect = calculateCropRect(
                                src.width.toFloat(),
                                src.height.toFloat(),
                                scale,
                                offset,
                                cropSizePx,
                                boxWidthPx,
                                boxHeightPx
                            )
                            val cropped = cropBitmap(src, cropRect, cropSizePx.toInt())
                            onCrop(cropped)
                        }
                    },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Text("OK", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Hitung rect crop di bitmap asli
fun calculateCropRect(
    bmpWidth: Float,
    bmpHeight: Float,
    scale: Float,
    offset: Offset,
    cropSizePx: Float,
    boxWidthPx: Float,
    boxHeightPx: Float
): Rect {
    val baseSize = cropSizePx
    val scaledW = bmpWidth * scale
    val scaledH = bmpHeight * scale
    val left = ((scaledW - baseSize) / 2f - offset.x) / scale
    val top = ((scaledH - baseSize) / 2f - offset.y) / scale
    return Rect(left, top, left + baseSize / scale, top + baseSize / scale)
}

// Crop bitmap asli sesuai rect
fun cropBitmap(src: Bitmap, cropRect: Rect, outSize: Int): Bitmap {
    val left = cropRect.left.toInt().coerceIn(0, src.width - 1)
    val top = cropRect.top.toInt().coerceIn(0, src.height - 1)
    val right = cropRect.right.toInt().coerceIn(left + 1, src.width)
    val bottom = cropRect.bottom.toInt().coerceIn(top + 1, src.height)
    val width = (right - left).coerceAtLeast(1)
    val height = (bottom - top).coerceAtLeast(1)
    val bmp = Bitmap.createBitmap(src, left, top, width, height)
    return Bitmap.createScaledBitmap(bmp, outSize, outSize, true)
}

// Ekstensi untuk drawable ke bitmap
fun android.graphics.drawable.Drawable.toBitmap(): Bitmap {
    if (this is android.graphics.drawable.BitmapDrawable) {
        return this.bitmap
    }
    val bmp = Bitmap.createBitmap(
        intrinsicWidth.takeIf { it > 0 } ?: 1,
        intrinsicHeight.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bmp)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bmp
} 