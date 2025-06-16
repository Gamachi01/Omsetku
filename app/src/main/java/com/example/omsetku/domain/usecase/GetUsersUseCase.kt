package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<List<User>> = repository.getUsers()
} 