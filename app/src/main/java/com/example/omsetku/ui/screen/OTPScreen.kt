package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreen(
    navController: NavController = rememberNavController(),
    email: String = "blabla@gmail.com" // Email bisa diteruskan dari SignUpScreen
) {
    // State untuk 6 digit OTP
    var otpDigits by remember { mutableStateOf(List(6) { "" }) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // OTP Verification Illustration
        Image(
            painter = painterResource(id = R.drawable.register_illustations), // Menggunakan gambar yang sudah ada di resource
            contentDescription = "OTP Verification Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(vertical = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = "Masukkan Kode OTP",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = "6 digit kode telah dikirim ke $email",
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = Poppins,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // 6-digit OTP Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            // Generate 6 OTP input fields
            repeat(6) { index ->
                OTPTextField(
                    value = otpDigits[index],
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            val newOtpDigits = otpDigits.toMutableList()
                            newOtpDigits[index] = newValue
                            otpDigits = newOtpDigits
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Resend OTP Option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tidak mendapat kode? ",
                fontSize = 14.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
            
            Text(
                text = "Kirim Ulang OTP",
                style = TextStyle(
                    color = Color(0xFF62DCC8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                ),
                modifier = Modifier.clickable { /* Implementasi pengiriman ulang OTP */ }
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Continue Button
        Button(
            onClick = { navController.navigate(Routes.PERSONAL_DATA) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62DCC8))
        ) {
            Text(
                text = "Lanjutkan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .width(52.dp)
            .height(52.dp),
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = Poppins
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF62DCC8),
            unfocusedBorderColor = Color.LightGray
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun OTPScreenPreview() {
    OTPScreen()
} 