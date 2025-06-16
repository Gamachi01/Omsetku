package com.example.omsetku.data.repository

import com.example.omsetku.data.local.UserDao
import com.example.omsetku.data.local.UserEntity
import com.example.omsetku.data.remote.model.UserDto
import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) : UserRepository {
    override fun getUsers(): Flow<List<User>> =
        userDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getUserById(id: String): Flow<User?> =
        userDao.getById(id).map { it?.toDomain() }

    override suspend fun addUser(user: User) {
        userDao.insert(user.toEntity())
        firestore.collection("users").document(user.id).set(user.toDto()).await()
    }

    override suspend fun updateUser(user: User) {
        userDao.insert(user.toEntity())
        firestore.collection("users").document(user.id).set(user.toDto()).await()
    }

    override suspend fun deleteUser(id: String) {
        val entity = userDao.getAll().firstOrNull()?.find { it.id == id }
        entity?.let { userDao.delete(it) }
        firestore.collection("users").document(id).delete().await()
    }
}

// Mapping extension
fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    businessName = businessName
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    businessName = businessName
)

fun User.toDto() = UserDto(
    id = id,
    name = name,
    email = email,
    businessName = businessName
) 