package com.example.omsetku.domain.usecase

import com.example.omsetku.domain.model.User
import com.example.omsetku.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
} 