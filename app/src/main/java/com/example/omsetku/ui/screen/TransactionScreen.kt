package com.example.omsetku.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.ui.components.Poppins
import com.example.omsetku.R
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import com.example.omsetku.Navigation.Routes
import com.example.omsetku.ui.components.BottomNavBar
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.ui.components.DatePickerField

enum class TransactionType {
    INCOME, EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Transaction") }
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    when (item) {
                        "Home" -> navController.navigate(Routes.HOME)
                        "Cashier" -> navController.navigate(Routes.CASHIER)
                        "Transaction" -> { /* Sudah di layar Transaction */ }
                        "HPP" -> navController.navigate(Routes.HPP)
                        "Report" -> navController.navigate(Routes.REPORT)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 80.dp)
                .padding(paddingValues)
        ) {
            Text(
                text = "Catat Transaksi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransactionButton(
                    text = "Pemasukan",
                    isSelected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
                TransactionButton(
                    text = "Pengeluaran",
                    isSelected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LabeledFieldBox(label = "Tanggal Transaksi") {
                DatePickerField(
                    value = tanggal,
                    onDateSelected = { tanggal = it }
                )
            }

            LabeledFieldBox(label = "Nominal") {
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    placeholder = { 
                        Text(
                            "Rp", 
                            fontSize = 14.sp, 
                            fontFamily = Poppins,
                            color = Color.Gray
                        ) 
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    )
                )
            }

            LabeledFieldBox(label = "Kategori") {
                OutlinedTextField(
                    value = if (selectedType == TransactionType.INCOME) "Pemasukan" else "Pengeluaran",
                    onValueChange = {},
                    enabled = false,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5),
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.Black
                    )
                )
            }

            LabeledFieldBox(label = "Deskripsi") {
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, fontFamily = Poppins),
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF5ED0C5)
                    ),
                    placeholder = { 
                        Text(
                            "Masukkan deskripsi transaksi...", 
                            fontSize = 14.sp, 
                            fontFamily = Poppins,
                            color = Color.Gray
                        ) 
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Simpan data */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                Text(
                    "Simpan", 
                    fontWeight = FontWeight.Bold, 
                    fontFamily = Poppins,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun TransactionButton(
    text: String,
    isSelected: Boolean,
    shape: Shape,
    onClick: () -> Unit
) {
    val selectedColor = Color(0xFF5ED0C5)
    val unselectedColor = Color.White

    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .width(176.dp),
        contentPadding = PaddingValues(vertical = 0.dp),
        shape = shape,
        border = BorderStroke(1.dp, selectedColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = if (isSelected) Color.White else Color.Black
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins
        )
    }
}

@Composable
fun LabeledFieldBox(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

