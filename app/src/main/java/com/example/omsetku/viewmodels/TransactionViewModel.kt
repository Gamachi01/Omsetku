package com.example.omsetku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.models.Transaction
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

    private val _incomeAmount = MutableStateFlow(0L)
    val incomeAmount: StateFlow<Long> = _incomeAmount.asStateFlow()

    private val _expenseAmount = MutableStateFlow(0L)
    val expenseAmount: StateFlow<Long> = _expenseAmount.asStateFlow()

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
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val type = transactionMap["type"] as? String ?: ""
                        val description = transactionMap["description"] as? String ?: ""
                        val category = transactionMap["category"] as? String ?: ""

                        val amount = when (val amountValue = transactionMap["amount"]) {
                            is Number -> amountValue.toLong()
                            is String -> amountValue.toLongOrNull() ?: 0L
                            else -> 0L
                        }

                        Transaction(
                            id = transactionMap["id"] as? String ?: "",
                            userId = transactionMap["userId"] as? String ?: "",
                            type = type,
                            amount = amount,
                            date = date,
                            category = category,
                            description = description,
                            createdAt = transactionMap["createdAt"] as? Long ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                val sortedTransactions = transactionItems.sortedByDescending { it.date }

                _transactions.value = sortedTransactions
                calculateAmounts(sortedTransactions)

            } catch (e: Exception) {
                _transactions.value = emptyList()
                _incomeAmount.value = 0L
                _expenseAmount.value = 0L
                _error.value = "Gagal memuat transaksi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTransaction(
        type: String,
        amount: Long,
        date: Long,
        description: String,
        category: String
    ) {
        if (amount <= 0L) {
            _error.value = "Nominal harus lebih dari 0"
            return
        }

        if (date <= 0L) {
            _error.value = "Tanggal tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.saveTransaction(
                    type = type,
                    amount = amount,
                    date = date,
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
        var income = 0L
        var expense = 0L

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

    fun getSummary(): Triple<List<Transaction>, Long, Long> {
        return Triple(_transactions.value, _incomeAmount.value.toLong(), _expenseAmount.value.toLong())
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Mengelompokkan dan menjumlahkan pengeluaran berdasarkan description
     * Hanya untuk transaksi dengan type EXPENSE
     * @return List<Pair<description, totalAmount>>
     */
    fun getGroupedExpenses(): List<Pair<String, Long>> {
        return _transactions.value
            .filter { it.type.equals("EXPENSE", ignoreCase = true) }
            .groupBy { it.description }
            .map { (desc, list) ->
                desc to list.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
    }
}
