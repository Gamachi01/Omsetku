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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var noTelepon by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("") }
    var jabatan by remember { mutableStateOf("") }
    
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
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigate(Routes.PROFILE) },
                modifier = Modifier.size(40.dp)
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
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Jenis Kelamin",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = jenisKelamin,
                onValueChange = { jenisKelamin = it },
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
        
        // Dropdown untuk jabatan
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Jabatan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = jabatan,
                onValueChange = { jabatan = it },
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
        
        Spacer(modifier = Modifier.weight(1f, fill = true))
        
        // Tombol simpan
        Button(
            onClick = { navController.navigate(Routes.PROFILE) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
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