package com.example.omsetku.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import com.example.omsetku.R
import com.example.omsetku.data.Transaction
import com.example.omsetku.ui.theme.IncomeColor
import com.example.omsetku.ui.theme.ExpenseColor
import com.example.omsetku.ui.theme.PrimaryVariant
import com.example.omsetku.ui.theme.Divider as DividerColor

@SuppressLint("SuspiciousIndentation")
@Composable
fun TransactionList(transactions: List<Transaction>) {
    var isExpanded by remember { mutableStateOf(true) }
    
    // Tinggi navbar
    val navbarHeight = 56.dp
    
    // Posisi maksimum bar saat tertutup (akan berada tepat di atas navbar)
    val barClosedPosition = 500.dp  // Posisi cukup jauh ke bawah tapi masih tampak di layar
    
    // Animasi untuk konten dan bar
    val contentOffset by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 400.dp, // Konten bergerak hingga tak terlihat
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "contentOffset"
    )
    
    // Animasi bar - akan berhenti di posisi tertentu
    val barOffset by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 
                      // Saat tidak expanded, bar hanya turun sedikit dan berhenti di atas navbar
                      navbarHeight + 450.dp,  
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "barOffset"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = navbarHeight + 24.dp) // Padding untuk memastikan ada ruang di bawah
    ) {
        // Konten transaksi
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = contentOffset)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(top = 40.dp) // Ruang untuk bar
                    .padding(bottom = 16.dp)
                    .zIndex(0f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
        
        // Bar yang selalu terlihat dan dapat diakses - terpisah dari konten
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = barOffset)
                .heightIn(min = 40.dp) // Memastikan bar selalu punya tinggi minimal
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .zIndex(1f) // Selalu di atas konten
        ) {
            // Bar handler
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { 
                        isExpanded = !isExpanded 
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(6.dp)
                        .background(
                            color = if (isExpanded) PrimaryVariant else Color.LightGray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
            
            // Divider
            Divider(color = DividerColor, thickness = 1.dp)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (transaction.type == "Pemasukan") 
                  IncomeColor.copy(alpha = 0.05f) 
                else 
                  ExpenseColor.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (transaction.type == "Pemasukan") 
                                IncomeColor.copy(alpha = 0.15f) 
                            else 
                                ExpenseColor.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (transaction.type == "Pemasukan") R.drawable.income_icon
                            else R.drawable.outcome_icon
                        ),
                        contentDescription = null,
                        tint = if (transaction.type == "Pemasukan") IncomeColor else ExpenseColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (transaction.type == "Pemasukan") "+Rp " else "-Rp ") +
                            "%,d".format(transaction.amount).replace(',', '.'),
                    fontSize = 14.sp,
                    color = if (transaction.type == "Pemasukan") IncomeColor else ExpenseColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.date,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}
