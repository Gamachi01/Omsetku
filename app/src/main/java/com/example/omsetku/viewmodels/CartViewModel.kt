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

    // UserID untuk identifikasi keranjang
    private var currentUserId: String = ""

    init {
        // Inisialisasi user ID saat ViewModel dibuat
        updateCurrentUserId()
    }

    // Fungsi untuk update current user ID
    private fun updateCurrentUserId() {
        authRepository.getCurrentUser()?.let { user ->
            if (currentUserId != user.uid) {
                // Jika user ID berubah, bersihkan keranjang
                if (currentUserId.isNotEmpty()) {
                    clearCart()
                }
                currentUserId = user.uid
            }
        } ?: run {
            // Jika tidak ada user yang login, kosongkan keranjang
            currentUserId = ""
            clearCart()
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        // Pastikan user ID terupdate
        updateCurrentUserId()

        if (quantity <= 0 || currentUserId.isEmpty()) return

        val productId = product.firestoreId
        val existingItem = _cartItems.value.find { it.productId == productId }

        // Ambil HPP dari produk
        val hpp = product.hpp

        val updatedItems = if (existingItem != null) {
            // Update quantity jika produk sudah ada di keranjang
            _cartItems.value.map {
                if (it.productId == productId) {
                    it.copy(quantity = it.quantity + quantity)
                } else {
                    it
                }
            }
        } else {
            // Tambahkan produk baru ke keranjang
            _cartItems.value + CartItem(
                productId = productId,
                name = product.name,
                price = product.price,
                quantity = quantity,
                imageRes = product.imageRes,
                hpp = hpp
            )
        }

        _cartItems.value = updatedItems
    }

    fun updateQuantity(productId: String, quantity: Int) {
        // Pastikan user ID terupdate
        updateCurrentUserId()

        if (currentUserId.isEmpty()) return

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
        // Pastikan user ID terupdate
        updateCurrentUserId()

        if (currentUserId.isEmpty()) return

        _cartItems.value = _cartItems.value.filter { it.productId != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getTotalItems(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    fun getSubtotal(): Long {
        return _cartItems.value.sumOf { it.subtotal }
    }

    /**
     * Menyimpan transaksi ke Firestore
     * @param taxEnabled Status pajak aktif/tidak
     * @param taxRate Persentase pajak (contoh: 10 untuk 10%)
     * @param products List produk untuk menghitung profit
     * @param marginProfit Persentase margin profit yang diinginkan
     */
    fun saveTransaction(taxEnabled: Boolean, taxRate: Int, products: List<Product>, marginProfit: Double) {
        // Pastikan user ID terupdate
        updateCurrentUserId()

        val currentUser = authRepository.getCurrentUser() ?: return
        val items = _cartItems.value

        if (items.isEmpty()) {
            _error.value = "Keranjang belanja kosong"
            return
        }

        // Pastikan user ID di keranjang sama dengan user yang sedang login
        if (currentUserId != currentUser.uid) {
            _error.value = "Sesi login telah berubah, silakan muat ulang keranjang"
            clearCart()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val now = System.currentTimeMillis()
                val subtotal = getSubtotal()
                val tax = if (taxEnabled) (subtotal * taxRate / 100) else 0L
                val total = subtotal + tax

                // Hitung total profit
                val totalProfit = calculateTotalProfit(products, marginProfit)

                // Buat transaksi baru
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.uid,
                    type = TransactionType.INCOME.name,
                    amount = total,
                    date = now,
                    category = "Penjualan",
                    description = generateTransactionDescription(items),
                    createdAt = now
                )

                // Simpan transaksi ke Firestore
                firestoreRepository.saveTransaction(transaction)
                
                // Set transaction success, clearCart dipanggil dari UI setelah dialog sukses/profit ditutup
                _transactionSuccess.value = true
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

    /**
     * Menghitung total profit dari transaksi berdasarkan HPP
     */
    fun calculateTotalProfit(products: List<Product>, marginProfit: Double): Double {
        return _cartItems.value.sumOf { item ->
            val product = products.find { 
                // Coba cari berdasarkan firestoreId dulu, jika tidak ada coba berdasarkan id
                it.firestoreId == item.productId || it.id.toString() == item.productId 
            }
            if (product != null) {
                // Hitung profit per item: harga jual - HPP
                val profitPerItem = item.price - product.hpp
                // Total profit = profit per item × quantity
                profitPerItem * item.quantity
            } else {
                0.0
            }
        }
    }

    // Fungsi baru: clear keranjang setelah dialog sukses/profit ditutup
    fun clearCartAfterSuccess() {
        clearCart()
    }
}