package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.repository.CartRepository
import javax.inject.Inject

class DeleteCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(id: String) {
        cartRepository.deleteCartItem(id)
    }
} 