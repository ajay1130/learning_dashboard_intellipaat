package com.example.learning_dashboard.ui.dashboard

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.progressPercent
import com.example.learning_dashboard.domain.usecase.auth.LogoutUseCase
import com.example.learning_dashboard.domain.usecase.course.ObserveCoursesUseCase
import com.example.learning_dashboard.domain.usecase.course.RefreshCoursesUseCase
import com.example.learning_dashboard.ui.common.toMessageRes
import com.example.learning_dashboard.ui.common.toStaleDataMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Error(@StringRes val message: Int) : DashboardUiState

    /**
     * [staleDataMessage] is set when the last refresh failed and we're showing cached data.
     */
    data class Content(
        val courses: List<Course>,
        val isRefreshing: Boolean,
        @StringRes val staleDataMessage: Int? = null,
    ) : DashboardUiState {
        val totalLessons: Int get() = courses.sumOf { it.lessonCount }
        val completedLessons: Int get() = courses.sumOf { it.completedLessons }
        val overallProgress: Int get() = progressPercent(completedLessons, totalLessons)
    }
}

private sealed interface RefreshState {
    data object Idle : RefreshState
    data object Running : RefreshState
    data class Failed(@StringRes val message: Int, @StringRes val staleDataMessage: Int) : RefreshState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeCourses: ObserveCoursesUseCase,
    private val refreshCourses: RefreshCoursesUseCase,
    private val logout: LogoutUseCase,
) : ViewModel() {

    private val refreshState = MutableStateFlow<RefreshState>(RefreshState.Running)
    private var refreshJob: Job? = null

    val uiState: StateFlow<DashboardUiState> =
        combine(observeCourses(), refreshState) { courses, refresh ->
            if (courses.isNotEmpty()) {
                DashboardUiState.Content(
                    courses = courses,
                    isRefreshing = refresh is RefreshState.Running,
                    staleDataMessage = (refresh as? RefreshState.Failed)?.staleDataMessage,
                )
            } else {
                when (refresh) {
                    RefreshState.Running -> DashboardUiState.Loading
                    is RefreshState.Failed -> DashboardUiState.Error(refresh.message)
                    RefreshState.Idle -> DashboardUiState.Empty
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        if (refreshJob?.isActive == true) return

        refreshState.value = RefreshState.Running
        refreshJob = viewModelScope.launch {
            refreshState.value = try {
                refreshCourses()
                RefreshState.Idle
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RefreshState.Failed(e.toMessageRes(), e.toStaleDataMessageRes())
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch { logout() }
    }
}
