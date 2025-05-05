package com.example.omsetku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.R

@Composable
fun BottomNavBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf("Home", "Cashier", "Transaction", "HPP", "Report")
    val icons = listOf(
        R.drawable.home,
        R.drawable.point_of_sale,
        R.drawable.add_circle,
        R.drawable.calculate,
        R.drawable.monitoring
    )

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        containerColor = Color(0xFFF8F8F8)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == item
                val activeColor = Color(0xFF2F7E68)
                val inactiveColor = Color.Gray

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onItemSelected(item) }
                        .padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(if (isSelected) activeColor else inactiveColor)
                    )
                    Text(
                        text = item,
                        fontSize = 12.sp,
                        color = if (isSelected) activeColor else inactiveColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
