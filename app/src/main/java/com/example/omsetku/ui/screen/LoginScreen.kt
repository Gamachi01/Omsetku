package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.R
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.ui.theme.PrimaryVariant

@Composable
fun LoginScreen() {
    var isLogin by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = if (isLogin) R.drawable.loginillustrations else R.drawable.register_illustations),
            contentDescription = "Auth Illustration",
            modifier = Modifier
                .height(if (isLogin) 280.dp else 220.dp)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isLogin) "Welcome Back!" else "Let's Get Started!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isLogin) "Please enter your Login details below" else "Please enter your Sign Up details below",
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthForm(isLogin)

        Spacer(modifier = Modifier.height(24.dp))

        // Divider dengan teks di tengah
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
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

        // Tombol login Facebook dan Google
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            SocialLoginButton(
                icon = painterResource(id = R.drawable.facebook_icon),
                text = "Facebook"
            ) {
                // TODO: Handle Facebook login
            }

            SocialLoginButton(
                icon = painterResource(id = R.drawable.google_icon),
                text = "Google"
            ) {
                // TODO: Handle Google login
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLogin) "Belum memiliki akun? " else "Sudah punya akun? ",
                fontSize = 15.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
            Text(
                text = if (isLogin) "Sign up" else "Login",
                style = TextStyle(
                    color = PrimaryVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    fontFamily = Poppins
                ),
                modifier = Modifier.clickable { isLogin = !isLogin }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthForm(isLogin: Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email", fontFamily = Poppins) },
        placeholder = { Text("example@gmail.com", fontFamily = Poppins) },
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryVariant,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = PrimaryVariant
        ),
        textStyle = TextStyle(fontFamily = Poppins)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password", fontFamily = Poppins) },
        placeholder = { Text("Enter your password", fontFamily = Poppins) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                    tint = PrimaryVariant
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryVariant,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = PrimaryVariant
        ),
        textStyle = TextStyle(fontFamily = Poppins)
    )

    if (!isLogin) {
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password", fontFamily = Poppins) },
            placeholder = { Text("Confirm your password", fontFamily = Poppins) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password",
                        tint = PrimaryVariant
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryVariant,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PrimaryVariant
            ),
            textStyle = TextStyle(fontFamily = Poppins)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { /* Handle login/signup logic */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryVariant),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = if (isLogin) "Login" else "Sign Up", 
            color = Color.White, 
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins
        )
    }
}

@Composable
fun SocialLoginButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = Color(0xFFF8F8F8),
        modifier = Modifier
            .width(160.dp)
            .height(56.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = "$text Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text, 
                color = Color.Black,
                fontSize = 15.sp,
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
