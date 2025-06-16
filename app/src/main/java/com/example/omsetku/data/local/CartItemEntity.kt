package com.example.omsetku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val name: String,
    val price: Long,
    val quantity: Int,
    val imageUrl: String? = null,
    val hpp: Double = 0.0,
    val subtotal: Long = 0L
) 