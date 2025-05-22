package com.example.omsetku.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.omsetku.R
import com.example.omsetku.data.Transaction
import com.example.omsetku.ui.theme.IncomeColor
import com.example.omsetku.ui.theme.ExpenseColor
import com.example.omsetku.ui.theme.Background
import com.example.omsetku.ui.theme.Divider as DividerColor

@SuppressLint("SuspiciousIndentation")
@Composable
fun TransactionList(transactions: List<Transaction>) {
    val density = LocalDensity.current
    var isExpanded by remember { mutableStateOf(true) }
    var contentHeight by remember { mutableStateOf(0.dp) }
    val listHeight = remember(contentHeight, isExpanded) {
        if (isExpanded) contentHeight else 0.dp
    }
    
    // Animasi untuk mengubah tinggi konten
    val animatedHeight by animateDpAsState(
        targetValue = listHeight,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "contentHeight"
    )
    
    // Frame utama
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Konten transaksi yang terlihat atau tersembunyi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedHeight)
                    .onGloballyPositioned { coordinates ->
                        // Hanya update height saat expanded, untuk mencegah height menjadi 0 sebelum animasi
                        if (isExpanded) {
                            with(density) {
                                contentHeight = coordinates.size.height.toDp()
                            }
                        }
                    }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Konten LazyColumn
                    val listState = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transactions) { transaction ->
                            TransactionItem(transaction)
                        }
                    }
                }
            }
            
            // Divider yang selalu terlihat
            Divider(
                color = DividerColor,
                thickness = 1.dp
            )
            
            // Bar abu-abu yang berfungsi sebagai handle (selalu di bawah)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { isExpanded = !isExpanded },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(6.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
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
