package com.example.omsetku.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.ui.components.Poppins

// Secara eksplisit mengimpor nilai warna dari Color.kt
import com.example.omsetku.ui.theme.PrimaryColor
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.PrimaryLight
import com.example.omsetku.ui.theme.BackgroundColor
import com.example.omsetku.ui.theme.SurfaceColor
import com.example.omsetku.ui.theme.IncomeColor
import com.example.omsetku.ui.theme.ExpenseColor
import com.example.omsetku.ui.theme.TextPrimaryColor
import com.example.omsetku.ui.theme.White
import com.example.omsetku.ui.theme.DarkText
import com.example.omsetku.ui.theme.Divider

object OmsetkuTheme {
    val Colors = object {
        val PrimaryColor: Color = com.example.omsetku.ui.theme.PrimaryColor
        val PrimaryVariant: Color = com.example.omsetku.ui.theme.PrimaryVariant
        val PrimaryLight: Color = com.example.omsetku.ui.theme.PrimaryLight
        val SecondaryColor: Color = Color(0xFF2F7E68)
        val BackgroundColor: Color = com.example.omsetku.ui.theme.BackgroundColor
        val SurfaceColor: Color = com.example.omsetku.ui.theme.SurfaceColor
        val ErrorColor: Color = Color(0xFFE74C3C)
        val SuccessColor: Color = Color(0xFF08C39F)
        val IncomeColor: Color = com.example.omsetku.ui.theme.IncomeColor
        val ExpenseColor: Color = com.example.omsetku.ui.theme.ExpenseColor
        val TextPrimaryColor: Color = com.example.omsetku.ui.theme.TextPrimaryColor
        val TextSecondaryColor: Color = Color.Gray
        val BorderColor: Color = Color.LightGray
        val White: Color = com.example.omsetku.ui.theme.White
        val DarkText: Color = com.example.omsetku.ui.theme.DarkText
        val Divider: Color = com.example.omsetku.ui.theme.Divider
    }

    val Typography = androidx.compose.material3.Typography(
        headlineLarge = TextStyle(
            fontFamily = Poppins,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineMedium = TextStyle(
            fontFamily = Poppins,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineSmall = TextStyle(
            fontFamily = Poppins,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        titleLarge = TextStyle(
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        titleMedium = TextStyle(
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        ),
        bodyLarge = TextStyle(
            fontFamily = Poppins,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Poppins,
            fontSize = 14.sp
        ),
        labelLarge = TextStyle(
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    )

    val Shapes = androidx.compose.material3.Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp)
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = PrimaryVariant,
    tertiary = IncomeColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = DarkText,
    onSurface = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryLight,
    secondary = Color(0xFF2F7E68),
    background = BackgroundColor,
    surface = SurfaceColor,
    error = ExpenseColor,
    onPrimary = White,
    onSecondary = White,
    onBackground = TextPrimaryColor,
    onSurface = TextPrimaryColor,
    onError = White
)

@Composable
fun OmsetkuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OmsetkuTheme.Typography,
        shapes = OmsetkuTheme.Shapes,
        content = content
    )
}

// Alias untuk kompatibilitas - untuk digunakan sementara sampai semua referensi diubah
@Composable
fun OmsetkuThemeComposable(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    OmsetkuTheme(darkTheme, dynamicColor, content)
}