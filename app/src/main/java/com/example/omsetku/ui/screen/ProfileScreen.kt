package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.omsetku.Navigation.Routes

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = { navController.navigate(Routes.HOME) },
                modifier = Modifier.size(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBFBF8)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate(Routes.EDIT_PROFILE) }
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
                Icon(
                    painter = painterResource(id = R.drawable.go_icon),
                    contentDescription = "Arrow",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MenuSection(title = "Operasional", navController = navController, items = listOf(
            MenuItem("Informasi Usaha", R.drawable.usaha_icon, onClick = { navController.navigate(Routes.BUSINESS_INFO) }),
            MenuItem("Pajak", R.drawable.pajak_icon, onClick = { navController.navigate(Routes.TAX_SETTINGS) })
        ))

        MenuSection(title = "Akun", navController = navController, items = listOf(
            MenuItem("Ubah Kata Sandi", R.drawable.password_icon),
            MenuItem("Notifikasi", R.drawable.notifikasi_icon)
        ))

        MenuSection(title = "Lainnya", navController = navController, items = listOf(
            MenuItem("Pusat Bantuan", R.drawable.help_icon),
            MenuItem("Log Out", R.drawable.logout_icon, onClick = { navController.navigate(Routes.HOME) })
        ))
    }
}

@Composable
fun MenuSection(title: String, navController: NavController, items: List<MenuItem>) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    items.forEach {
        ProfileMenuItem(title = it.title, icon = it.iconRes, onClick = it.onClick)
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: Int, onClick: () -> Unit = {}) {
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
                .clickable { 
                    onClick()
                }
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
                Icon(
                    painter = painterResource(id = R.drawable.go_icon),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onClick()
                        },
                    tint = Color.Gray
                )
            }
        }
    }
}

data class MenuItem(
    val title: String,
    val iconRes: Int,
    val onClick: () -> Unit = {}
)
