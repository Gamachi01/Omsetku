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
import com.example.omsetku.models.Business
import com.example.omsetku.models.CartItem
import com.example.omsetku.models.User

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
    private val hppCollection = db.collection("hpp")

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
     * Menyimpan data bisnis (dengan model Business)
     */
    suspend fun saveBusiness(business: Business) {
        val data = mapOf(
            "id" to business.id,
            "ownerId" to getCurrentUserId(),
            "name" to business.name,
            "type" to business.type,
            "address" to business.address,
            "email" to business.email,
            "phone" to business.phone,
            "logo" to business.logo,
            "createdAt" to business.createdAt
        )
        businessCollection.document(business.id).set(data).await()
    }

    /**
     * Memperbarui data bisnis (dengan model Business)
     */
    suspend fun updateBusiness(business: Business) {
        val updateData = hashMapOf<String, Any>()
        business.name.let { updateData["name"] = it }
        business.type.let { updateData["type"] = it }
        business.address.let { updateData["address"] = it }
        business.email?.let { updateData["email"] = it }
        business.phone?.let { updateData["phone"] = it }
        business.logo?.let { updateData["logo"] = it }
        business.createdAt.let { updateData["createdAt"] = it }
        businessCollection.document(business.id).update(updateData).await()
    }

    /**
     * Menghapus bisnis berdasarkan ID
     */
    suspend fun deleteBusiness(businessId: String) {
        businessCollection.document(businessId).delete().await()
    }

    /**
     * Menghapus semua bisnis milik user saat ini
     */
    suspend fun clearBusiness() {
        val userId = getCurrentUserId()
        val snapshot = businessCollection.whereEqualTo("ownerId", userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    /**
     * Mengambil semua bisnis milik user saat ini (List<Business>)
     */
    suspend fun getUserBusinessesModel(): List<Business> {
        val userId = getCurrentUserId()
        val snapshot = businessCollection.whereEqualTo("ownerId", userId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            data["id"] = doc.id
            try {
                Business(
                    id = doc.id,
                    name = data["name"] as? String ?: "",
                    type = data["type"] as? String ?: "",
                    address = data["address"] as? String ?: "",
                    email = data["email"] as? String,
                    phone = data["phone"] as? String,
                    logo = data["logo"] as? String,
                    createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
                )
            } catch (e: Exception) {
                null
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
        description: String? = null,
        hppValue: Double? = null
    ): Boolean {
        val updateData = hashMapOf<String, Any>()

        name?.let { updateData["name"] = it }
        price?.let { updateData["price"] = it }
        imageUrl?.let { updateData["imageUrl"] = it }
        description?.let { updateData["description"] = it }
        hppValue?.let { updateData["hpp"] = it }

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

    /**
     * Menyimpan data HPP ke Firestore
     * @param hppData Map yang berisi data HPP yang akan disimpan
     */
    suspend fun saveHpp(hppData: Map<String, Any>) {
        try {
            hppCollection.add(hppData).await()
        } catch (e: Exception) {
            throw Exception("Gagal menyimpan data HPP: ${e.message}")
        }
    }

    /**
     * Mengambil data HPP untuk produk tertentu
     * @param productId ID produk yang akan diambil data HPP-nya
     * @return List of Map yang berisi data HPP
     */
    suspend fun getHppByProduct(productId: String): List<Map<String, Any>> {
        return try {
            val snapshot = hppCollection
                .whereEqualTo("productId", productId)
                .orderBy("tanggalHitung", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            throw Exception("Gagal mengambil data HPP: ${e.message}")
        }
    }

    /**
     * Mengambil semua data HPP
     * @return List of Map yang berisi semua data HPP
     */
    suspend fun getAllHpp(): List<Map<String, Any>> {
        return try {
            val snapshot = hppCollection
                .orderBy("tanggalHitung", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            throw Exception("Gagal mengambil data HPP: ${e.message}")
        }
    }

    /**
     * Menyimpan item keranjang
     */
    suspend fun saveCartItem(cartItem: CartItem) {
        val data = mapOf(
            "productId" to cartItem.productId,
            "ownerId" to getCurrentUserId(),
            "name" to cartItem.name,
            "price" to cartItem.price,
            "quantity" to cartItem.quantity,
            "imageRes" to cartItem.imageRes,
            "hpp" to cartItem.hpp
        )
        db.collection("cart_items").document(cartItem.productId).set(data).await()
    }

    /**
     * Update item keranjang
     */
    suspend fun updateCartItem(cartItem: CartItem) {
        val updateData = hashMapOf<String, Any>()
        updateData["name"] = cartItem.name
        updateData["price"] = cartItem.price
        updateData["quantity"] = cartItem.quantity
        updateData["imageRes"] = cartItem.imageRes
        updateData["hpp"] = cartItem.hpp
        db.collection("cart_items").document(cartItem.productId).update(updateData).await()
    }

    /**
     * Hapus item keranjang berdasarkan productId
     */
    suspend fun deleteCartItem(productId: String) {
        db.collection("cart_items").document(productId).delete().await()
    }

    /**
     * Hapus semua item keranjang milik user saat ini
     */
    suspend fun clearCart() {
        val userId = getCurrentUserId()
        val snapshot = db.collection("cart_items").whereEqualTo("ownerId", userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    /**
     * Ambil semua item keranjang milik user saat ini
     */
    suspend fun getUserCartItems(): List<CartItem> {
        val userId = getCurrentUserId()
        val snapshot = db.collection("cart_items").whereEqualTo("ownerId", userId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            try {
                CartItem(
                    productId = data["productId"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    price = (data["price"] as? Number)?.toLong() ?: 0L,
                    quantity = (data["quantity"] as? Number)?.toInt() ?: 0,
                    imageRes = (data["imageRes"] as? Number)?.toInt() ?: 0,
                    hpp = (data["hpp"] as? Number)?.toDouble() ?: 0.0
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Mengambil satu bisnis utama milik user (jika ada)
     */
    suspend fun getUserBusiness(): Business? {
        val userId = getCurrentUserId()
        val snapshot = businessCollection.whereEqualTo("ownerId", userId).limit(1).get().await()
        val doc = snapshot.documents.firstOrNull() ?: return null
        val data = doc.data ?: return null
        data["id"] = doc.id
        return try {
            Business(
                id = doc.id,
                name = data["name"] as? String ?: "",
                type = data["type"] as? String ?: "",
                address = data["address"] as? String ?: "",
                email = data["email"] as? String,
                phone = data["phone"] as? String,
                logo = data["logo"] as? String,
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Menyimpan atau update pengaturan pajak
     */
    suspend fun saveOrUpdateTaxSettings(taxSettings: TaxSettings) {
        val taxData = TaxSettings.toMap(taxSettings)
        db.collection("taxSettings").document(taxSettings.userId).set(taxData).await()
    }

    /**
     * Menghapus pengaturan pajak berdasarkan id
     */
    suspend fun deleteTaxSettings(id: String) {
        db.collection("taxSettings").document(id).delete().await()
    }

    /**
     * Mengambil pengaturan pajak berdasarkan userId
     */
    suspend fun getTaxSettingsByUserId(userId: String): TaxSettings? {
        val document = db.collection("taxSettings").document(userId).get().await()
        return if (document.exists()) TaxSettings.fromMap(document.data!!) else null
    }

    /**
     * Menghapus semua pengaturan pajak (hati-hati, biasanya hanya untuk admin)
     */
    suspend fun deleteAllTaxSettings() {
        val snapshot = db.collection("taxSettings").get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    /**
     * Update transaksi
     */
    suspend fun updateTransaction(transaction: Transaction) {
        val updateData = hashMapOf<String, Any>()
        updateData["type"] = transaction.type
        updateData["amount"] = transaction.amount
        updateData["date"] = transaction.date
        updateData["category"] = transaction.category
        updateData["description"] = transaction.description ?: ""
        updateData["createdAt"] = transaction.createdAt
        transactionCollection.document(transaction.id).update(updateData).await()
    }

    /**
     * Hapus transaksi
     */
    suspend fun deleteTransaction(transactionId: String) {
        transactionCollection.document(transactionId).delete().await()
    }

    /**
     * Simpan user
     */
    suspend fun saveUser(user: User) {
        val userData = mapOf(
            "id" to user.id,
            "name" to user.name,
            "email" to user.email,
            "phone" to user.phone,
            "gender" to user.gender,
            "position" to user.position,
            "createdAt" to user.createdAt
        )
        userCollection.document(user.id).set(userData).await()
    }

    /**
     * Update user
     */
    suspend fun updateUser(user: User) {
        val updateData = hashMapOf<String, Any>()
        updateData["name"] = user.name
        updateData["email"] = user.email
        updateData["phone"] = user.phone
        updateData["gender"] = user.gender
        updateData["position"] = user.position
        userCollection.document(user.id).update(updateData).await()
    }

    /**
     * Hapus user
     */
    suspend fun deleteUser(userId: String) {
        userCollection.document(userId).delete().await()
    }

    /**
     * Ambil semua user
     */
    suspend fun getAllUsers(): List<User> {
        val snapshot = userCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            try {
                com.example.omsetku.models.User.fromMap(data)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Mendapatkan daftar transaksi milik user saat ini (List<Transaction>)
     */
    suspend fun getUserTransactionsList(): List<Transaction> {
        val userId = getCurrentUserId()
        val snapshot = transactionCollection.whereEqualTo("userId", userId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            try {
                Transaction(
                    id = doc.id,
                    userId = data["userId"] as? String ?: "",
                    type = data["type"] as? String ?: "",
                    amount = (data["amount"] as? Number)?.toLong() ?: 0L,
                    date = (data["date"] as? Number)?.toLong() ?: 0L,
                    category = data["category"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}