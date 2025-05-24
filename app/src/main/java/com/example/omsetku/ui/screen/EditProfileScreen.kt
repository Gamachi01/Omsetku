package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var nama by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("johndoe@example.com") }
    var noTelepon by remember { mutableStateOf("081234567890") }
    var jenisKelamin by remember { mutableStateOf("Laki-laki") }
    var jabatan by remember { mutableStateOf("Pemilik Usaha") }
    
    val scrollState = rememberScrollState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    
    // Efek untuk memuat data profil saat screen dibuka
    LaunchedEffect(Unit) {
        // Di implementasi nyata, kita akan mengambil data dari repository
        // Untuk contoh ini, kita gunakan data statis
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header dengan tombol kembali dan judul
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigate(Routes.PROFILE) },
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
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Edit Profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Foto profil
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
        
        // Form fields
        FormField(label = "Nama", value = nama, onValueChange = { nama = it })
        
        Spacer(modifier = Modifier.height(16.dp))
        
        FormField(label = "Email", value = email, onValueChange = { email = it })
        
        Spacer(modifier = Modifier.height(16.dp))
        
        FormField(label = "No Telepon", value = noTelepon, onValueChange = { noTelepon = it })
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dropdown untuk jenis kelamin
        var expandedGender by remember { mutableStateOf(false) }
        val genderOptions = listOf("Laki-laki", "Perempuan")
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Jenis Kelamin",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = it }
            ) {
                OutlinedTextField(
                    value = jenisKelamin,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender)
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = Poppins) },
                            onClick = {
                                jenisKelamin = option
                                expandedGender = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dropdown untuk jabatan
        var expandedPosition by remember { mutableStateOf(false) }
        val positionOptions = listOf("Pemilik Usaha", "Manager", "Karyawan", "Lainnya")
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Jabatan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = expandedPosition,
                onExpandedChange = { expandedPosition = it }
            ) {
                OutlinedTextField(
                    value = jabatan,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPosition)
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = expandedPosition,
                    onDismissRequest = { expandedPosition = false }
                ) {
                    positionOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = Poppins) },
                            onClick = {
                                jabatan = option
                                expandedPosition = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f, fill = true))
        
        // Tampilkan error jika ada
        if (error != null) {
            Text(
                text = error ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = Poppins,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Tombol simpan
        Button(
            onClick = { 
                // Di implementasi nyata, kita akan menyimpan data ke repository
                navController.navigate(Routes.PROFILE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Simpan",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF5ED0C5)
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = Poppins
            )
        )
    }
} 