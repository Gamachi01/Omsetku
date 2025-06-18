package com.example.omsetku.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Login dengan email dan password
     */
    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Login gagal")
    }

    /**
     * Register dengan email dan password
     */
    suspend fun register(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Registrasi gagal")
    }

    /**
     * Logout user
     */
    suspend fun logout() {
        auth.signOut()
    }

    /**
     * Mengecek apakah user sudah login
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Mendapatkan user saat ini
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Re-authenticate user dengan password saat ini
     */
    suspend fun reauthenticate(email: String, currentPassword: String) {
        val user = getCurrentUser() ?: throw Exception("User tidak ditemukan")
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()
    }

    /**
     * Update password user
     */
    suspend fun updatePassword(newPassword: String) {
        val user = getCurrentUser() ?: throw Exception("User tidak ditemukan")
        user.updatePassword(newPassword).await()
    }
} 