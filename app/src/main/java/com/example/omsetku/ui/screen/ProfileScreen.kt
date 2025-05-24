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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.R
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.viewmodels.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // State untuk mengontrol dialog konfirmasi logout
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isLoading by authViewModel.isLoading.collectAsState()
    val user by authViewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            IconButton(
                onClick = { navController.navigate(Routes.HOME) },
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = (-12).dp)
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
                        text = user?.name ?: "Nama Pengguna",
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
            MenuItem("Log Out", R.drawable.logout_icon, onClick = { showLogoutDialog = true })
        ))
    }
    
    // Dialog konfirmasi logout
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            isLoading = isLoading,
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                authViewModel.logout()
                showLogoutDialog = false
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun LogoutConfirmationDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log Out Akun",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Apakah anda yakin log out?",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Tombol Ya - untuk konfirmasi logout
                Button(
                    onClick = onConfirm,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62DCC8)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Ya",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Tombol Batal - untuk membatalkan logout
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF62DCC8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Batal",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFF62DCC8)
                    )
                }
            }
        }
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
