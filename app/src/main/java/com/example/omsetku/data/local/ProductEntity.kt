package com.example.omsetku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String? = null,
    val hpp: Double = 0.0,
    val stock: Int = 0
) 