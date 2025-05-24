package com.example.omsetku.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.viewmodels.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessFormScreen(
    navController: NavController,
    businessViewModel: BusinessViewModel = viewModel()
) {
    var businessName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var businessEmail by remember { mutableStateOf("") }
    var businessPhone by remember { mutableStateOf("") }
    
    val isLoading by businessViewModel.isLoading.collectAsState()
    val error by businessViewModel.error.collectAsState()
    val isSuccess by businessViewModel.isSuccess.collectAsState()
    
    // Efek untuk navigasi jika data berhasil disimpan
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.BUSINESS_FORM) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Data Usaha",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF5ED0C5)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Lengkapi data usaha Anda untuk memulai",
            fontSize = 16.sp,
            fontFamily = Poppins,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Form Business
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nama Usaha
            Text(
                text = "Nama Usaha",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                placeholder = { Text("Masukkan nama usaha", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Poppins
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Jenis Usaha
            Text(
                text = "Jenis Usaha",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = businessType,
                onValueChange = { businessType = it },
                placeholder = { Text("Contoh: Toko Kelontong, Restoran, dll", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Poppins
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Alamat Usaha
            Text(
                text = "Alamat Usaha",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = businessAddress,
                onValueChange = { businessAddress = it },
                placeholder = { Text("Masukkan alamat lengkap", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email Usaha (Opsional)
            Text(
                text = "Email Usaha (Opsional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = businessEmail,
                onValueChange = { businessEmail = it },
                placeholder = { Text("Masukkan email usaha", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Poppins
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Nomor Telepon Usaha (Opsional)
            Text(
                text = "Nomor Telepon Usaha (Opsional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = businessPhone,
                onValueChange = { businessPhone = it },
                placeholder = { Text("Masukkan nomor telepon usaha", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF5ED0C5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Poppins
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tombol Simpan
            Button(
                onClick = { 
                    if (validateInputs(businessName, businessType, businessAddress)) {
                        businessViewModel.saveBusinessData(
                            name = businessName,
                            type = businessType,
                            address = businessAddress,
                            email = businessEmail.takeIf { it.isNotBlank() },
                            phone = businessPhone.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5ED0C5)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Simpan & Lanjutkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tombol Lewati (Opsional)
            TextButton(
                onClick = { 
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.BUSINESS_FORM) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Lewati untuk saat ini",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }
        }
    }
    
    // Error dialog jika ada error
    if (error != null) {
        AlertDialog(
            onDismissRequest = { businessViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error ?: "") },
            confirmButton = {
                TextButton(onClick = { businessViewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Validasi input form bisnis
 */
private fun validateInputs(
    businessName: String,
    businessType: String,
    businessAddress: String
): Boolean {
    // Check if required fields are filled
    if (businessName.isBlank() || businessType.isBlank() || businessAddress.isBlank()) {
        return false
    }
    
    return true
} 