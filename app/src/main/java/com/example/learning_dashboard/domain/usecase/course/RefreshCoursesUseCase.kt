package com.example.learning_dashboard.domain.usecase.course

import com.example.learning_dashboard.domain.repository.CourseRepository
import javax.inject.Inject

class RefreshCoursesUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
) {
    suspend operator fun invoke() = courseRepository.refreshCourses()
}
