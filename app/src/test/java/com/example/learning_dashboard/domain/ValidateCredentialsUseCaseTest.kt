package com.example.learning_dashboard.domain

import com.example.learning_dashboard.domain.usecase.auth.ValidateCredentialsUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateCredentialsUseCaseTest {

    private val validate = ValidateCredentialsUseCase()

    @Test
    fun `accepts a well formed email with surrounding whitespace`() {
        assertTrue(validate("  demo@learn.com ", "password123").isValid)
    }

    @Test
    fun `flags each field independently`() {
        val result = validate("demo@learn", "12345")
        assertFalse(result.isEmailValid)
        assertFalse(result.isPasswordValid)

        val onlyPasswordBad = validate("demo@learn.com", "123")
        assertTrue(onlyPasswordBad.isEmailValid)
        assertFalse(onlyPasswordBad.isPasswordValid)
    }
}
