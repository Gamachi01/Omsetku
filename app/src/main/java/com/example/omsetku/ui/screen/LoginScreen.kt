package com.example.omsetku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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

@Composable
fun LoginScreen() {
    var isLogin by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Image(
            painter = painterResource(id = if (isLogin) R.drawable.loginillustrations else R.drawable.register_illustations),
            contentDescription = "Auth Illustration",
            modifier = Modifier
                .height(if (isLogin) 300.dp else 220.dp)
                .fillMaxWidth()
        )

        Text(
            text = if (isLogin) "Welcome Back!" else "Let’s Get Started!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold

        )
        Text(
            text = if (isLogin) "Please enter your Login details below" else "Please enter your Sign Up details below",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthForm(isLogin)

        Spacer(modifier = Modifier.height(20.dp))

        // Divider dengan teks di tengah
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Divider(
                color = Color(0xFF999999),
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = " Atau login dengan ",
                style = TextStyle(color = Color(0xFF999999)),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Divider(
                color = Color(0xFF999999),
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }

        // Tombol login Facebook dan Google
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
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

        Spacer(modifier = Modifier.height(24.dp))

        Column (modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            Row {
                Text(text = if (isLogin) "Belum memiliki akun? " else "Sudah punya akun? ")
                Text(
                    text = if (isLogin) "Sign up" else "Login",
                    style = TextStyle(
                        color = Color(0xFF08C39F),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { isLogin = !isLogin }
                )
            }
        }

    }
}

@Composable
fun AuthForm(isLogin: Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        placeholder = { Text("example@gmail.com") },
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        placeholder = { Text("Enter your password") },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Text(if (passwordVisible) "Hide" else "Show")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (!isLogin) {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm your password") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
        onClick = { /* Handle login/signup logic */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF08C39F))
    ) {
        Text(text = if (isLogin) "Login" else "Sign Up", color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun SocialLoginButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
        color = Color(0xFFF0F0F0),
        modifier = Modifier
            .width(150.dp)
            .height(50.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = icon,
                contentDescription = "$text Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
