package com.example.learning_dashboard.domain.usecase.course

import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCoursesUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
) {
    operator fun invoke(): Flow<List<Course>> = courseRepository.observeCourses()
}
