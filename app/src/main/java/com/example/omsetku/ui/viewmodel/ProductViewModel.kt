package com.example.omsetku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.domain.model.Product
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
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        getProducts()
    }

    fun getProducts() {
        getProductsUseCase().onEach { _products.value = it }.launchIn(viewModelScope)
    }

    fun addProduct(product: Product) {
        viewModelScope.launch { addProductUseCase(product) }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { updateProductUseCase(product) }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch { deleteProductUseCase(id) }
    }

    suspend fun getProductById(id: String): Product? {
        return getProductByIdUseCase(id)
    }
} 