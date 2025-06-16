package com.example.omsetku.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.R
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.firebase.FirestoreRepository
import com.example.omsetku.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val storageRepository = FirebaseModule.storageRepository
    private val repository = FirestoreRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Menyimpan mapping ID produk dari id ke Firestore ID
    private val productIdMap = mutableMapOf<String, String>()

    init {
        loadProducts()
    }

    /**
     * Memuat daftar produk dari Firestore
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val productsList = repository.getUserProducts()
                val productItems = productsList.map { productMap ->
                    Product(
                        id = productMap["id"] as? String ?: "",
                        firestoreId = productMap["id"] as? String ?: "",
                        name = productMap["name"] as? String ?: "",
                        price = (productMap["price"] as? Number)?.toLong() ?: 0,
                        imageRes = R.drawable.logo,
                        imageUrl = productMap["imageUrl"] as? String ?: "",
                        quantity = (productMap["quantity"] as? Number)?.toInt() ?: 0,
                        hpp = (productMap["hpp"] as? Number)?.toDouble() ?: 0.0
                    )
                }
                _products.value = productItems
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Menambahkan produk baru
     */
    fun addProduct(name: String, price: Int, imageUri: Uri? = null) {
        if (name.isBlank() || price <= 0) {
            _error.value = "Nama produk dan harga harus valid"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                var imageUrl = ""
                if (imageUri != null) {
                    try {
                        imageUrl = storageRepository.uploadProductImage(imageUri)
                    } catch (e: Exception) {
                        _error.value = "Gagal mengupload gambar: ${e.message}"
                        _isLoading.value = false
                        return@launch
                    }
                }
                val firestoreId = repository.saveProduct(name, price.toLong(), imageUrl)
                val newProduct = Product(
                    id = firestoreId,
                    firestoreId = firestoreId,
                    name = name,
                    price = price.toLong(),
                    imageRes = R.drawable.logo,
                    imageUrl = imageUrl,
                    quantity = 0,
                    hpp = 0.0
                )
                _products.value = _products.value + newProduct
            } catch (e: Exception) {
                _error.value = "Gagal menambahkan produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mengedit produk yang ada
     */
    fun editProduct(productId: String, name: String, price: Int, imageUri: Uri? = null) {
        if (name.isBlank() || price <= 0) {
            _error.value = "Nama produk dan harga harus valid"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val currentProduct = _products.value.find { it.id == productId }
                var imageUrl = currentProduct?.imageUrl ?: ""
                if (imageUri != null) {
                    try {
                        imageUrl = storageRepository.uploadProductImage(imageUri)
                    } catch (e: Exception) {
                        _error.value = "Gagal mengupload gambar: ${e.message}"
                        _isLoading.value = false
                        return@launch
                    }
                }
                repository.updateProduct(productId, name, price.toLong(), imageUrl)
                _products.value = _products.value.map {
                    if (it.id == productId) it.copy(name = name, price = price.toLong(), imageUrl = imageUrl)
                    else it
                }
            } catch (e: Exception) {
                _error.value = "Gagal mengedit produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Menghapus produk
     */
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val productToDelete = _products.value.find { it.id == productId }
                if (!productToDelete?.imageUrl.isNullOrEmpty()) {
                    try {
                        storageRepository.deleteProductImage(productToDelete?.imageUrl ?: "")
                    } catch (e: Exception) {
                        _error.value = "Gagal menghapus gambar produk, tapi produk akan tetap dihapus"
                    }
                }
                repository.deleteProduct(productId)
                _products.value = _products.value.filter { it.id != productId }
            } catch (e: Exception) {
                _error.value = "Gagal menghapus produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mengubah kuantitas produk yang dipilih
     */
    fun updateProductQuantity(productId: String, quantity: Int) {
        val updatedProducts = _products.value.map {
            if (it.id == productId) it.copy(quantity = quantity)
            else it
        }
        _products.value = updatedProducts
    }

    /**
     * Mendapatkan daftar produk yang dipilih (kuantitas > 0)
     */
    fun getSelectedProducts(): List<Product> {
        return _products.value.filter { it.quantity > 0 }
    }

    /**
     * Clear semua error
     */
    fun clearError() {
        _error.value = null
    }
}