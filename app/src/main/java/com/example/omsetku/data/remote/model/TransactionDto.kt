package com.example.omsetku.data.remote.model

data class TransactionDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val date: Long = 0L,
    val subtotal: Long = 0L,
    val tax: Long = 0L,
    val total: Long = 0L,
    val profit: Long = 0L,
    val items: List<CartItemDto> = emptyList()
) 