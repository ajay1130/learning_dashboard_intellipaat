package com.example.learning_dashboard.data.remote

import com.example.learning_dashboard.domain.model.InvalidCredentialsException
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

interface AuthApi {
    /** Returns an access token. */
    suspend fun login(email: String, password: String): String
}

class FakeAuthApi @Inject constructor(
    private val connectivity: ConnectivityChecker,
) : AuthApi {

    override suspend fun login(email: String, password: String): String {
        delay(1000)
        if (!connectivity.isOnline()) throw IOException("No internet connection")
        if (!email.equals(DEMO_EMAIL, ignoreCase = true) || password != DEMO_PASSWORD) {
            throw InvalidCredentialsException()
        }
        return UUID.randomUUID().toString()
    }

    companion object {
        const val DEMO_EMAIL = "demo@learn.com"
        const val DEMO_PASSWORD = "password123"
    }
}
