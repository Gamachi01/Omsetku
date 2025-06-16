package com.example.omsetku.domain.repository

import com.example.omsetku.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addCartItem(item: CartItem)
    suspend fun updateCartItem(item: CartItem)
    suspend fun deleteCartItem(id: String)
    suspend fun clearCart()
} 