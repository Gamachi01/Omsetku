package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.omsetku.navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.viewmodels.AuthViewModel
import com.example.omsetku.models.Business

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessFormScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var businessName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var businessEmail by remember { mutableStateOf("") }
    var businessPhone by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val businessDataSaved by authViewModel.businessDataSaved.collectAsState()

    // Efek untuk navigasi jika data usaha sudah disimpan
    LaunchedEffect(businessDataSaved) {
        if (businessDataSaved) {
            // Reset status dan arahkan ke home
            authViewModel.resetBusinessDataStatus()
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) {
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Data Usaha",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF5ED0C5)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Lengkapi data usaha Anda",
            fontSize = 16.sp,
            fontFamily = Poppins,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Form Data Usaha
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

            // Dropdown untuk jenis usaha
            var expanded by remember { mutableStateOf(false) }
            val businessTypes = listOf("Makanan & Minuman", "Retail", "Jasa", "Manufaktur", "Lainnya")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = businessType,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Pilih jenis usaha", fontSize = 14.sp, fontFamily = Poppins) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .menuAnchor(),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    businessTypes.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = Poppins) },
                            onClick = {
                                businessType = option
                                expanded = false
                            }
                        )
                    }
                }
            }

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
                placeholder = { Text("Masukkan alamat usaha", fontSize = 14.sp, fontFamily = Poppins) },
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

            // Email Usaha
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            // Nomor Telepon Usaha
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        val business = com.example.omsetku.models.Business(
                            id = java.util.UUID.randomUUID().toString(),
                            name = businessName,
                            type = businessType,
                            address = businessAddress,
                            email = businessEmail.takeIf { it.isNotBlank() },
                            phone = businessPhone.takeIf { it.isNotBlank() },
                            logo = null,
                            createdAt = System.currentTimeMillis()
                        )
                        authViewModel.saveBusiness(business)
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
                        text = "Simpan dan Lanjutkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }
        }
    }

    // Error dialog jika ada error
    if (error != null) {
        AlertDialog(
            onDismissRequest = { authViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error ?: "") },
            confirmButton = {
                TextButton(onClick = { authViewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Validasi input data usaha
 */
private fun validateInputs(
    businessName: String,
    businessType: String,
    businessAddress: String
): Boolean {
    // Check if all required fields are filled
    if (businessName.isBlank() || businessType.isBlank() || businessAddress.isBlank()) {
        return false
    }

    return true
} 