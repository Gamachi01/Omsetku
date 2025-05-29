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
import androidx.compose.ui.text.input.VisualTransformation
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
fun ChangePasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }
    
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
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
                text = "Ubah Kata Sandi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Current Password Field
        Text(
            text = "Kata Sandi Saat Ini",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
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
            visualTransformation = if (currentPasswordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (currentPasswordVisible) 
                                R.drawable.visibilityoff
                            else 
                                R.drawable.visibilityon
                        ),
                        contentDescription = if (currentPasswordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // New Password Field
        Text(
            text = "Kata Sandi Baru",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
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
            visualTransformation = if (newPasswordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (newPasswordVisible) 
                                R.drawable.visibilityoff
                            else 
                                R.drawable.visibilityon
                        ),
                        contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm New Password Field
        Text(
            text = "Konfirmasi Kata Sandi Baru",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
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
            visualTransformation = if (confirmNewPasswordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (confirmNewPasswordVisible) 
                                R.drawable.visibilityoff
                            else 
                                R.drawable.visibilityon
                        ),
                        contentDescription = if (confirmNewPasswordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            }
        )

        // Error message if passwords don't match
        if (newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty() && newPassword != confirmNewPassword) {
            Text(
                text = "Kata sandi baru tidak cocok",
                color = Color.Red,
                fontSize = 12.sp,
                fontFamily = Poppins,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Error message from ViewModel if any
        if (error != null) {
            Text(
                text = error ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = Poppins,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Simpan Button
        Button(
            onClick = {
                if (validateInputs(currentPassword, newPassword, confirmNewPassword)) {
                    authViewModel.changePassword(currentPassword, newPassword)
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
                    text = "Simpan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }
        }
    }
}

/**
 * Validasi input untuk ubah password
 */
private fun validateInputs(
    currentPassword: String,
    newPassword: String,
    confirmNewPassword: String
): Boolean {
    // Check if all fields are filled
    if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
        return false
    }

    // Check if new password matches confirmation
    if (newPassword != confirmNewPassword) {
        return false
    }

    // Check minimum password length
    if (newPassword.length < 6) {
        return false
    }

    return true
} 