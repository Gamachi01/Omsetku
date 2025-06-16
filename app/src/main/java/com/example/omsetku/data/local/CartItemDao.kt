package com.example.omsetku.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CartItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()
} 