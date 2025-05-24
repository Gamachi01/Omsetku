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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessInfoScreen(navController: NavController) {
    var namaUsaha by remember { mutableStateOf("") }
    var jenisUsaha by remember { mutableStateOf("") }
    var alamatUsaha by remember { mutableStateOf("") }
    var emailUsaha by remember { mutableStateOf("") }
    var teleponUsaha by remember { mutableStateOf("") }
    
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
        
        // Logo usaha
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFF5ED0C5), CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.usaha_icon),
                    contentDescription = "Logo Usaha",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF5ED0C5)
                )
            }
        }
        
        Text(
            text = "Logo",
            fontSize = 14.sp,
            fontFamily = Poppins,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
                readOnly = true,
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
            onClick = { navController.navigate(Routes.PROFILE) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
        ) {
            Text(
                text = "Simpan",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
} 