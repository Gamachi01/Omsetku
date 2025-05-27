package com.example.omsetku.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository yang menangani operasi Firebase Storage
 */
class StorageRepository {
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Referensi storage
    private val productImagesRef = storage.reference.child("product_images")
    private val businessLogosRef = storage.reference.child("business_logos")

    /**
     * Mendapatkan ID user saat ini yang sedang login
     */
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User belum login")
    }

    /**
     * Upload gambar produk ke Firebase Storage
     * @param imageUri Uri gambar yang akan diupload
     * @return URL gambar yang sudah diupload
     */
    suspend fun uploadProductImage(imageUri: Uri): String {
        try {
            val userId = getCurrentUserId()
            val filename = "${userId}_${UUID.randomUUID()}"
            val fileRef = productImagesRef.child(filename)

            // Upload gambar
            fileRef.putFile(imageUri).await()

            // Dapatkan URL download
            val downloadUrl = fileRef.downloadUrl.await()

            Log.d("StorageRepository", "Image uploaded successfully: $downloadUrl")
            return downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error uploading image: ${e.message}", e)
            throw Exception("Gagal mengupload gambar: ${e.message}")
        }
    }

    /**
     * Upload logo bisnis ke Firebase Storage
     * @param imageUri Uri gambar yang akan diupload sebagai logo
     * @return URL gambar yang sudah diupload
     */
    suspend fun uploadBusinessLogo(imageUri: Uri): String {
        try {
            val userId = getCurrentUserId()
            val filename = "${userId}_business_logo_${UUID.randomUUID()}"
            val fileRef = businessLogosRef.child(filename)

            // Upload gambar
            fileRef.putFile(imageUri).await()

            // Dapatkan URL download
            val downloadUrl = fileRef.downloadUrl.await()

            Log.d("StorageRepository", "Business logo uploaded successfully: $downloadUrl")
            return downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error uploading business logo: ${e.message}", e)
            throw Exception("Gagal mengupload logo bisnis: ${e.message}")
        }
    }

    /**
     * Menghapus gambar produk dari Firebase Storage
     * @param imageUrl URL gambar yang akan dihapus
     */
    suspend fun deleteProductImage(imageUrl: String) {
        try {
            if (imageUrl.isEmpty()) return

            // Ekstrak path dari URL
            val storageRef = storage.getReferenceFromUrl(imageUrl)

            // Hapus file
            storageRef.delete().await()

            Log.d("StorageRepository", "Image deleted successfully")
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error deleting image: ${e.message}", e)
            throw Exception("Gagal menghapus gambar: ${e.message}")
        }
    }
}