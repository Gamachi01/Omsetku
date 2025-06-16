package com.example.omsetku.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String? = null,
    val hpp: Double = 0.0,
    val stock: Int = 0
) 