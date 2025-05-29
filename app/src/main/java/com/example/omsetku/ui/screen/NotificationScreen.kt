package com.example.omsetku.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.ui.components.Poppins
import java.text.SimpleDateFormat
import java.util.*

data class NotificationItem(
    val title: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    // Sample notifications
    val notifications = remember {
        mutableStateListOf(
            NotificationItem(
                "Transaksi Berhasil",
                "Transaksi pembelian produk berhasil dilakukan",
                Date(System.currentTimeMillis() - 3600000) // 1 hour ago
            ),
            NotificationItem(
                "Pengingat Stok",
                "Stok produk 'Produk A' hampir habis",
                Date(System.currentTimeMillis() - 7200000) // 2 hours ago
            ),
            NotificationItem(
                "Update Aplikasi",
                "Versi terbaru Omsetku telah tersedia",
                Date(System.currentTimeMillis() - 86400000) // 1 day ago
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifikasi",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5ED0C5),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "No notifications",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tidak ada notifikasi",
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationItem) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFE8F7F6)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateFormat.format(notification.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        }
    }
} 