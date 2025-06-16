package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.Transaction
import com.example.omsetku.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(id: String): Transaction? {
        return transactionRepository.getTransactionById(id)
    }
} 