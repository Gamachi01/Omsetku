package com.example.omsetku.firebase

import android.net.Uri
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
    private val storageRef = storage.reference
    
    /**
     * Mendapatkan ID user saat ini yang sedang login
     */
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User belum login")
    }
    
    /**
     * Upload gambar ke Firebase Storage
     * @param uri Uri dari gambar lokal
     * @param folderPath Path folder di Firebase Storage (e.g. "product_images")
     * @return URL download dari gambar yang diupload
     */
    suspend fun uploadImage(uri: Uri, folderPath: String): String {
        val userId = getCurrentUserId()
        val fileName = UUID.randomUUID().toString()
        val imageRef = storageRef.child("$userId/$folderPath/$fileName.jpg")
        
        val uploadTask = imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }
    
    /**
     * Upload gambar profil user
     */
    suspend fun uploadProfileImage(uri: Uri): String {
        return uploadImage(uri, "profile_images")
    }
    
    /**
     * Upload logo bisnis
     */
    suspend fun uploadBusinessLogo(uri: Uri): String {
        return uploadImage(uri, "business_logos")
    }
    
    /**
     * Upload gambar produk
     */
    suspend fun uploadProductImage(uri: Uri): String {
        return uploadImage(uri, "product_images")
    }
    
    /**
     * Menghapus gambar dari Firebase Storage
     * @param downloadUrl URL download dari gambar yang akan dihapus
     */
    suspend fun deleteImage(downloadUrl: String) {
        val imageRef = storage.getReferenceFromUrl(downloadUrl)
        imageRef.delete().await()
    }
} 