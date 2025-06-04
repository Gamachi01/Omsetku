package com.example.omsetku.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Komponen TextField yang konsisten untuk seluruh aplikasi
 */
fun formatRupiahInput(input: String): String {
    val clean = input.replace(".", "").replace(",", "")
    if (clean.isBlank()) return ""
    return clean.toLongOrNull()?.let {
        "%,d".format(it).replace(',', '.')
    } ?: input
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isRupiah: Boolean = false
) {
    val displayValue = if (isRupiah) formatRupiahInput(value) else value

    OutlinedTextField(
        value = displayValue,
        onValueChange = {
            if (isRupiah) onValueChange(it.replace("[^0-9]".toRegex(), ""))
            else onValueChange(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        label = label?.let { { Text(it, fontFamily = Poppins) } },
        placeholder = placeholder?.let { { Text(it, fontFamily = Poppins, color = Color.Gray) } },
        readOnly = readOnly,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let { { Text(it, fontFamily = Poppins) } },
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF5ED0C5),
            disabledBorderColor = Color.LightGray,
            disabledTextColor = Color.Black,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            errorSupportingTextColor = Color.Red
        ),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = Poppins
        )
    )
}

/**
 * Komponen TextField untuk input multiline (seperti deskripsi)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        label = label?.let { { Text(it, fontFamily = Poppins) } },
        placeholder = placeholder?.let { { Text(it, fontFamily = Poppins, color = Color.Gray) } },
        readOnly = readOnly,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let { { Text(it, fontFamily = Poppins) } },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF5ED0C5),
            disabledBorderColor = Color.LightGray,
            disabledTextColor = Color.Black,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            errorSupportingTextColor = Color.Red
        ),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = Poppins
        )
    )
}

/**
 * Komponen Label untuk form field
 */
@Composable
fun FormLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Poppins,
        modifier = modifier
    )
}

/**
 * Komponen wrapper untuk form field dengan label
 */
@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        FormLabel(text = label)
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
} 