package com.example.omsetku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val date: Long,
    val subtotal: Long,
    val tax: Long,
    val total: Long,
    val profit: Long,
    val items: String // JSON string dari list item, untuk simplifikasi
) 