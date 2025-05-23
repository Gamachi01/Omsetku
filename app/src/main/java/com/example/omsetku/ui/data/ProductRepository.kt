package com.example.omsetku.ui.data

import com.example.omsetku.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Model data produk yang digunakan di seluruh aplikasi
 */
data class ProductItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int,
    var quantity: Int = 0
)

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
            ProductItem(1, "Cappucino", 25000, R.drawable.logo),
            ProductItem(2, "Americano", 20000, R.drawable.logo),
            ProductItem(3, "Espresso", 15000, R.drawable.logo),
            ProductItem(4, "Brown Sugar Latte", 15000, R.drawable.logo)
        )
    }
    
    /**
     * Menambahkan produk baru
     */
    fun addProduct(name: String, price: Int): ProductItem {
        val newId = (_products.value.maxOfOrNull { it.id } ?: 0) + 1
        val newProduct = ProductItem(newId, name, price, R.drawable.logo)
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