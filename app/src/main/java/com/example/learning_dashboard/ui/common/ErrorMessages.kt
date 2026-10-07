package com.example.learning_dashboard.ui.common

import androidx.annotation.StringRes
import com.example.learning_dashboard.R
import com.example.learning_dashboard.domain.model.InvalidCredentialsException
import java.io.IOException

@StringRes
fun Throwable.toMessageRes(): Int = when (this) {
    is InvalidCredentialsException -> R.string.error_invalid_credentials
    is IOException -> R.string.error_no_internet
    else -> R.string.error_generic
}

/** Message shown above cached content when a refresh fails. */
@StringRes
fun Throwable.toStaleDataMessageRes(): Int = when (this) {
    is IOException -> R.string.offline_banner
    else -> R.string.refresh_failed_banner
}
