package com.example.omsetku.models

/**
 * Model untuk item dalam keranjang
 */
data class CartItem(
    val productId: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageRes: Int
) {
    val subtotal: Int
        get() = price * quantity
}