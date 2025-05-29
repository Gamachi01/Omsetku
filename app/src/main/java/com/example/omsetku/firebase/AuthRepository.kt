package com.example.omsetku.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository yang menangani autentikasi menggunakan Firebase Auth
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Melakukan login dengan email dan password
     */
    suspend fun login(email: String, password: String): FirebaseUser {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { user ->
                        continuation.resume(user)
                    } ?: continuation.resumeWithException(
                        Exception("Login berhasil tapi user tidak ditemukan")
                    )
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    /**
     * Mendaftarkan user baru dengan email dan password
     */
    suspend fun register(email: String, password: String): FirebaseUser {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { user ->
                        continuation.resume(user)
                    } ?: continuation.resumeWithException(
                        Exception("Registrasi berhasil tapi user tidak ditemukan")
                    )
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    /**
     * Melakukan logout
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Mendapatkan user yang sedang login
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Memeriksa apakah user sedang login
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Mengirim email reset password
     */
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * Mengubah password user
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): FirebaseUser {
        return suspendCoroutine { continuation ->
            val user = auth.currentUser
            val email = user?.email

            if (user == null || email == null) {
                continuation.resumeWithException(Exception("User tidak ditemukan"))
                return@suspendCoroutine
            }

            // Re-authenticate user
            auth.signInWithEmailAndPassword(email, currentPassword)
                .addOnSuccessListener {
                    // Update password
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            continuation.resume(user)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(Exception("Gagal mengubah password: ${e.message}"))
                        }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(Exception("Password saat ini salah"))
                }
        }
    }
}