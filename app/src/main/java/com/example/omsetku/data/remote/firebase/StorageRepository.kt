package com.example.omsetku.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Repository yang menangani operasi Firebase Storage
 */
class StorageRepository {
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
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
     * @param context Context untuk akses contentResolver
     * @return URL gambar yang sudah diupload
     */
    suspend fun uploadProductImage(imageUri: Uri, context: Context): String {
        try {
            // Simpan file hasil crop ke filesDir
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fileName = "product_${System.currentTimeMillis()}.png"
            val file = java.io.File(context.filesDir, fileName)
            val outputStream = java.io.FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            // Kembalikan path lokal file
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error saving image locally: ${e.message}", e)
            throw Exception("Gagal menyimpan gambar secara lokal: ${e.message}")
        }
    }

    /**
     * Upload logo bisnis ke Firebase Storage
     * @param imageUri Uri gambar yang akan diupload sebagai logo
     * @param context Context untuk akses contentResolver
     * @return URL gambar yang sudah diupload
     */
    suspend fun uploadBusinessLogo(imageUri: Uri, context: Context): String {
        try {
            val userId = getCurrentUserId()
            val filename = "${userId}_business_logo_${UUID.randomUUID()}"
            val fileRef = businessLogosRef.child(filename)

            // Upload gambar dari stream (support FileProvider URI)
            Log.d("StorageRepository", "URI yang dikirim (logo): $imageUri")
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e("StorageRepository", "InputStream null untuk URI (logo): $imageUri")
                throw Exception("Gagal membuka file logo. URI tidak valid atau file tidak ditemukan.")
            }
            fileRef.putStream(inputStream).await()
            inputStream.close()

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