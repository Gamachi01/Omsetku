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
import java.util.*

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

    /**
     * Fungsi lama (default): load transaksi 1 bulan terakhir
     */
    fun loadTransactions() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis
        val endDate = System.currentTimeMillis()
        loadTransactions(startDate, endDate)
    }

    /**
     * Fungsi baru: load transaksi berdasarkan rentang tanggal
     */
    fun loadTransactions(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val transactionList = repository.getUserTransactions(startDate, endDate)

                val transactionItems = transactionList.mapNotNull { transactionMap ->
                    try {
                        val date = transactionMap["date"] as? Long ?: 0L
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        val type = transactionMap["type"] as? String ?: ""
                        val description = transactionMap["description"] as? String ?: ""
                        val category = transactionMap["category"] as? String ?: ""

                        val amount = when (val amountValue = transactionMap["amount"]) {
                            is Number -> amountValue.toInt()
                            is String -> amountValue.toIntOrNull() ?: 0
                            else -> 0
                        }

                        Transaction(
                            type = type,
                            description = description,
                            amount = amount,
                            date = dateFormat.format(Date(date)),
                            category = category
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                val sortedTransactions = transactionItems.sortedByDescending {
                    try {
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        dateFormat.parse(it.date)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                }

                _transactions.value = sortedTransactions
                calculateAmounts(sortedTransactions)

            } catch (e: Exception) {
                _transactions.value = emptyList()
                _incomeAmount.value = 0
                _expenseAmount.value = 0
                _error.value = "Gagal memuat transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTransaction(
        type: String,
        amount: Int,
        date: String,
        description: String,
        category: String
    ) {
        if (amount <= 0) {
            _error.value = "Nominal harus lebih dari 0"
            return
        }

        if (date.isBlank()) {
            _error.value = "Tanggal tidak boleh kosong"
            return
        }

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
                repository.saveTransaction(
                    type = type,
                    amount = amount.toLong(),
                    date = parsedDate,
                    category = category,
                    description = description
                )

                loadTransactions() // panggil default load

                if (_error.value == null) {
                    _error.value = null
                }

            } catch (e: Exception) {
                _error.value = "Gagal menyimpan transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateAmounts(transactions: List<Transaction>) {
        var income = 0
        var expense = 0

        transactions.forEach { transaction ->
            val isIncome = transaction.type.equals("INCOME", ignoreCase = true) ||
                    transaction.type.equals("Pemasukan", ignoreCase = true)

            if (isIncome) {
                income += transaction.amount
            } else {
                expense += transaction.amount
            }
        }

        _incomeAmount.value = income
        _expenseAmount.value = expense
    }

    fun getSummary(): Triple<List<Transaction>, Int, Int> {
        return Triple(_transactions.value, _incomeAmount.value, _expenseAmount.value)
    }

    fun clearError() {
        _error.value = null
    }
}
