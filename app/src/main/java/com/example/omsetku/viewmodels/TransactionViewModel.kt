package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.firebase.FirebaseModule
import com.example.omsetku.models.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionViewModel : ViewModel() {
    private val firestoreRepository = FirebaseModule.firestoreRepository
    
    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State untuk error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // State untuk daftar transaksi
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    // State untuk transaksi terpilih (untuk detail)
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()
    
    // State untuk summary transaksi
    private val _transactionSummary = MutableStateFlow<Map<String, Long>>(emptyMap())
    val transactionSummary: StateFlow<Map<String, Long>> = _transactionSummary.asStateFlow()
    
    // Filter date range
    private val _startDate = MutableStateFlow<Long>(getStartOfMonth())
    val startDate: StateFlow<Long> = _startDate.asStateFlow()
    
    private val _endDate = MutableStateFlow<Long>(getEndOfMonth())
    val endDate: StateFlow<Long> = _endDate.asStateFlow()
    
    // Filter type
    private val _filterType = MutableStateFlow<String?>(null)
    val filterType: StateFlow<String?> = _filterType.asStateFlow()
    
    init {
        // Load transaksi saat ViewModel dibuat
        loadTransactions()
    }
    
    /**
     * Memuat daftar transaksi
     */
    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val transactionList = firestoreRepository.getUserTransactions(
                    startDate = _startDate.value,
                    endDate = _endDate.value,
                    type = _filterType.value
                )
                _transactions.value = transactionList.map { Transaction.fromMap(it) }
                
                // Load summary
                loadTransactionSummary()
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat transaksi"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Memuat ringkasan transaksi
     */
    private fun loadTransactionSummary() {
        viewModelScope.launch {
            try {
                val summary = firestoreRepository.getTransactionSummary(
                    startDate = _startDate.value,
                    endDate = _endDate.value
                )
                _transactionSummary.value = summary
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat ringkasan transaksi"
            }
        }
    }
    
    /**
     * Menyimpan transaksi baru
     */
    fun saveTransaction(
        type: String,
        amount: Long,
        date: Long,
        category: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                firestoreRepository.saveTransaction(
                    type = type,
                    amount = amount,
                    date = date,
                    category = category,
                    description = description
                )
                
                // Refresh data
                loadTransactions()
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal menyimpan transaksi"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Set filter tanggal
     */
    fun setDateRange(startDate: Long, endDate: Long) {
        _startDate.value = startDate
        _endDate.value = endDate
        loadTransactions()
    }
    
    /**
     * Set filter tipe transaksi
     */
    fun setTypeFilter(type: String?) {
        _filterType.value = type
        loadTransactions()
    }
    
    /**
     * Memilih transaksi untuk melihat detail
     */
    fun selectTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
    }
    
    /**
     * Membersihkan transaksi terpilih
     */
    fun clearSelectedTransaction() {
        _selectedTransaction.value = null
    }
    
    /**
     * Reset filter
     */
    fun resetFilters() {
        _startDate.value = getStartOfMonth()
        _endDate.value = getEndOfMonth()
        _filterType.value = null
        loadTransactions()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Mendapatkan awal bulan untuk filter
     */
    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Mendapatkan akhir bulan untuk filter
     */
    private fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
    
    /**
     * Format tanggal menjadi string
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }
} 