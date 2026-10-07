package com.example.learning_dashboard.ui.dashboard

import com.example.learning_dashboard.MainDispatcherRule
import com.example.learning_dashboard.R
import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.CourseDetail
import com.example.learning_dashboard.domain.repository.AuthRepository
import com.example.learning_dashboard.domain.repository.CourseRepository
import com.example.learning_dashboard.domain.usecase.auth.LogoutUseCase
import com.example.learning_dashboard.domain.usecase.course.ObserveCoursesUseCase
import com.example.learning_dashboard.domain.usecase.course.RefreshCoursesUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val python = Course(1, "Python Programming", "John Smith", lessonCount = 8, completedLessons = 5)
    private val repository = FakeCourseRepository()

    private fun createViewModel() = DashboardViewModel(
        observeCourses = ObserveCoursesUseCase(repository),
        refreshCourses = RefreshCoursesUseCase(repository),
        logout = LogoutUseCase(NoOpAuthRepository),
    )

    @Test
    fun `offline refresh keeps showing cached courses with an offline message`() = runTest {
        repository.cache.value = listOf(python)
        repository.refreshError = IOException("airplane mode")

        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

        val state = viewModel.uiState.value
        assertTrue("expected Content but was $state", state is DashboardUiState.Content)
        state as DashboardUiState.Content
        assertEquals(listOf(python), state.courses)
        assertEquals(R.string.offline_banner, state.staleDataMessage)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `offline refresh with nothing cached shows an error`() = runTest {
        repository.refreshError = IOException("airplane mode")

        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

        assertEquals(DashboardUiState.Error(R.string.error_no_internet), viewModel.uiState.value)
    }

    @Test
    fun `shows loading until the first refresh finishes, then empty if there are no courses`() = runTest {
        val gate = CompletableDeferred<Unit>()
        repository.refreshGate = gate

        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)

        gate.complete(Unit)
        advanceUntilIdle()
        assertEquals(DashboardUiState.Empty, viewModel.uiState.value)
    }
}

private class FakeCourseRepository : CourseRepository {
    val cache = MutableStateFlow<List<Course>>(emptyList())
    var refreshError: Exception? = null
    var refreshGate: CompletableDeferred<Unit>? = null

    override fun observeCourses(): Flow<List<Course>> = cache

    override fun observeCourse(courseId: Long): Flow<CourseDetail?> = flowOf(null)

    override suspend fun refreshCourses() {
        refreshGate?.await()
        refreshError?.let { throw it }
    }

    override suspend fun markLessonCompleted(lessonId: Long) = Unit
}

private object NoOpAuthRepository : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = flowOf(true)
    override suspend fun login(email: String, password: String) = Unit
    override suspend fun logout() = Unit
}
