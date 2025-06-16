package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.CartItem
import com.example.omsetku.domain.repository.CartRepository
import javax.inject.Inject

class AddCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(item: CartItem) {
        cartRepository.addCartItem(item)
    }
} 