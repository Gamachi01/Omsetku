package com.example.omsetku.firebase


/**
 * Singleton untuk menyediakan instance repository Firebase
 */
object FirebaseModule {
    // Lazy-initialized instances
    val authRepository by lazy { AuthRepository() }
    val firestoreRepository by lazy { FirestoreRepository() }
    val storageRepository by lazy { StorageRepository() }
}