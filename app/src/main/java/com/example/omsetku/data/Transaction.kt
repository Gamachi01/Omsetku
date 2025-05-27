package com.example.omsetku.data

data class Transaction(
    val type: String,
    val description: String,
    val amount: Int,
    val date: String,
    val category: String = ""
)
