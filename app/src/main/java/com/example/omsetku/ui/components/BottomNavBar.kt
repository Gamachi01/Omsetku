package com.example.omsetku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.PrimaryLight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(64.dp)
    ) {
        val items = listOf(
            BottomNavItem("Home", Icons.Default.Home),
            BottomNavItem("Cashier", Icons.Default.ShoppingCart),
            BottomNavItem("Transaction", Icons.Default.Receipt),
            BottomNavItem("HPP", Icons.Default.Calculate),
            BottomNavItem("Report", Icons.Default.Assessment)
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item.title,
                onClick = { onItemSelected(item.title) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selectedItem == item.title) Color(0xFF5ED0C5) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selectedItem == item.title) Color(0xFF5ED0C5) else Color.Gray,
                        fontFamily = Poppins
                    )
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)
