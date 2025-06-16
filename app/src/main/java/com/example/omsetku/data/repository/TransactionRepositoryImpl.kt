package com.example.omsetku.data.repository

import com.example.omsetku.data.local.TransactionDao
import com.example.omsetku.data.local.TransactionEntity
import com.example.omsetku.data.remote.model.TransactionDto
import com.example.omsetku.domain.model.Transaction
import com.example.omsetku.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore
) : TransactionRepository {
    override fun getTransactions(): Flow<List<Transaction>> =
        transactionDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: String): Transaction? =
        transactionDao.getAll().firstOrNull()?.find { it.id == id }?.toDomain()

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
        firestore.collection("transactions").document(transaction.id).set(transaction.toDto()).await()
    }

    override suspend fun deleteTransaction(id: String) {
        val entity = transactionDao.getAll().firstOrNull()?.find { it.id == id }
        entity?.let { transactionDao.delete(it) }
        firestore.collection("transactions").document(id).delete().await()
    }
}

// Mapping extension
fun TransactionEntity.toDomain(): Transaction {
    val itemsType = object : TypeToken<List<com.example.omsetku.domain.model.CartItem>>() {}.type
    val items: List<com.example.omsetku.domain.model.CartItem> = Gson().fromJson(this.items, itemsType)
    return Transaction(
        id = id,
        userId = userId,
        type = type,
        date = date,
        subtotal = subtotal,
        tax = tax,
        total = total,
        profit = profit,
        items = items
    )
}

fun Transaction.toEntity(): TransactionEntity {
    val itemsJson = Gson().toJson(items)
    return TransactionEntity(
        id = id,
        userId = userId,
        type = type,
        date = date,
        subtotal = subtotal,
        tax = tax,
        total = total,
        profit = profit,
        items = itemsJson
    )
}

fun Transaction.toDto(): TransactionDto = TransactionDto(
    id = id,
    userId = userId,
    type = type,
    date = date,
    subtotal = subtotal,
    tax = tax,
    total = total,
    profit = profit,
    items = items.map { it.toDto() }
) 