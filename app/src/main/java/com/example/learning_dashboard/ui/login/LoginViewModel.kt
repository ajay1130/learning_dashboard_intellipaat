package com.example.learning_dashboard.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_dashboard.R
import com.example.learning_dashboard.domain.usecase.auth.LoginUseCase
import com.example.learning_dashboard.domain.usecase.auth.ValidateCredentialsUseCase
import com.example.learning_dashboard.ui.common.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateCredentials: ValidateCredentialsUseCase,
    private val login: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun onLoginClick() {
        val current = _uiState.value
        if (current.isLoading) return

        val validation = validateCredentials(current.email, current.password)
        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    emailError = R.string.error_invalid_email.takeUnless { validation.isEmailValid },
                    passwordError = R.string.error_short_password.takeUnless { validation.isPasswordValid },
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                login(current.email, current.password)
                // Navigation is driven by the session flow in MainActivity.
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.toMessageRes()) }
            }
        }
    }
}
