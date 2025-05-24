package com.example.omsetku.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Repository yang menangani operasi Firestore Database
 */
class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // Referensi koleksi
    private val userCollection = db.collection("users")
    private val businessCollection = db.collection("businesses")
    private val transactionCollection = db.collection("transactions")
    private val productCollection = db.collection("products")
    
    /**
     * Mendapatkan ID user saat ini yang sedang login
     */
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User belum login")
    }
    
    /**
     * Menyimpan data user setelah registrasi
     */
    suspend fun saveUserData(
        userId: String,
        name: String,
        email: String,
        phone: String,
        gender: String = "",
        position: String = ""
    ) {
        val userData = hashMapOf(
            "id" to userId,
            "name" to name,
            "email" to email,
            "phone" to phone,
            "gender" to gender,
            "position" to position,
            "createdAt" to System.currentTimeMillis()
        )
        
        userCollection.document(userId).set(userData).await()
    }
    
    /**
     * Mendapatkan data user berdasarkan ID
     */
    suspend fun getUserData(userId: String = getCurrentUserId()): Map<String, Any> {
        val document = userCollection.document(userId).get().await()
        return document.data ?: throw Exception("User data tidak ditemukan")
    }
    
    /**
     * Memperbarui data user
     */
    suspend fun updateUserData(
        name: String? = null,
        phone: String? = null,
        gender: String? = null,
        position: String? = null
    ) {
        val userId = getCurrentUserId()
        val updateData = hashMapOf<String, Any>()
        
        name?.let { updateData["name"] = it }
        phone?.let { updateData["phone"] = it }
        gender?.let { updateData["gender"] = it }
        position?.let { updateData["position"] = it }
        
        if (updateData.isNotEmpty()) {
            userCollection.document(userId).update(updateData).await()
        }
    }
    
    /**
     * Menyimpan data bisnis
     */
    suspend fun saveBusinessData(
        name: String,
        type: String,
        address: String,
        email: String? = null,
        phone: String? = null,
        logo: String? = null
    ): String {
        val userId = getCurrentUserId()
        
        val businessData = hashMapOf(
            "ownerId" to userId,
            "name" to name,
            "type" to type,
            "address" to address,
            "email" to email,
            "phone" to phone,
            "logo" to logo,
            "createdAt" to System.currentTimeMillis()
        )
        
        // Hapus field null
        businessData.entries.removeIf { it.value == null }
        
        val docRef = businessCollection.add(businessData).await()
        return docRef.id
    }
    
    /**
     * Mendapatkan data bisnis milik user saat ini
     */
    suspend fun getUserBusinesses(): List<Map<String, Any>> {
        val userId = getCurrentUserId()
        val snapshot = businessCollection
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
        
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data
            data?.let {
                it["id"] = doc.id
                it
            }
        }
    }
    
    /**
     * Menyimpan data produk
     */
    suspend fun saveProduct(
        name: String,
        price: Long,
        imageUrl: String? = null,
        description: String? = null
    ): String {
        val userId = getCurrentUserId()
        
        val productData = hashMapOf(
            "ownerId" to userId,
            "name" to name,
            "price" to price,
            "imageUrl" to imageUrl,
            "description" to description,
            "createdAt" to System.currentTimeMillis()
        )
        
        // Hapus field null
        productData.entries.removeIf { it.value == null }
        
        val docRef = productCollection.add(productData).await()
        return docRef.id
    }
    
    /**
     * Mendapatkan daftar produk milik user saat ini
     */
    suspend fun getUserProducts(): List<Map<String, Any>> {
        val userId = getCurrentUserId()
        val snapshot = productCollection
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
        
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data
            data?.let {
                it["id"] = doc.id
                it
            }
        }
    }
    
    /**
     * Mencatat transaksi baru
     */
    suspend fun saveTransaction(
        type: String, // "INCOME" atau "EXPENSE"
        amount: Long,
        date: Long,
        category: String,
        description: String? = null
    ): String {
        val userId = getCurrentUserId()
        
        val transactionData = hashMapOf(
            "userId" to userId,
            "type" to type,
            "amount" to amount,
            "date" to date,
            "category" to category,
            "description" to description,
            "createdAt" to System.currentTimeMillis()
        )
        
        // Hapus field null
        transactionData.entries.removeIf { it.value == null }
        
        val docRef = transactionCollection.add(transactionData).await()
        return docRef.id
    }
    
    /**
     * Mendapatkan daftar transaksi milik user saat ini
     */
    suspend fun getUserTransactions(
        startDate: Long? = null,
        endDate: Long? = null,
        type: String? = null
    ): List<Map<String, Any>> {
        val userId = getCurrentUserId()
        var query = transactionCollection.whereEqualTo("userId", userId)
        
        // Filter berdasarkan tanggal
        if (startDate != null) {
            query = query.whereGreaterThanOrEqualTo("date", startDate)
        }
        if (endDate != null) {
            query = query.whereLessThanOrEqualTo("date", endDate)
        }
        
        // Filter berdasarkan tipe transaksi
        if (type != null) {
            query = query.whereEqualTo("type", type)
        }
        
        // Urutkan berdasarkan tanggal terbaru
        query = query.orderBy("date", Query.Direction.DESCENDING)
        
        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data
            data?.let {
                it["id"] = doc.id
                it
            }
        }
    }
    
    /**
     * Mendapatkan total pemasukan dan pengeluaran dalam periode tertentu
     */
    suspend fun getTransactionSummary(startDate: Long, endDate: Long): Map<String, Long> {
        val transactions = getUserTransactions(startDate, endDate)
        
        var totalIncome = 0L
        var totalExpense = 0L
        
        transactions.forEach { transaction ->
            val amount = (transaction["amount"] as? Number)?.toLong() ?: 0
            val type = transaction["type"] as? String
            
            when (type) {
                "INCOME" -> totalIncome += amount
                "EXPENSE" -> totalExpense += amount
            }
        }
        
        return mapOf(
            "totalIncome" to totalIncome,
            "totalExpense" to totalExpense,
            "balance" to (totalIncome - totalExpense)
        )
    }
    
    /**
     * Memperbarui data produk
     */
    suspend fun updateProduct(
        productId: String,
        name: String? = null,
        price: Long? = null,
        imageUrl: String? = null,
        description: String? = null
    ): Boolean {
        val updateData = hashMapOf<String, Any>()
        
        name?.let { updateData["name"] = it }
        price?.let { updateData["price"] = it }
        imageUrl?.let { updateData["imageUrl"] = it }
        description?.let { updateData["description"] = it }
        
        if (updateData.isEmpty()) return true
        
        return try {
            productCollection.document(productId).update(updateData).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Menghapus produk
     */
    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            productCollection.document(productId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 