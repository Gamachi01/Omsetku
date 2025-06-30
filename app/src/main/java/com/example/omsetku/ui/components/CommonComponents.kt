package com.example.omsetku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.omsetku.R
import com.example.omsetku.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Close
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip

val Poppins = FontFamily(
    Font(R.font.poppins, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmsetkuTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun OmsetkuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFF5ED0C5))
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5ED0C5)
            )
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmsetkuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF5ED0C5),
            unfocusedBorderColor = Color.LightGray
        )
    )
}

@Composable
fun OmsetkuCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
fun OmsetkuDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
    buttons: @Composable () -> Unit
) {
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
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                content()

                Spacer(modifier = Modifier.height(24.dp))

                buttons()
            }
        }
    }
}

@Composable
fun ProfitAlertDialog(
    profit: Double,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Profit",
                    tint = Color(0xFF5ED0C5),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Profit Transaksi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = Color(0xFF5ED0C5),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Transaksi ini menghasilkan profit.",
                    fontSize = 16.sp,
                    color = Color(0xFF757575),
                    fontFamily = Poppins,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${profit.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5ED0C5),
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5ED0C5)
                    )
                ) {
                    Text("Lanjutkan", fontFamily = Poppins, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RealtimeProfitAlert(
    profit: Double,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        )
    ) {
        Card(
            modifier = modifier
                .padding(16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Profit",
                    tint = Color(0xFF5ED0C5),
                    modifier = Modifier.size(24.dp)
                )
                
                Column {
                    Text(
                        text = "Profit Saat Ini",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color(0xFF5ED0C5)
                    )
                    Text(
                        text = "Rp ${profit.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerHighlightBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    // Warna shimmer: nuansa hijau branding yang lebih soft, highlight putih tipis di tengah
    val shimmerColors = listOf(
        Color(0xFFDBFFF6).copy(alpha = 0.45f),
        Color(0xFF5ED0C5).copy(alpha = 0.32f),
        Color.White.copy(alpha = 0.09f), // highlight makin halus
        Color(0xFF5ED0C5).copy(alpha = 0.32f),
        Color(0xFFDBFFF6).copy(alpha = 0.45f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f, // area lebih lebar
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing), // lebih lama & smooth
            repeatMode = RepeatMode.Restart
        ), label = "shimmerX"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(translateAnim - 600f, 0f), // area lebih lebar
                    end = Offset(translateAnim + 600f, 400f)
                )
            )
    ) {
        content()
    }
}