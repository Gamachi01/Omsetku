package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(id: String) {
        transactionRepository.deleteTransaction(id)
    }
} 