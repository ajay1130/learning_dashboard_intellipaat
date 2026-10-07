package com.example.learning_dashboard.domain.usecase.auth

import javax.inject.Inject

data class CredentialsValidation(
    val isEmailValid: Boolean,
    val isPasswordValid: Boolean,
) {
    val isValid: Boolean get() = isEmailValid && isPasswordValid
}

class ValidateCredentialsUseCase @Inject constructor() {

    operator fun invoke(email: String, password: String) = CredentialsValidation(
        isEmailValid = EMAIL_REGEX.matches(email.trim()),
        isPasswordValid = password.length >= MIN_PASSWORD_LENGTH,
    )

    companion object {
        const val MIN_PASSWORD_LENGTH = 6

        // android.util.Patterns isn't available in plain JVM tests, hence the local regex.
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
