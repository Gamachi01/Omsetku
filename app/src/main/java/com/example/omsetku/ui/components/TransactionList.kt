package com.example.omsetku.ui.components


import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 16.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(6.dp)
                    .clickable { isExpanded = !isExpanded }
                    .background(
                        color = if (isExpanded) IncomeColor else Color.LightGray,
                        shape = RoundedCornerShape(50)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Transaksi Terakhir",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins
            )
            
            Text(
                text = if (isExpanded) "Sembunyikan" else "Lihat Semua",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = IncomeColor,
                fontFamily = Poppins,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        }

        if (isExpanded) {
            Divider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            
            val listState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                state = listState,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        } else {
            // Menampilkan hanya beberapa transaksi terbaru jika tidak expanded
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                transactions.take(3).forEach { transaction ->
                    TransactionItem(transaction)
                    if (transaction != transactions.take(3).last()) {
                        Divider(
                            color = DividerColor.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TransactionItem(transaction: Transaction) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (transaction.type == "Pemasukan") 
                  IncomeColor.copy(alpha = 0.05f) 
                else 
                  ExpenseColor.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = transaction.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = Poppins
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = transaction.description,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (transaction.type == "Pemasukan") "+Rp " else "-Rp ") +
                            "%,d".format(transaction.amount).replace(',', '.'),
                    fontSize = 15.sp,
                    color = if (transaction.type == "Pemasukan") IncomeColor else ExpenseColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = transaction.date,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}
