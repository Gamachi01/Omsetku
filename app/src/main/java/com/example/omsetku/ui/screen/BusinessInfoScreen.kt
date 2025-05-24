package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.components.FormField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.omsetku.viewmodels.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessInfoScreen(
    navController: NavController,
    businessViewModel: BusinessViewModel = viewModel()
) {
    // State untuk data bisnis
    var namaUsaha by remember { mutableStateOf("") }
    var jenisUsaha by remember { mutableStateOf("") }
    var alamatUsaha by remember { mutableStateOf("") }
    var emailUsaha by remember { mutableStateOf("") }
    var teleponUsaha by remember { mutableStateOf("") }
    
    // Ambil data bisnis dari ViewModel
    val currentBusiness by businessViewModel.currentBusiness.collectAsState()
    val isLoading by businessViewModel.isLoading.collectAsState()
    val error by businessViewModel.error.collectAsState()
    
    // Update form state dari data bisnis saat ini
    LaunchedEffect(currentBusiness) {
        currentBusiness?.let { business ->
            namaUsaha = business.name
            jenisUsaha = business.type
            alamatUsaha = business.address
            emailUsaha = business.email ?: ""
            teleponUsaha = business.phone ?: ""
        }
    }
    
    // Memuat data bisnis saat screen dibuka
    LaunchedEffect(Unit) {
        businessViewModel.loadBusinessData()
    }
    
    val scrollState = rememberScrollState()
    
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
                text = "Informasi Usaha",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF5ED0C5),
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            // Logo usaha
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF5ED0C5), CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.usaha_icon),
                        contentDescription = "Logo Usaha",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF5ED0C5)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Logo",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Form fields
            FormField(label = "Nama Usaha", value = namaUsaha, onValueChange = { namaUsaha = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dropdown untuk jenis usaha
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Jenis Usaha",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = jenisUsaha,
                    onValueChange = { jenisUsaha = it },
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
                    ),
                    readOnly = false,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.go_icon),
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(label = "Alamat Usaha", value = alamatUsaha, onValueChange = { alamatUsaha = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(label = "Email Usaha", value = emailUsaha, onValueChange = { emailUsaha = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField(label = "Telepon Usaha", value = teleponUsaha, onValueChange = { teleponUsaha = it })
            
            Spacer(modifier = Modifier.weight(1f, fill = true))
            
            // Tombol simpan
            Button(
                onClick = { 
                    currentBusiness?.let { business ->
                        businessViewModel.updateBusinessData(
                            businessId = business.id,
                            name = namaUsaha,
                            type = jenisUsaha,
                            address = alamatUsaha,
                            email = emailUsaha,
                            phone = teleponUsaha
                        )
                    }
                    navController.navigate(Routes.PROFILE)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5)),
                enabled = !isLoading && currentBusiness != null
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
        
        // Error message
        if (error != null) {
            Text(
                text = error ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = Poppins,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
} 