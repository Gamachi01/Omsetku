package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Image(
                painter = painterResource(id = R.drawable.back_icon),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_icon),
                        contentDescription = "Profile",
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Nama Pengguna",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.go_icon),
                    contentDescription = "Arrow",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MenuSection(title = "Operasional", items = listOf(
            MenuItem("Informasi Usaha", R.drawable.usaha_icon),
            MenuItem("Pajak", R.drawable.pajak_icon)
        ))

        MenuSection(title = "Akun", items = listOf(
            MenuItem("Ubah Kata Sandi", R.drawable.password_icon),
            MenuItem("Notifikasi", R.drawable.notifikasi_icon)
        ))

        MenuSection(title = "Lainnya", items = listOf(
            MenuItem("Pusat Bantuan", R.drawable.help_icon),
            MenuItem("Log Out", R.drawable.logout_icon)
        ))
    }
}

@Composable
fun MenuSection(title: String, items: List<MenuItem>) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    items.forEach {
        ProfileMenuItem(title = it.title, icon = it.iconRes)
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: Int) {
    var isSelected by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        border = if (isSelected) BorderStroke(2.dp, Color.Green) else null
    ) {
        Row(
            modifier = Modifier
                .clickable { isSelected = !isSelected }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 14.sp)
            }

            if (title != "Log Out") {
                Image(
                    painter = painterResource(id = R.drawable.go_icon),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            isSelected = !isSelected
                        }
                )
            }
        }
    }
}

data class MenuItem(
    val title: String,
    val iconRes: Int
)
