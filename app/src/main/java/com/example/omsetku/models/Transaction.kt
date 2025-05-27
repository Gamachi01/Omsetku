package com.example.omsetku.models

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val amount: Long = 0,
    val date: Long = 0,
    val category: String = "",
    val description: String = "",
    val createdAt: Long = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Transaction {
            return Transaction(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                type = map["type"] as? String ?: "",
                amount = (map["amount"] as? Number)?.toLong() ?: 0,
                date = (map["date"] as? Number)?.toLong() ?: 0,
                category = map["category"] as? String ?: "",
                description = map["description"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0
            )
        }
    }

    val isIncome: Boolean
        get() = type == TransactionType.INCOME.name

    val isExpense: Boolean
        get() = type == TransactionType.EXPENSE.name

    val formattedAmount: String
        get() = "Rp ${amount.toString().reversed().chunked(3).joinToString(".").reversed()}"
}