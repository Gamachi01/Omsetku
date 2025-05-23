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

object OmsetkuTheme {
    val Colors = object {
        val PrimaryColor = PrimaryColor
        val PrimaryVariant = PrimaryVariant
        val PrimaryLight = PrimaryLight
        val SecondaryColor = Color(0xFF2F7E68)
        val BackgroundColor = BackgroundColor
        val SurfaceColor = SurfaceColor
        val ErrorColor = Color(0xFFE74C3C)
        val SuccessColor = Color(0xFF08C39F)
        val IncomeColor = IncomeColor
        val ExpenseColor = ExpenseColor
        val TextPrimaryColor = TextPrimaryColor
        val TextSecondaryColor = Color.Gray
        val BorderColor = Color.LightGray
        val White = White
        val DarkText = DarkText
        val Divider = Divider
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
    primary = OmsetkuTheme.Colors.PrimaryColor,
    secondary = OmsetkuTheme.Colors.PrimaryVariant,
    tertiary = OmsetkuTheme.Colors.IncomeColor,
    background = OmsetkuTheme.Colors.BackgroundColor,
    surface = OmsetkuTheme.Colors.SurfaceColor,
    onPrimary = OmsetkuTheme.Colors.White,
    onSecondary = OmsetkuTheme.Colors.White,
    onTertiary = OmsetkuTheme.Colors.White,
    onBackground = OmsetkuTheme.Colors.DarkText,
    onSurface = OmsetkuTheme.Colors.DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = OmsetkuTheme.Colors.PrimaryColor,
    primaryContainer = OmsetkuTheme.Colors.PrimaryLight,
    secondary = OmsetkuTheme.Colors.SecondaryColor,
    background = OmsetkuTheme.Colors.BackgroundColor,
    surface = OmsetkuTheme.Colors.SurfaceColor,
    error = OmsetkuTheme.Colors.ErrorColor,
    onPrimary = OmsetkuTheme.Colors.White,
    onSecondary = OmsetkuTheme.Colors.White,
    onBackground = OmsetkuTheme.Colors.TextPrimaryColor,
    onSurface = OmsetkuTheme.Colors.TextPrimaryColor,
    onError = OmsetkuTheme.Colors.White
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