package com.example.omsetku.domain.model

data class Transaction(
    val id: String,
    val userId: String,
    val type: String,
    val date: Long,
    val subtotal: Long,
    val tax: Long,
    val total: Long,
    val profit: Long,
    val items: List<CartItem>
) 