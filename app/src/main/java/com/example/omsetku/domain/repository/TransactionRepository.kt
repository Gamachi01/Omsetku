package com.example.omsetku.domain.repository

import com.example.omsetku.models.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun getAllTransactions(userId: String): List<Transaction>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
} 