package com.example.omsetku.data.remote.model

data class ProductDto(
    val id: String = "",
    val name: String = "",
    val price: Long = 0L,
    val imageUrl: String? = null,
    val hpp: Double = 0.0,
    val stock: Int = 0
) 