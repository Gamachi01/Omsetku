package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(id: String) {
        productRepository.deleteProduct(id)
    }
} 