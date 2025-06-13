package com.example.omsetku.ui.data

import com.example.omsetku.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository untuk mengelola data produk di aplikasi
 */
class ProductRepository private constructor() {
    // Simulasi database dengan MutableStateFlow
    private val _products = MutableStateFlow<List<ProductItem>>(emptyList())
    val products: StateFlow<List<ProductItem>> = _products.asStateFlow()

    init {
        // Inisialisasi dengan data dummy
        _products.value = listOf(
            ProductItem(1, "dummy-id-1", "Cappucino", 25000, R.drawable.logo, "", 0, 0.0),
            ProductItem(2, "dummy-id-2", "Americano", 20000, R.drawable.logo, "", 0, 0.0),
            ProductItem(3, "dummy-id-3", "Espresso", 15000, R.drawable.logo, "", 0, 0.0),
            ProductItem(4, "dummy-id-4", "Brown Sugar Latte", 15000, R.drawable.logo, "", 0, 0.0)
        )
    }

    /**
     * Menambahkan produk baru
     */
    fun addProduct(name: String, price: Int): ProductItem {
        val newId = (_products.value.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = ProductItem(newId, "dummy-id-$newId", name, price, R.drawable.logo, "", 0, 0.0)
        _products.value = _products.value + newProduct
        return newProduct
    }

    /**
     * Memperbarui produk yang sudah ada
     */
    fun updateProduct(id: Int, name: String, price: Int): ProductItem? {
        val currentProducts = _products.value.toMutableList()
        val index = currentProducts.indexOfFirst { it.id == id }
        if (index == -1) return null

        val updatedProduct = currentProducts[index].copy(name = name, price = price)
        currentProducts[index] = updatedProduct
        _products.value = currentProducts
        return updatedProduct
    }

    /**
     * Menghapus produk
     */
    fun deleteProduct(id: Int): Boolean {
        val currentProducts = _products.value
        val newProducts = currentProducts.filter { it.id != id }
        if (newProducts.size == currentProducts.size) return false

        _products.value = newProducts
        return true
    }

    /**
     * Mendapatkan daftar produk saat ini
     */
    fun getProducts(): List<ProductItem> {
        return _products.value
    }

    /**
     * Mendapatkan produk berdasarkan ID
     */
    fun getProductById(id: Int): ProductItem? {
        return _products.value.find { it.id == id }
    }

    companion object {
        // Singleton
        @Volatile
        private var INSTANCE: ProductRepository? = null

        fun getInstance(): ProductRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ProductRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}