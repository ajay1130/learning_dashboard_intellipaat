package com.example.learning_dashboard.domain.repository

import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.CourseDetail
import kotlinx.coroutines.flow.Flow

/**
 * The local database is the source of truth. Observers always read from it;
 * [refreshCourses] pulls from the network and writes into it.
 */
interface CourseRepository {
    fun observeCourses(): Flow<List<Course>>

    fun observeCourse(courseId: Long): Flow<CourseDetail?>

    /** Throws if the network call fails. Cached data is left untouched in that case. */
    suspend fun refreshCourses()

    suspend fun markLessonCompleted(lessonId: Long)
}
