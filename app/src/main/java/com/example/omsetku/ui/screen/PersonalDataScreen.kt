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
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val personalDataSaved by authViewModel.personalDataSaved.collectAsState()

    // Efek untuk navigasi jika data diri sudah disimpan
    LaunchedEffect(personalDataSaved) {
        if (personalDataSaved) {
            // Reset status dan arahkan ke form bisnis
            authViewModel.resetPersonalDataStatus()
            navController.navigate(Routes.BUSINESS_FORM)
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
            text = "Data Diri",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF5ED0C5)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Lengkapi data diri Anda",
            fontSize = 16.sp,
            fontFamily = Poppins,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Form Data Diri
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nama Lengkap
            Text(
                text = "Nama Lengkap",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Masukkan nama lengkap", fontSize = 14.sp, fontFamily = Poppins) },
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

            // Jenis Kelamin
            Text(
                text = "Jenis Kelamin",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dropdown untuk jenis kelamin
            var expanded by remember { mutableStateOf(false) }
            val genderOptions = listOf("Laki-laki", "Perempuan")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Pilih jenis kelamin", fontSize = 14.sp, fontFamily = Poppins) },
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
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = Poppins) },
                            onClick = {
                                gender = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Jabatan
            Text(
                text = "Jabatan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                placeholder = { Text("Masukkan jabatan Anda", fontSize = 14.sp, fontFamily = Poppins) },
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

            // Nomor Telepon
            Text(
                text = "Nomor Telepon",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Masukkan nomor telepon", fontSize = 14.sp, fontFamily = Poppins) },
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
                    if (validateInputs(fullName, gender, position, phoneNumber)) {
                        authViewModel.savePersonalData(fullName, gender, position, phoneNumber)
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
                        text = "Selanjutnya",
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
 * Validasi input data diri
 */
private fun validateInputs(
    fullName: String,
    gender: String,
    position: String,
    phoneNumber: String
): Boolean {
    // Check if all required fields are filled
    if (fullName.isBlank() || gender.isBlank() || phoneNumber.isBlank()) {
        return false
    }

    return true
}