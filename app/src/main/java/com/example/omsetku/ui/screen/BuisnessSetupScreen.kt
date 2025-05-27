package com.example.omsetku.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSetupScreen(navController: NavController = rememberNavController()) {
    var nama by remember { mutableStateOf(TextFieldValue("")) }
    var noTelepon by remember { mutableStateOf(TextFieldValue("")) }
    var namaUsaha by remember { mutableStateOf(TextFieldValue("")) }
    var alamatUsaha by remember { mutableStateOf(TextFieldValue("")) }
    var jenisUsaha by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val jenisUsahaOptions = listOf(
        "Makanan & Minuman",
        "Fashion",
        "Elektronik",
        "Kesehatan",
        "Kecantikan",
        "Jasa",
        "Lainnya"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Judul
        Text(
            text = "Siapkan Bisnis Anda",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Nama Anda
        Text(
            text = "Nama Anda",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF62DCC8)
            ),
            textStyle = TextStyle(fontFamily = Poppins),
            singleLine = true
        )

        // No Telepon
        Text(
            text = "No Telepon",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = noTelepon,
            onValueChange = { noTelepon = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF62DCC8)
            ),
            textStyle = TextStyle(fontFamily = Poppins),
            singleLine = true
        )

        // Nama Usaha
        Text(
            text = "Nama Usaha",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = namaUsaha,
            onValueChange = { namaUsaha = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF62DCC8)
            ),
            textStyle = TextStyle(fontFamily = Poppins),
            singleLine = true
        )

        // Jenis Usaha (Dropdown)
        Text(
            text = "Jenis Usaha",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = jenisUsaha,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color(0xFF62DCC8)
                ),
                textStyle = TextStyle(fontFamily = Poppins),
                trailingIcon = {
                    IconButton(onClick = { isDropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Pilih Jenis Usaha"
                        )
                    }
                }
            )

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White)
            ) {
                jenisUsahaOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontFamily = Poppins
                            )
                        },
                        onClick = {
                            jenisUsaha = option
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Alamat Usaha
        Text(
            text = "Alamat Usaha",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = alamatUsaha,
            onValueChange = { alamatUsaha = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF62DCC8)
            ),
            textStyle = TextStyle(fontFamily = Poppins),
            minLines = 3
        )

        Spacer(modifier = Modifier.weight(1f, fill = true))

        // Tombol Lanjutkan
        Button(
            onClick = { navController.navigate(Routes.HOME) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
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

@Preview(showBackground = true)
@Composable
private fun BusinessSetupScreenPreview() {
    BusinessSetupScreen()
}