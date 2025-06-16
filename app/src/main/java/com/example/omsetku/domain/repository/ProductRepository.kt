package com.example.omsetku.domain.repository

import com.example.omsetku.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun addProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: String)
} 