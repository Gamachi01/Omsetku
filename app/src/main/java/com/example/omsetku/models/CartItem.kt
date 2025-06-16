package com.example.omsetku.models

/**
 * Model untuk item dalam keranjang
 */
data class CartItem(
    val productId: String,
    val name: String,
    val price: Long,
    val quantity: Int,
    val imageRes: Int,
    val hpp: Double = 0.0
) {
    val subtotal: Long
        get() = price * quantity
}