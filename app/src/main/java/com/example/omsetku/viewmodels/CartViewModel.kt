package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.models.CartItem
import com.example.omsetku.models.Product
import com.example.omsetku.models.Transaction
import com.example.omsetku.models.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CartViewModel : ViewModel() {
    private val firestoreRepository = FirebaseModule.firestoreRepository
    private val authRepository = FirebaseModule.authRepository
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _transactionSuccess = MutableStateFlow(false)
    val transactionSuccess: StateFlow<Boolean> = _transactionSuccess.asStateFlow()
    
    fun addToCart(product: Product, quantity: Int) {
        val existingItem = _cartItems.value.find { it.productId == product.id }
        
        val updatedItems = if (existingItem != null) {
            // Update quantity jika produk sudah ada di keranjang
            _cartItems.value.map {
                if (it.productId == product.id) {
                    it.copy(quantity = it.quantity + quantity)
                } else {
                    it
                }
            }
        } else {
            // Tambahkan produk baru ke keranjang
            _cartItems.value + CartItem(
                productId = product.id,
                name = product.name,
                price = product.price.toInt(),
                quantity = quantity,
                imageRes = product.imageRes
            )
        }
        
        _cartItems.value = updatedItems
    }
    
    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        
        _cartItems.value = _cartItems.value.map {
            if (it.productId == productId) {
                it.copy(quantity = quantity)
            } else {
                it
            }
        }
    }
    
    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.productId != productId }
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
    }
    
    fun getTotalItems(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
    
    fun getSubtotal(): Int {
        return _cartItems.value.sumOf { it.subtotal }
    }
    
    /**
     * Menyimpan transaksi ke Firestore
     * @param taxEnabled Status pajak aktif/tidak
     * @param taxRate Persentase pajak (contoh: 10 untuk 10%)
     */
    fun saveTransaction(taxEnabled: Boolean, taxRate: Int) {
        val currentUser = authRepository.getCurrentUser() ?: return
        val items = _cartItems.value
        
        if (items.isEmpty()) {
            _error.value = "Keranjang belanja kosong"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val now = System.currentTimeMillis()
                val subtotal = getSubtotal()
                val tax = if (taxEnabled) (subtotal * taxRate / 100) else 0
                val total = subtotal + tax
                
                // Buat transaksi baru
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.uid,
                    type = TransactionType.INCOME.name,
                    amount = total.toLong(),
                    date = now,
                    category = "Penjualan",
                    description = generateTransactionDescription(items),
                    createdAt = now
                )
                
                // Simpan transaksi ke Firestore
                firestoreRepository.saveTransaction(transaction)
                
                // Reset state keranjang
                _transactionSuccess.value = true
                clearCart()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun generateTransactionDescription(items: List<CartItem>): String {
        return when {
            items.isEmpty() -> "Penjualan"
            items.size == 1 -> "Penjualan ${items[0].name}"
            else -> {
                val mainItem = items[0].name
                val otherCount = items.size - 1
                "Penjualan $mainItem dan $otherCount produk lainnya"
            }
        }
    }
    
    fun resetTransactionSuccess() {
        _transactionSuccess.value = false
    }
    
    fun clearError() {
        _error.value = null
    }
} 