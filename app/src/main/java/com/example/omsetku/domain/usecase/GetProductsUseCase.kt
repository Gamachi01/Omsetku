package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.Product
import com.example.omsetku.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = productRepository.getProducts()
} 