package com.example.omsetku.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omsetku.R

@Composable
fun CashierScreen() {
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showManageProductDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search Product") },
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { showAddProductDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Product")
            }
            Button(onClick = { showManageProductDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Manage Product")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Manage Product")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Oops! No Product Yet.", fontSize = 16.sp, color = Color.Gray)
        Text("Add product items first to start taking orders.", fontSize = 14.sp, color = Color.Gray)
    }

    if (showAddProductDialog) {
        AddProductDialog(onDismiss = { showAddProductDialog = false })
    }
    if (showManageProductDialog) {
        ManageProductDialog(onDismiss = { showManageProductDialog = false })
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable { }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload_icon),
                        contentDescription = "Upload Image",
                        tint = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                var productName by remember { mutableStateOf(TextFieldValue()) }
                BasicTextField(value = productName, onValueChange = { productName = it })
                Spacer(modifier = Modifier.height(8.dp))
                var productPrice by remember { mutableStateOf(TextFieldValue()) }
                BasicTextField(value = productPrice, onValueChange = { productPrice = it })
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Add Product")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ManageProductDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Product") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable { }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload_icon),
                        contentDescription = "Upload Image",
                        tint = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                var productName by remember { mutableStateOf(TextFieldValue()) }
                BasicTextField(value = productName, onValueChange = { productName = it })
                Spacer(modifier = Modifier.height(8.dp))
                var productPrice by remember { mutableStateOf(TextFieldValue()) }
                BasicTextField(value = productPrice, onValueChange = { productPrice = it })
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}