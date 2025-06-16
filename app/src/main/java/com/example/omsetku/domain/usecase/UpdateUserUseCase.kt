package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.updateUser(user)
} 