package com.example.omsetku.domain.usecase

import com.example.omsetku.models.Transaction
import com.example.omsetku.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.addTransaction(transaction)
    }
} 