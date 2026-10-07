package com.example.learning_dashboard.domain.usecase.course

import com.example.learning_dashboard.domain.model.CourseDetail
import com.example.learning_dashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCourseDetailUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
) {
    operator fun invoke(courseId: Long): Flow<CourseDetail?> = courseRepository.observeCourse(courseId)
}
