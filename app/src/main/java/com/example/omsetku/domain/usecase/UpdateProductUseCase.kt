package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.Product
import com.example.omsetku.domain.repository.ProductRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        productRepository.updateProduct(product)
    }
} 