package com.example.omsetku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.domain.model.CartItem
import com.example.omsetku.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val deleteCartItemUseCase: DeleteCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    init {
        getCartItems()
    }

    fun getCartItems() {
        getCartItemsUseCase().onEach { _cartItems.value = it }.launchIn(viewModelScope)
    }

    fun addCartItem(item: CartItem) {
        viewModelScope.launch { addCartItemUseCase(item) }
    }

    fun updateCartItem(item: CartItem) {
        viewModelScope.launch { updateCartItemUseCase(item) }
    }

    fun deleteCartItem(id: String) {
        viewModelScope.launch { deleteCartItemUseCase(id) }
    }

    fun clearCart() {
        viewModelScope.launch { clearCartUseCase() }
    }
} 