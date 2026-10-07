package com.example.learning_dashboard.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_dashboard.domain.model.CourseDetail
import com.example.learning_dashboard.domain.usecase.course.MarkLessonCompletedUseCase
import com.example.learning_dashboard.domain.usecase.course.ObserveCourseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CourseDetailUiState {
    data object Loading : CourseDetailUiState
    data object NotFound : CourseDetailUiState
    data class Content(val course: CourseDetail) : CourseDetailUiState
}

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCourseDetail: ObserveCourseDetailUseCase,
    private val markLessonCompleted: MarkLessonCompletedUseCase,
) : ViewModel() {

    // Key matches the property name in CourseDetailDestination.
    private val courseId: Long = checkNotNull(savedStateHandle.get<Long>("courseId"))

    val uiState: StateFlow<CourseDetailUiState> = observeCourseDetail(courseId)
        .map { course ->
            if (course == null) CourseDetailUiState.NotFound else CourseDetailUiState.Content(course)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CourseDetailUiState.Loading)

    fun onMarkCompleted(lessonId: Long) {
        viewModelScope.launch { markLessonCompleted(lessonId) }
    }
}
