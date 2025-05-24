package com.example.omsetku.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.example.omsetku.models.TaxSettings
import com.example.omsetku.models.Transaction

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
        return try {
            auth.currentUser?.uid ?: throw Exception("User belum login")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting current user: ${e.message}")
            throw Exception("User belum login atau sesi telah berakhir")
        }
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
        try {
            val document = userCollection.document(userId).get().await()
            return document.data ?: throw Exception("User data tidak ditemukan")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting user data: ${e.message}")
            // Kembalikan data kosong dengan ID saja daripada crash
            return mapOf("id" to userId, "error" to (e.message ?: "Unknown error"))
        }
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
        try {
            val userId = try {
                getCurrentUserId()
            } catch (e: Exception) {
                Log.e("FirestoreRepository", "Failed to get current user: ${e.message}")
                return emptyList() // Return list kosong daripada crash
            }
            
            try {
                // Gunakan query yang paling sederhana mungkin
                var query = transactionCollection.whereEqualTo("userId", userId)
                
                // Filter berdasarkan tipe transaksi
                if (type != null) {
                    query = query.whereEqualTo("type", type)
                }
                
                // Hilangkan pengurutan dengan Query.Direction untuk menghindari kebutuhan indeks
                val snapshot = query.get().await()
                
                return snapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data
                        data?.let {
                            it["id"] = doc.id
                            
                            // Filter startDate dan endDate secara manual dengan penanganan error
                            try {
                                val date = (it["date"] as? Number)?.toLong() ?: 0L
                                val inRange = (startDate == null || date >= startDate) && 
                                              (endDate == null || date <= endDate)
                                
                                if (inRange) it else null
                            } catch (e: Exception) {
                                // Jika tanggal tidak bisa diproses, masih tampilkan transaksi
                                it
                            }
                        }
                    } catch (e: Exception) {
                        // Abaikan dokumen yang bermasalah
                        null
                    }
                }.sortedByDescending { 
                    try { 
                        (it["date"] as? Number)?.toLong() ?: 0L 
                    } catch (e: Exception) { 
                        0L 
                    }
                } // Urutkan di dalam aplikasi alih-alih di query
            } catch (e: Exception) {
                // Jika terjadi error, kembalikan list kosong daripada crash
                Log.e("FirestoreRepository", "Error querying transactions: ${e.message}")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Fatal error in getUserTransactions: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Mendapatkan total pemasukan dan pengeluaran dalam periode tertentu
     */
    suspend fun getTransactionSummary(startDate: Long, endDate: Long): Map<String, Long> {
        try {
            val transactions = getUserTransactions(startDate, endDate)
            
            var totalIncome = 0L
            var totalExpense = 0L
            
            transactions.forEach { transaction ->
                try {
                    val amount = (transaction["amount"] as? Number)?.toLong() ?: 0
                    val type = transaction["type"] as? String
                    
                    when {
                        type.equals("INCOME", ignoreCase = true) -> totalIncome += amount
                        type.equals("EXPENSE", ignoreCase = true) -> totalExpense += amount
                    }
                } catch (e: Exception) {
                    // Abaikan transaksi yang error
                }
            }
            
            return mapOf(
                "totalIncome" to totalIncome,
                "totalExpense" to totalExpense,
                "balance" to (totalIncome - totalExpense)
            )
        } catch (e: Exception) {
            // Kembalikan default map jika terjadi error
            return mapOf(
                "totalIncome" to 0L,
                "totalExpense" to 0L,
                "balance" to 0L
            )
        }
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
    
    /**
     * Memperbarui data bisnis
     */
    suspend fun updateBusinessData(
        businessId: String,
        name: String? = null,
        type: String? = null,
        address: String? = null,
        email: String? = null,
        phone: String? = null,
        logo: String? = null
    ): Boolean {
        val updateData = hashMapOf<String, Any>()
        
        name?.let { updateData["name"] = it }
        type?.let { updateData["type"] = it }
        address?.let { updateData["address"] = it }
        email?.let { updateData["email"] = it }
        phone?.let { updateData["phone"] = it }
        logo?.let { updateData["logo"] = it }
        
        if (updateData.isEmpty()) return true
        
        return try {
            businessCollection.document(businessId).update(updateData).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Mendapatkan pengaturan pajak
     */
    suspend fun getTaxSettings(userId: String): TaxSettings? {
        return try {
            val document = db.collection("taxSettings")
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                val data = document.data
                if (data != null) {
                    TaxSettings.fromMap(data)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting tax settings", e)
            throw e
        }
    }
    
    /**
     * Menyimpan pengaturan pajak
     */
    suspend fun saveTaxSettings(taxSettings: TaxSettings) {
        try {
            val taxData = TaxSettings.toMap(taxSettings)
            
            db.collection("taxSettings")
                .document(taxSettings.userId)
                .set(taxData)
                .await()
            
            Log.d("FirestoreRepository", "Tax settings saved successfully")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving tax settings", e)
            throw e
        }
    }

    /**
     * Menyimpan data transaksi
     */
    suspend fun saveTransaction(transaction: Transaction) {
        try {
            val transactionData = mapOf(
                "id" to transaction.id,
                "userId" to transaction.userId,
                "type" to transaction.type,
                "amount" to transaction.amount,
                "date" to transaction.date,
                "category" to transaction.category,
                "description" to transaction.description,
                "createdAt" to transaction.createdAt
            )
            
            transactionCollection.document(transaction.id).set(transactionData).await()
            Log.d("FirestoreRepository", "Transaction saved successfully")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving transaction", e)
            throw e
        }
    }
} 