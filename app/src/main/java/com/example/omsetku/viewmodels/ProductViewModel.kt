package com.example.omsetku.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.R
import com.example.omsetku.data.repository.ProductRepository
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.firebase.FirestoreRepository
import com.example.omsetku.ui.data.ProductItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val storageRepository = FirebaseModule.storageRepository

    private val _products = MutableStateFlow<List<ProductItem>>(emptyList())
    val products: StateFlow<List<ProductItem>> = _products

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
            try {
                // Sync dengan Firestore
                repository.syncProducts()
                
                // Observe perubahan dari database lokal
                repository.getAllProducts()
                    .catch { e ->
                        _error.value = e.message
                        _isLoading.value = false
                    }
                    .collect { products ->
                        _products.value = products
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
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
                // Upload gambar jika ada
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

                // Simpan ke repository
                val newProduct = ProductItem(
                    id = System.currentTimeMillis().toString(),
                    firestoreId = "",
                    name = name,
                    price = price,
                    imageRes = R.drawable.logo,
                    imageUrl = imageUrl,
                    quantity = 0,
                    hpp = 0.0
                )
                repository.addProduct(newProduct)
                // Update UI dengan produk baru
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
                val updatedProduct = ProductItem(
                    id = productId,
                    firestoreId = currentProduct?.firestoreId ?: "",
                    name = name,
                    price = price,
                    imageRes = R.drawable.logo,
                    imageUrl = imageUrl,
                    quantity = 0,
                    hpp = 0.0
                )
                repository.updateProduct(updatedProduct)
                _products.value = _products.value.map {
                    if (it.id == productId) it.copy(name = name, price = price, imageUrl = imageUrl)
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
    fun getSelectedProducts(): List<ProductItem> {
        return _products.value.filter { it.quantity > 0 }
    }

    /**
     * Clear semua error
     */
    fun clearError() {
        _error.value = null
    }
}