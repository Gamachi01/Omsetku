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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.omsetku.R
import com.example.omsetku.data.Transaction

@SuppressLint("SuspiciousIndentation")
@Composable
fun TransactionList(transactions: List<Transaction>) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()
        .animateContentSize(animationSpec = tween(durationMillis = 600))) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(6.dp)
                    .clickable { isExpanded = !isExpanded }
                    .background(
                        color = if (isExpanded) Color(0xFF08C39F) else Color.LightGray,
                        shape = RoundedCornerShape(50)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Transaksi Terakhir",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))

        }

        if (isExpanded) {
            val listState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 370.dp),
                    state = listState
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }


        }
    }
}


@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(
                    id = if (transaction.type == "Pemasukan") R.drawable.income_icon
                    else R.drawable.outcome_icon
                ),
                contentDescription = null,
                tint = if (transaction.type == "Pemasukan") Color(0xFF08C39F) else Color(0xFFE74C3C),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = transaction.type,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = transaction.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = (if (transaction.type == "Pemasukan") "+Rp " else "-Rp ") +
                        "%,d".format(transaction.amount).replace(',', '.'),
                fontSize = 14.sp,
                color = if (transaction.type == "Pemasukan") Color(0xFF08C39F) else Color(0xFFE74C3C),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
