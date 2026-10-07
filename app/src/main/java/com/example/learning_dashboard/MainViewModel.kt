package com.example.learning_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_dashboard.domain.usecase.auth.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
) : ViewModel() {

    /** null until the stored session has been read. */
    val isLoggedIn: StateFlow<Boolean?> = observeSession()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
