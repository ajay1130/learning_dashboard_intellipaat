package com.example.learning_dashboard.data.repository

import com.example.learning_dashboard.data.local.CourseDao
import com.example.learning_dashboard.data.local.entity.CourseEntity
import com.example.learning_dashboard.data.local.entity.CourseSummaryRow
import com.example.learning_dashboard.data.local.entity.CourseWithLessons
import com.example.learning_dashboard.data.local.entity.LessonEntity
import com.example.learning_dashboard.data.remote.CourseApi
import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.domain.model.CourseDetail
import com.example.learning_dashboard.domain.model.Lesson
import com.example.learning_dashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultCourseRepository @Inject constructor(
    private val api: CourseApi,
    private val dao: CourseDao,
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> =
        dao.observeCourseSummaries().map { rows -> rows.map { it.toDomain() } }

    override fun observeCourse(courseId: Long): Flow<CourseDetail?> =
        dao.observeCourse(courseId).map { it?.toDomain() }

    override suspend fun refreshCourses() {
        val remote = api.getCourses()

        val courses = remote.mapIndexed { index, course ->
            CourseEntity(
                id = course.id,
                title = course.title,
                instructor = course.instructor,
                position = index,
            )
        }
        val lessons = remote.flatMap { course ->
            course.lessons.mapIndexed { index, lesson ->
                LessonEntity(
                    id = lesson.id,
                    courseId = course.id,
                    position = index,
                    title = lesson.title,
                    completed = lesson.completed,
                )
            }
        }
        dao.sync(courses, lessons)
    }

    override suspend fun markLessonCompleted(lessonId: Long) {
        dao.markLessonCompleted(lessonId)
    }
}

private fun CourseSummaryRow.toDomain() = Course(
    id = id,
    title = title,
    instructor = instructor,
    lessonCount = lessonCount,
    completedLessons = completedCount,
)

private fun CourseWithLessons.toDomain() = CourseDetail(
    id = course.id,
    title = course.title,
    instructor = course.instructor,
    lessons = lessons
        .sortedBy { it.position }
        .map { Lesson(id = it.id, title = it.title, isCompleted = it.completed) },
)
