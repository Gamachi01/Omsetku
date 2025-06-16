package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        return authRepository.register(name, email, password)
    }
} 