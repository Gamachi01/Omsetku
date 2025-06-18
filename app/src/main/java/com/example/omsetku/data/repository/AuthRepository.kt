package com.example.omsetku.data.repository

import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user?.toDomain()
        if (user != null) Result.success(user) else Result.failure(Exception("User not found"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        user?.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(name).build())
        val domainUser = user?.toDomain(name)
        if (domainUser != null) Result.success(domainUser) else Result.failure(Exception("User not found"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}

fun FirebaseUser.toDomain(name: String? = null) = User(
    id = uid,
    name = name ?: displayName.orEmpty(),
    email = email.orEmpty(),
    businessName = ""
) 