package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.data.Transaction
import com.example.omsetku.firebase.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionViewModel : ViewModel() {
    private val repository = FirestoreRepository()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _incomeAmount = MutableStateFlow(0)
    val incomeAmount: StateFlow<Int> = _incomeAmount.asStateFlow()
    
    private val _expenseAmount = MutableStateFlow(0)
    val expenseAmount: StateFlow<Int> = _expenseAmount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadTransactions()
    }
    
    /**
     * Memuat daftar transaksi dari Firestore
     */
    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Hitung start date (1 bulan ke belakang) dan end date (hari ini)
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, -1)
                val startDate = calendar.timeInMillis
                val endDate = System.currentTimeMillis()
                
                val transactionList = repository.getUserTransactions(startDate, endDate)
                
                // Convert dari Map ke Transaction
                val transactionItems = transactionList.map { transactionMap ->
                    val date = transactionMap["date"] as? Long ?: 0L
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    
                    Transaction(
                        type = transactionMap["type"] as? String ?: "",
                        description = transactionMap["description"] as? String ?: "",
                        amount = (transactionMap["amount"] as? Number)?.toInt() ?: 0,
                        date = dateFormat.format(Date(date))
                    )
                }
                
                _transactions.value = transactionItems
                calculateAmounts(transactionItems)
            } catch (e: Exception) {
                _error.value = "Gagal memuat transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Mencatat transaksi baru
     */
    fun saveTransaction(
        type: String,
        amount: Int,
        date: String,
        description: String
    ) {
        if (amount <= 0) {
            _error.value = "Nominal harus lebih dari 0"
            return
        }
        
        if (date.isBlank()) {
            _error.value = "Tanggal tidak boleh kosong"
            return
        }
        
        // Konversi format tanggal dari UI ke timestamp
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val parsedDate = try {
            dateFormat.parse(date)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Tentukan kategori berdasarkan tipe
                val categoryLabel = if (type == "INCOME") "Pemasukan" else "Pengeluaran"
                
                // Simpan ke Firestore
                repository.saveTransaction(
                    type = type,
                    amount = amount.toLong(),
                    date = parsedDate,
                    category = categoryLabel,
                    description = description
                )
                
                // Reload transactions
                loadTransactions()
                
                // Clear error
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Menghitung total pemasukan dan pengeluaran
     */
    private fun calculateAmounts(transactions: List<Transaction>) {
        var income = 0
        var expense = 0
        
        transactions.forEach { transaction ->
            if (transaction.type == "Pemasukan" || transaction.type == "INCOME") {
                income += transaction.amount
            } else {
                expense += transaction.amount
            }
        }
        
        _incomeAmount.value = income
        _expenseAmount.value = expense
    }
    
    /**
     * Mendapatkan ringkasan transaksi (untuk HomeScreen)
     */
    fun getSummary(): Triple<List<Transaction>, Int, Int> {
        return Triple(_transactions.value, _incomeAmount.value, _expenseAmount.value)
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
} 