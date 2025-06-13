package com.example.omsetku.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.R
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.firebase.FirestoreRepository
import com.example.omsetku.ui.data.ProductItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = FirestoreRepository()
    private val storageRepository = FirebaseModule.storageRepository

    private val _products = MutableStateFlow<List<ProductItem>>(emptyList())
    val products: StateFlow<List<ProductItem>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Menyimpan mapping ID produk dari hashCode ke ID Firestore
    private val productIdMap = mutableMapOf<Int, String>()

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

                // Convert dari Map ke ProductItem
                val productItems = productsList.map { productMap ->
                    val firestoreId = productMap["id"] as? String ?: ""
                    val id = firestoreId.hashCode()

                    // Simpan mapping ID
                    productIdMap[id] = firestoreId

                    ProductItem(
                        id = id,
                        firestoreId = firestoreId,
                        name = productMap["name"] as? String ?: "",
                        price = (productMap["price"] as? Number)?.toInt() ?: 0,
                        imageRes = R.drawable.logo,  // Default image
                        imageUrl = productMap["imageUrl"] as? String ?: "",
                        quantity = 0,
                        hpp = (productMap["hpp"] as? Number)?.toDouble() ?: 0.0  // Ambil HPP dari Firestore
                    )
                }

                _products.value = productItems
            } catch (e: Exception) {
                _error.value = "Gagal memuat produk: ${e.message}"
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

                // Simpan ke Firestore
                val productId = repository.saveProduct(
                    name = name,
                    price = price.toLong(),
                    imageUrl = imageUrl
                )

                // Simpan mapping ID
                val hashedId = productId.hashCode()
                productIdMap[hashedId] = productId

                // Update UI dengan produk baru
                val newProduct = ProductItem(
                    id = hashedId,
                    firestoreId = productId,
                    name = name,
                    price = price,
                    imageRes = R.drawable.logo,
                    imageUrl = imageUrl,
                    quantity = 0,
                    hpp = 0.0  // Default HPP untuk produk baru
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
    fun editProduct(productId: Int, name: String, price: Int, imageUri: Uri? = null) {
        if (name.isBlank() || price <= 0) {
            _error.value = "Nama produk dan harga harus valid"
            return
        }

        // Dapatkan Firestore ID dari mapping
        val firestoreId = productIdMap[productId] ?: run {
            _error.value = "ID produk tidak valid"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Cari produk yang akan diupdate
                val currentProduct = _products.value.find { it.id == productId }
                var imageUrl = currentProduct?.imageUrl ?: ""

                // Upload gambar baru jika ada
                if (imageUri != null) {
                    try {
                        imageUrl = storageRepository.uploadProductImage(imageUri)
                    } catch (e: Exception) {
                        _error.value = "Gagal mengupload gambar: ${e.message}"
                        _isLoading.value = false
                        return@launch
                    }
                }

                // Perbarui di Firestore
                val success = repository.updateProduct(
                    productId = firestoreId,
                    name = name,
                    price = price.toLong(),
                    imageUrl = imageUrl
                )

                if (success) {
                    // Update di state lokal
                    val updatedProducts = _products.value.map {
                        if (it.id == productId) {
                            it.copy(name = name, price = price, imageUrl = imageUrl)
                        } else {
                            it
                        }
                    }

                    _products.value = updatedProducts
                } else {
                    _error.value = "Gagal memperbarui produk di database"
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
    fun deleteProduct(productId: Int) {
        // Dapatkan Firestore ID dari mapping
        val firestoreId = productIdMap[productId] ?: run {
            _error.value = "ID produk tidak valid"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Cari produk yang akan dihapus
                val productToDelete = _products.value.find { it.id == productId }

                // Hapus gambar jika ada
                if (!productToDelete?.imageUrl.isNullOrEmpty()) {
                    try {
                        storageRepository.deleteProductImage(productToDelete?.imageUrl ?: "")
                    } catch (e: Exception) {
                        // Lanjutkan meskipun gagal menghapus gambar
                        _error.value = "Gagal menghapus gambar produk, tapi produk akan tetap dihapus"
                    }
                }

                // Hapus di Firestore
                val success = repository.deleteProduct(firestoreId)

                if (success) {
                    // Hapus dari state lokal
                    _products.value = _products.value.filter { it.id != productId }

                    // Hapus dari mapping
                    productIdMap.remove(productId)
                } else {
                    _error.value = "Gagal menghapus produk di database"
                }
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
    fun updateProductQuantity(productId: Int, quantity: Int) {
        val updatedProducts = _products.value.map {
            if (it.id == productId) {
                it.copy(quantity = quantity)
            } else {
                it
            }
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