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

enum class TransactionType {
    INCOME, EXPENSE
}

@Composable
fun TransactionScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf(Routes.TRANSACTION) }
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    navController.navigate(item) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Text(
                text = "Catat Transaksi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Poppins
            )

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TransactionButton(
                    text = "Pemasukan",
                    isSelected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                )
                TransactionButton(
                    text = "Pengeluaran",
                    isSelected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabeledFieldBox(label = "Tanggal Transaksi") {
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.transactioncalender),
                            contentDescription = "Kalender",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }

            LabeledFieldBox(label = "Nominal") {
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rp", fontSize = 14.sp) }
                )
            }

            LabeledFieldBox(label = "Kategori") {
                OutlinedTextField(
                    value = if (selectedType == TransactionType.INCOME) "Pemasukan" else "Pengeluaran",
                    onValueChange = {},
                    enabled = false,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LabeledFieldBox(label = "Deskripsi") {
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Simpan data */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ED0C5))
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold, fontFamily = Poppins)
            }
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
            .height(32.dp)
            .width(176.dp),
        contentPadding = PaddingValues(vertical = 0.dp),
        shape = shape,
        border = BorderStroke(1.dp, selectedColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = Color.Black
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
            .padding(vertical = 8.dp)
            .width(353.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = Poppins
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(353.dp)
                .heightIn(min = 56.dp),
        ) {
            content()
        }
    }
}

