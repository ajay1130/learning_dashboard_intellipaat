package com.example.learning_dashboard.domain.usecase.auth

import com.example.learning_dashboard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /** Emits whether a user is currently logged in. */
    operator fun invoke(): Flow<Boolean> = authRepository.isLoggedIn
}
