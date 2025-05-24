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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    // Efek untuk navigasi jika user sudah login
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.SIGNUP) {
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
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Buat Akun Baru",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color(0xFF5ED0C5)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Silahkan isi data dengan benar",
            fontSize = 16.sp,
            fontFamily = Poppins,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Form Register
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nama
            Text(
                text = "Nama Lengkap",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
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
            
            // Email
            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Masukkan email", fontSize = 14.sp, fontFamily = Poppins) },
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
            
            // Nomor Telepon
            Text(
                text = "Nomor Telepon",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password
            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Masukkan password", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
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
            
            // Konfirmasi Password
            Text(
                text = "Konfirmasi Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Konfirmasi password", fontSize = 14.sp, fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
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
            
            // Tombol Register
            Button(
                onClick = { 
                    if (validateInputs(name, email, phone, password, confirmPassword)) {
                        authViewModel.register(name, email, password, phone)
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
                        text = "Daftar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Text untuk login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun?",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray
                )
                
                TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
                    Text(
                        text = "Masuk",
                        color = Color(0xFF5ED0C5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
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
 * Validasi input register
 */
private fun validateInputs(
    name: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String
): Boolean {
    // Check if all fields are filled
    if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
        return false
    }
    
    // Check email format
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    if (!email.matches(emailRegex)) {
        return false
    }
    
    // Check if passwords match
    if (password != confirmPassword) {
        return false
    }
    
    // Check minimum password length
    if (password.length < 6) {
        return false
    }
    
    return true
} 