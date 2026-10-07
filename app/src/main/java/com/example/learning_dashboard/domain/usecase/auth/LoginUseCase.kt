package com.example.learning_dashboard.domain.usecase.auth

import com.example.learning_dashboard.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String) {
        authRepository.login(email.trim(), password)
    }
}
