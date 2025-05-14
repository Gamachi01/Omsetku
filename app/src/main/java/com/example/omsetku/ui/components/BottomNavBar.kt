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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == item
                val activeColor = Color(0xFF5ED0C5)
                val inactiveColor = Color(0xFF9E9E9E)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onItemSelected(item) }
                        .padding(4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(activeColor.copy(alpha = 0.1f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = item,
                                tint = activeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = icons[index]),
                            contentDescription = item,
                            tint = inactiveColor,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 8.dp)
                        )
                    }
                    
                    Text(
                        text = item,
                        fontSize = 11.sp,
                        color = if (isSelected) activeColor else inactiveColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
