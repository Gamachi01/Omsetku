package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun LoginScreen(navController: NavController = rememberNavController()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Login Illustration
        Image(
            painter = painterResource(id = R.drawable.loginillustrations),
            contentDescription = "Login Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(vertical = 16.dp)
        )

        // Welcome Text
        Text(
            text = "Selamat Datang!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Silakan masukkan detail Login Anda di bawah ini",
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        Text(
            text = "Email",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("contoh@gmail.com", fontFamily = Poppins, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PrimaryVariant
            ),
            textStyle = TextStyle(fontFamily = Poppins),
            singleLine = true
        )

        // Password Field
        Text(
            text = "Password",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("masukkan password", fontFamily = Poppins, color = Color.LightGray) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PrimaryVariant
            ),
            textStyle = TextStyle(fontFamily = Poppins),
            singleLine = true
        )

        // Login Button
        Button(
            onClick = { navController.navigate(Routes.HOME) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62DCC8))
        ) {
            Text(
                text = "Login",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )
        }

        // Divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Divider(
                color = Color(0xFFDDDDDD),
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = " Atau login dengan ",
                style = TextStyle(
                    color = Color(0xFF999999),
                    fontFamily = Poppins,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Divider(
                color = Color(0xFFDDDDDD),
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }

        // Social Login Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            SocialLoginButton(
                icon = painterResource(id = R.drawable.facebook_icon),
                text = "Facebook"
            ) {
                navController.navigate(Routes.HOME)
            }

            SocialLoginButton(
                icon = painterResource(id = R.drawable.google_icon),
                text = "Google"
            ) {
                navController.navigate(Routes.HOME)
            }
        }

        // Sign Up Text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Belum memiliki akun? ",
                fontSize = 14.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
            Text(
                text = "Sign up",
                style = TextStyle(
                    color = Color(0xFF62DCC8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                ),
                modifier = Modifier.clickable { /* TODO: Navigate to Sign Up */ }
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    icon: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .width(160.dp)
            .height(48.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = "$text Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.Black,
                fontSize = 14.sp,
                fontFamily = Poppins
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
