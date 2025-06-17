package com.example.omsetku.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.omsetku.navigation.Routes
import com.example.omsetku.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Warna-warna untuk gradient
    val primaryColor = Color(0xFF5ED0C5)    // Hijau Omsetku
    val secondaryColor = Color(0xFF2A9D8F)  // Hijau gelap
    val accentColor = Color(0xFF98E5DC)     // Hijau muda

    // Animasi untuk fade in dengan durasi lebih lama
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2500,  // Durasi fade in diperpanjang
            easing = FastOutSlowInEasing
        ), label = ""
    )

    // Animasi untuk scaling dengan efek bounce yang lebih lambat
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow  // Mengurangi stiffness agar lebih lambat
        ), label = ""
    )

    LaunchedEffect(key1 = true) {
        delay(1000) // Delay sebelum memulai animasi
        startAnimation = true
        delay(4000) // Menunggu animasi selesai lebih lama
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor,
                        accentColor
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(250.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            )
        }
    }
} 