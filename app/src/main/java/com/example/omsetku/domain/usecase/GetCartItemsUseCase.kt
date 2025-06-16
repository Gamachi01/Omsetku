package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.CartItem
import com.example.omsetku.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> = cartRepository.getCartItems()
} 