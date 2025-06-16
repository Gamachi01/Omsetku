package com.example.omsetku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class, CartItemEntity::class, TransactionEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao
} 