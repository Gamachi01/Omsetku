package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.repository.UserRepository

class DeleteUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: String) = repository.deleteUser(id)
} 