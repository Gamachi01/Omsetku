package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.Product
import com.example.omsetku.domain.repository.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        productRepository.addProduct(product)
    }
} 