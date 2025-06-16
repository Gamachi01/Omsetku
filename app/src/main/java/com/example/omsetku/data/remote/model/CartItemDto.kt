package com.example.omsetku.data.remote.model

data class CartItemDto(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val price: Long = 0L,
    val quantity: Int = 0,
    val imageUrl: String? = null,
    val hpp: Double = 0.0,
    val subtotal: Long = 0L
) 