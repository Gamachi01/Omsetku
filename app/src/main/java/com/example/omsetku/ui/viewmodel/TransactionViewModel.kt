package com.example.omsetku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omsetku.domain.model.Transaction
import com.example.omsetku.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        getTransactions()
    }

    fun getTransactions() {
        getTransactionsUseCase().onEach { _transactions.value = it }.launchIn(viewModelScope)
    }

    suspend fun getTransactionById(id: String): Transaction? {
        return getTransactionByIdUseCase(id)
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch { addTransactionUseCase(transaction) }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch { deleteTransactionUseCase(id) }
    }
} 