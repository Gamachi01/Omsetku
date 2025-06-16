package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.Transaction
import com.example.omsetku.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = transactionRepository.getTransactions()
} 