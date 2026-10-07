package com.example.learning_dashboard.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>

    /** Throws [com.example.learning_dashboard.domain.model.InvalidCredentialsException] on bad credentials. */
    suspend fun login(email: String, password: String)

    suspend fun logout()
}
