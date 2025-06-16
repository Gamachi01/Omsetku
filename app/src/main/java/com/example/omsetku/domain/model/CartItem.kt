package com.example.omsetku.domain.model

data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val price: Long,
    val quantity: Int,
    val imageUrl: String? = null,
    val hpp: Double = 0.0,
    val subtotal: Long = 0L
) 