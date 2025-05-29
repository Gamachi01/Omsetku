package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pusat Bantuan",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contact Support Section
            item {
                Text(
                    text = "Hubungi Kami",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SupportCard(
                    icon = Icons.Default.Email,
                    title = "Email",
                    subtitle = "support@omsetku.com",
                    onClick = { /* Handle email click */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SupportCard(
                    icon = Icons.Default.Phone,
                    title = "Telepon",
                    subtitle = "+62 812-3456-7890",
                    onClick = { /* Handle phone click */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SupportCardWithDrawable(
                    iconRes = R.drawable.whatsapp,
                    title = "WhatsApp",
                    subtitle = "Chat dengan customer service",
                    onClick = { /* Handle WhatsApp click */ }
                )
            }

            // FAQ Section
            item {
                Text(
                    text = "FAQ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                FAQItem(
                    question = "Bagaimana cara menambahkan produk?",
                    answer = "Anda dapat menambahkan produk melalui menu Produk dengan mengklik tombol + dan mengisi detail produk yang diperlukan."
                )
                Spacer(modifier = Modifier.height(8.dp))
                FAQItem(
                    question = "Bagaimana cara melihat laporan penjualan?",
                    answer = "Laporan penjualan dapat dilihat melalui menu Laporan. Anda dapat memilih periode waktu dan jenis laporan yang diinginkan."
                )
                Spacer(modifier = Modifier.height(8.dp))
                FAQItem(
                    question = "Cara mengatur notifikasi?",
                    answer = "Pengaturan notifikasi dapat diakses melalui menu Pengaturan > Notifikasi. Anda dapat mengaktifkan atau menonaktifkan jenis notifikasi yang diinginkan."
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5ED0C5),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportCardWithDrawable(
    iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more",
                    tint = Color(0xFF5ED0C5)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }
    }
} 